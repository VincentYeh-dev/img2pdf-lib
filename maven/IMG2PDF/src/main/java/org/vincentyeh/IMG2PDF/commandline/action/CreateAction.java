package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.vincentyeh.IMG2PDF.commandline.CheckHelpParser;
import org.vincentyeh.IMG2PDF.commandline.Configuration;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sequence;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.file.text.UTF8InputStream;
import org.vincentyeh.IMG2PDF.pdf.document.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.NameFormatter;

public class CreateAction extends AbstractAction {

    private static final String DEF_PDF_SIZE = "A4";
    private static final String DEF_PDF_ALIGN = "CENTER-CENTER";
    private static final String DEF_PDF_DIRECTION = "Vertical";
    private static final String DEFV_PDF_SORTBY = "NAME";
    private static final String DEFV_PDF_SEQUENCE = "INCREASE";
    private static final String DEFV_PDF_FILTER = "glob:*.{PNG,JPG}";

    protected final PageSize pdf_size;
    protected final PageAlign pdf_align;
    protected final PageDirection pdf_direction;
    protected final boolean pdf_auto_rotate;
    protected final Sortby pdf_sortby;
    protected final Sequence pdf_sequence;
    protected final String pdf_owner_password;
    protected final String pdf_user_password;
    protected final DocumentAccessPermission pdf_permission;
    protected final String pdf_destination;
//    protected final String list_destination;
    protected final File dst;
    protected final boolean debug;
    protected final boolean overwrite_tasklist;

    protected final File[] sources;
    //	protected final String str_path_filter;
    protected final FileFilterHelper ffh;

    private static final Option opt_help;

    static {
        opt_help = createOption("h", "help", "help_create");
    }

    public CreateAction(String[] args) throws UnrecognizedEnumException, ParseException {
        super(getLocaleOptions());

        CommandLine cmd = (new CheckHelpParser(opt_help)).parse(options, args);

        debug = cmd.hasOption("debug");
        overwrite_tasklist = cmd.hasOption("overwrite");

        pdf_size = PageSize.getByString(cmd.getOptionValue("pdf_size", DEF_PDF_SIZE));

        pdf_align = new PageAlign(cmd.getOptionValue("pdf_align", DEF_PDF_ALIGN));

        pdf_direction = PageDirection.getByString(cmd.getOptionValue("pdf_direction", DEF_PDF_DIRECTION));

        pdf_sortby = Sortby.getByString(cmd.getOptionValue("pdf_sortby", DEFV_PDF_SORTBY));

        pdf_sequence = Sequence.getByString(cmd.getOptionValue("pdf_sequence", DEFV_PDF_SEQUENCE));

        pdf_permission = new DocumentAccessPermission(cmd.getOptionValue("pdf_permission", "11"));

        pdf_auto_rotate = cmd.hasOption("pdf_auto_rotate");

        pdf_owner_password = cmd.getOptionValue("pdf_owner_password");
        pdf_user_password = cmd.getOptionValue("pdf_user_password");

        pdf_destination = cmd.getOptionValue("pdf_destination");

        String list_destination = cmd.getOptionValue("list_destination");
        dst = (new File(list_destination)).getAbsoluteFile();


        if (!overwrite_tasklist && dst.exists())
            throw new RuntimeException(String.format(Configuration.getResString("err_overwrite"), dst.getAbsolutePath()));

        try {
            ffh = new FileFilterHelper(cmd.getOptionValue("filter", DEFV_PDF_FILTER));
        } catch (UnsupportedOperationException e) {
            throw new RuntimeException(String.format(Configuration.getResString("err_filter"), e.getMessage()));
        }

        String[] str_sources = cmd.getOptionValues("source");
        if (str_sources == null) {
            str_sources = new String[0];
        }

        System.out.println(Configuration.getResString("source_folder_verifying"));

        ArrayList<File> verified_sources = new ArrayList<>();

        for (String str_source : str_sources) {
            File raw = (new File(str_source)).getAbsoluteFile();

            System.out.printf("\t[" + Configuration.getResString("common_verifying") + "] %s\n", raw.getAbsolutePath());

            System.out.print("\t");
            if (!raw.exists()) {
                System.err.printf(Configuration.getResString("err_filenotfound") + "\n", raw.getAbsolutePath());
                continue;
            } else if (raw.isDirectory()) {
                System.err.printf(Configuration.getResString("err_path_is_file") + "\n", raw.getAbsolutePath());
                continue;
            } else {
                System.out.printf("[" + Configuration.getResString("common_verified") + "] %s\n",
                        raw.getAbsolutePath());
            }
            verified_sources.add(raw);
        }
        sources = new File[verified_sources.size()];
        verified_sources.toArray(sources);

        System.out.printf("### " + Configuration.getResString("tasklist_config")
                        + " ###\n%s:%s\n%s:%s\n%s:%s\n%s:%s\n%s:%s\n%s:%s\n%s:%s############\n",
//				
                Configuration.getResString("arg_align"), pdf_align.toString(),
//				
                Configuration.getResString("arg_size"), pdf_size.toString(),
//				
                Configuration.getResString("arg_direction"), pdf_direction.toString(),
//				
                Configuration.getResString("arg_auto_rotate"), pdf_auto_rotate,
//				
                Configuration.getResString("arg_filter"), ffh.getOperator(),
//				
                Configuration.getResString("arg_tasklist_dst"), list_destination,
//				
                Configuration.getResString("arg_source"), dumpArrayString(sources)
//				
        );
    }

    @Override
    public void start() throws Exception {
//        TaskList tasks = dst.exists() ? new TaskList(dst) : new TaskList();
        TaskList tasks = new TaskList();

        for (File source : sources) {
            if (!source.exists())
                throw new FileNotFoundException(
                        String.format(Configuration.getResString("err_filenotfound"), source.getName()));
            tasks.addAll(importTasksFromTXT(source));
        }

        try {
            tasks.toXMLFile(dst);
            System.out.printf("[" + Configuration.getResString("common_exported") + "] %s\n", dst.getAbsolutePath());
        } catch (IOException e) {
            System.err.printf(Configuration.getResString("err_tasklist_create") + "\n", e.getMessage());
        }
    }

    protected TaskList importTasksFromTXT(File dirlist) throws IOException {
//        if (!dirlist.exists())
//            throw new FileNotFoundException(
//                    String.format(Configuration.getResString("err_filenotfound"), dirlist.getName()));

        UTF8InputStream uis = new UTF8InputStream(dirlist);
        BufferedReader reader = new BufferedReader(new InputStreamReader(uis.getInputStream(), StandardCharsets.UTF_8));

        TaskList tasks = new TaskList();

        System.out.printf(Configuration.getResString("import_from_list") + "\n", dirlist.getName());
        int line_counter = 0;
        String buf = "";
        while (buf != null) {
            buf = reader.readLine();
            line_counter++;
            if (buf != null && !buf.isEmpty()) {
                File dir = (new File(buf)).getAbsoluteFile();

                if (!dir.exists()) {
                    System.err.printf(Configuration.getResString("err_source_filenotfound") + "\n", dirlist.getName(),
                            line_counter, dir.getAbsolutePath());
                }

                if (dir.isFile()) {
                    System.err.printf(Configuration.getResString("err_source_path_is_file") + "\n", dirlist.getName(),
                            line_counter, dir.getAbsolutePath());
                }

                System.out.printf("\t[" + Configuration.getResString("common_importing") + "] %s\n",
                        dir.getAbsolutePath());

                tasks.add(parse2Task(dir));

                System.out.printf("\t[" + Configuration.getResString("common_imported") + "] %s\n",
                        dir.getAbsolutePath());

            }
        }

        try {
            uis.close();
        } catch (Exception ignore) {
        }

        try {
            reader.close();
        } catch (Exception ignore) {
        }

        return tasks;
    }

    private Task parse2Task(File source_directory) throws FileNotFoundException {
        HashMap<String, Object> configuration = new HashMap<>();

        NameFormatter nf = new NameFormatter(source_directory);
        configuration.put("pdf_permission", pdf_permission);
        configuration.put("pdf_destination", nf.format(pdf_destination));
        configuration.put("pdf_align", pdf_align);
        configuration.put("pdf_size", pdf_size);
        configuration.put("pdf_direction", pdf_direction);
        configuration.put("pdf_auto_rotate", pdf_auto_rotate);

        configuration.put("pdf_user_password", pdf_user_password);
        configuration.put("pdf_owner_password", pdf_owner_password);

        ArrayList<ImgFile> imgs = importImagesFile(source_directory);

        Collections.sort(imgs);
        if (debug) {
            System.out.println("@Debug");
            System.out.println("Sort Images:");
            for (ImgFile img : imgs) {
                System.out.println(img);
            }
            System.out.println();
        }

//        configuration.put("imgs", imgs);

        return new Task(configuration,imgs);
    }

    private ArrayList<ImgFile> importImagesFile(File source_directory) throws FileNotFoundException {
        if (debug) {
            System.out.println("@Debug");
            System.out.println("Import Images:");
        }
        ArrayList<ImgFile> imgs = new ArrayList<>();

        File[] files = source_directory.listFiles(ffh);

        if (files == null)
            files = new File[0];

        for (File f : files) {
            ImgFile img = new ImgFile(f.getAbsolutePath(), pdf_sortby, pdf_sequence);
            imgs.add(img);
            if (debug)
                System.out.println(img);
        }

        if (debug)
            System.out.println();

        return imgs;
    }

    private static <T> String dumpArrayString(T[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (array != null && array.length != 0) {
            sb.append(array[0].toString());
            for (int i = 1; i < array.length; i++) {
                sb.append(",");
                sb.append(array[i].toString());
            }
        }
        sb.append("]\n");
        return sb.toString();
    }

    private static Options getLocaleOptions() {

        Options options = new Options();
        Option opt_debug = createOption("d", "debug", "help_create_debug");
        Option opt_overwrite = createOption("ow", "overwrite", "help_create_overwrite_tasklist");

        Option opt_pdf_size = createEnumOption("pz", "pdf_size", "help_create_pdf_size", PageSize.class);
        Option opt_pdf_align = createArgOption("pa", "pdf_align", "help_create_pdf_align");
        Option opt_pdf_direction = createEnumOption("pdi", "pdf_direction", "help_create_pdf_direction",
                PageDirection.class);
        Option opt_pdf_auto_rotate = createOption("par", "pdf_auto_rotate", "help_create_pdf_auto_rotate");
        Option opt_pdf_sortby = createEnumOption("ps", "pdf_sortby", "help_create_pdf_sortby", Sortby.class);
        Option opt_pdf_sequence = createEnumOption("pseq", "pdf_sequence", "help_create_pdf_sequence", Sequence.class);
        Option opt_pdf_owner_password = createArgOption("popwd", "pdf_owner_password",
                "help_create_pdf_owner_password");
        Option opt_pdf_user_password = createArgOption("pupwd", "pdf_user_password", "help_create_pdf_user_password");

        Option opt_pdf_permission = createArgOption("pp", "pdf_permission", "help_create_pdf_permission");

        Option opt_pdf_destination = createArgOption("pdst", "pdf_destination", "help_create_pdf_destination");
        opt_pdf_destination.setRequired(true);

        Option opt_filter = createArgOption("f", "filter", "help_create_filter");

        Option opt_sources = createArgOption("src", "source", "help_create_source");
        opt_sources.setRequired(true);

        Option opt_list_destination = createArgOption("ldst", "list_destination", "help_create_list_destination");
        opt_list_destination.setRequired(true);

        options.addOption(opt_debug);
        options.addOption(opt_overwrite);
        options.addOption(opt_help);
        options.addOption(opt_pdf_size);
        options.addOption(opt_pdf_align);
        options.addOption(opt_pdf_direction);
        options.addOption(opt_pdf_auto_rotate);
        options.addOption(opt_pdf_sortby);
        options.addOption(opt_pdf_sequence);
        options.addOption(opt_pdf_owner_password);
        options.addOption(opt_pdf_user_password);
        options.addOption(opt_pdf_permission);
        options.addOption(opt_pdf_destination);
        options.addOption(opt_filter);
        options.addOption(opt_sources);
        options.addOption(opt_list_destination);

        Option opt_mode = new Option("m", "mode", true, "mode");
        options.addOption(opt_mode);

        return options;
    }

}
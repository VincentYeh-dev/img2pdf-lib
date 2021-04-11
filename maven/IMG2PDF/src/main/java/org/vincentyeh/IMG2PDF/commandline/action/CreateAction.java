package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.vincentyeh.IMG2PDF.commandline.parser.CheckHelpParser;
import org.vincentyeh.IMG2PDF.Configuration;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.vincentyeh.IMG2PDF.commandline.parser.PropertiesOption;
import org.vincentyeh.IMG2PDF.pdf.doc.DocumentArgument;
import org.vincentyeh.IMG2PDF.pdf.page.PageArgument;
import org.vincentyeh.IMG2PDF.util.FileFilterHelper;
import org.vincentyeh.IMG2PDF.pdf.doc.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.FileSorter;
import org.vincentyeh.IMG2PDF.util.FileChecker;
import org.vincentyeh.IMG2PDF.util.NameFormatter;

import static org.vincentyeh.IMG2PDF.util.FileSorter.Sequence;
import static org.vincentyeh.IMG2PDF.util.FileSorter.Sortby;

public class CreateAction extends AbstractAction {

    private static final String DEF_PDF_SIZE = "A4";
    private static final String DEF_PDF_ALIGN = "CENTER-CENTER";
    private static final String DEF_PDF_DIRECTION = "Portrait";
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
    protected final String pdf_dst;
    protected final File list_dst;
    protected final boolean debug;
    protected final boolean overwrite_tasklist;

    protected final File[] sources;
    protected final FileFilterHelper ffh;

    private static final Option opt_help;

    static {
        opt_help = PropertiesOption.getOption("h", "help", "help_create");
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

        pdf_dst = cmd.getOptionValue("pdf_destination");

        String list_destination = cmd.getOptionValue("list_destination");
        list_dst = (new File(list_destination)).getAbsoluteFile();

        if (!overwrite_tasklist && list_dst.exists())
            throw new RuntimeException(String.format(Configuration.getResString("err_overwrite"), list_dst.getAbsolutePath()));

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


//        Directory List:
        for (String str_source : str_sources) {
            File raw = (new File(str_source)).getAbsoluteFile();

            System.out.printf("\t[" + Configuration.getResString("common_verifying") + "] %s\n", raw.getAbsolutePath());

            System.out.print("\t");

            try {
                FileChecker.checkReadableFile(raw);

                System.out.printf("[" + Configuration.getResString("common_verified") + "] %s\n",
                        raw.getAbsolutePath());

                verified_sources.add(raw);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        sources = new File[verified_sources.size()];
        verified_sources.toArray(sources);

        System.out.printf("### " + Configuration.getResString("tasklist_config")
                        + " ###\n%s:%s\n%s:%s\n%s:%s\n%s:%s\n%s:%s\n%s:%s\n%s:%s############\n",
//
                Configuration.getResString("arg_align"), pdf_align,
//
                Configuration.getResString("arg_size"), pdf_size,
//
                Configuration.getResString("arg_direction"), pdf_direction,
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
        TaskList tasks = new TaskList();

        for (File source : sources) {
            FileChecker.checkExists(source);
            tasks.addAll(importTasksFromTXT(source));
        }

        try {
            tasks.toXMLFile(list_dst);
            System.out.printf("[" + Configuration.getResString("common_exported") + "] %s\n", list_dst.getAbsolutePath());
        } catch (IOException e) {
            System.err.printf(Configuration.getResString("err_tasklist_create") + "\n", e.getMessage());
        }
    }

    protected TaskList importTasksFromTXT(File dirlist) throws IOException {
        FileChecker.checkReadableFile(dirlist);
        TaskList tasks = new TaskList();
        System.out.printf(Configuration.getResString("import_from_list") + "\n", dirlist.getName());

        List<String> lines = Files.readAllLines(dirlist.toPath(), StandardCharsets.UTF_8);
        for (int line_counter = 0; line_counter < lines.size(); line_counter++) {
            String line = lines.get(line_counter);

//            Ignore BOM Header:
            line = line.replace("\uFEFF", "");

            if (line.trim().isEmpty() || line.isEmpty())
                continue;

            File dir = new File(line);

            if (!dir.isAbsolute()) {
                dir = new File(dirlist.getParent(), line);
            }


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

        return tasks;
    }

    private Task parse2Task(File source_directory) throws IOException {

        NameFormatter nf = new NameFormatter(source_directory);
        DocumentArgument documentArgument = new DocumentArgument(pdf_owner_password, pdf_user_password, pdf_permission, new File(nf.format(pdf_dst)));
        PageArgument pageArgument = new PageArgument();
        pageArgument.setAlign(pdf_align);
        pageArgument.setSize(pdf_size);
        pageArgument.setDirection(pdf_direction);
        pageArgument.setAutoRotate(pdf_auto_rotate);
        return new Task(documentArgument, pageArgument, importSortedImagesFiles(source_directory));
    }

    private File[] importSortedImagesFiles(File source_directory) {
        File[] files = source_directory.listFiles(ffh);
        if (files == null)
            files = new File[0];


        FileSorter sorter = new FileSorter(pdf_sortby, pdf_sequence);
        Arrays.sort(files, sorter);
        if (debug) {
            System.out.println("@Debug");
            System.out.println("Sort Images:");
            for (File img : files) {
                System.out.println("\t"+img);
            }
            System.out.println();
        }

        return files;
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
        Option opt_debug = PropertiesOption.getOption("d", "debug", "help_create_debug");
        Option opt_overwrite = PropertiesOption.getOption("ow", "overwrite", "help_create_overwrite_tasklist");

        Option opt_pdf_size = PropertiesOption.getArgumentOption("pz", "pdf_size", "help_create_pdf_size",listEnum(PageSize.class));
        Option opt_pdf_align = PropertiesOption.getArgumentOption("pa", "pdf_align", "help_create_pdf_align");
        Option opt_pdf_direction = PropertiesOption.getArgumentOption("pdi", "pdf_direction", "help_create_pdf_direction",
                listEnum(PageDirection.class));
        Option opt_pdf_auto_rotate = PropertiesOption.getOption("par", "pdf_auto_rotate", "help_create_pdf_auto_rotate");
        Option opt_pdf_sortby = PropertiesOption.getArgumentOption("ps", "pdf_sortby", "help_create_pdf_sortby", listEnum(Sortby.class));
        Option opt_pdf_sequence = PropertiesOption.getArgumentOption("pseq", "pdf_sequence", "help_create_pdf_sequence", listEnum(Sequence.class));
        Option opt_pdf_owner_password = PropertiesOption.getArgumentOption("popwd", "pdf_owner_password",
                "help_create_pdf_owner_password");
        Option opt_pdf_user_password = PropertiesOption.getArgumentOption("pupwd", "pdf_user_password", "help_create_pdf_user_password");

        Option opt_pdf_permission = PropertiesOption.getArgumentOption("pp", "pdf_permission", "help_create_pdf_permission");

        Option opt_pdf_destination = PropertiesOption.getArgumentOption("pdst", "pdf_destination", "help_create_pdf_destination");
        opt_pdf_destination.setRequired(true);

        Option opt_filter = PropertiesOption.getArgumentOption("f", "filter", "help_create_filter");

        Option opt_sources = PropertiesOption.getArgumentOption("src", "source", "help_create_source");
        opt_sources.setRequired(true);

        Option opt_list_destination = PropertiesOption.getArgumentOption("ldst", "list_destination", "help_create_list_destination");
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
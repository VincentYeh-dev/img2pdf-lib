package org.vincentyeh.IMG2PDF.commandline.action;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.vincentyeh.IMG2PDF.commandline.parser.CheckHelpParser;
import org.vincentyeh.IMG2PDF.Configuration;
import org.vincentyeh.IMG2PDF.commandline.action.exception.HelperException;
import org.vincentyeh.IMG2PDF.converter.ConversionListener;
import org.vincentyeh.IMG2PDF.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.pdf.doc.ImagesDocumentAdaptor;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.BytesSize;
import org.vincentyeh.IMG2PDF.util.FileChecker;

public class ConvertAction extends AbstractAction {

    protected final File[] tasklist_sources;
    protected final boolean open_when_complete;
    protected final boolean overwrite_output;
    private static final Option opt_help;

    static {
        opt_help = PropertiesOption.getOption("h", "help", "help_convert");
    }

    private final File tempFolder  ;
    private final long maxMainMemoryBytes;

    public ConvertAction(String[] args) throws ParseException, FileNotFoundException {
        super(getLocaleOptions());

        CommandLine cmd = (new CheckHelpParser(opt_help)).parse(options, args);

        if (cmd.hasOption("-h"))
            throw new HelperException(options);

        String[] str_sources = cmd.getOptionValues("tasklist_source");

        tempFolder=new File(cmd.getOptionValue("temp_folder",".org.vincentyeh.IMG2PDF.tmp"));
        tempFolder.mkdirs();

        maxMainMemoryBytes= BytesSize.parseString(cmd.getOptionValue("memory_max_usage","50MB")).getBytes();

        open_when_complete = cmd.hasOption("open_when_complete");
        overwrite_output = cmd.hasOption("overwrite");

        tasklist_sources = new File[str_sources.length];
        for (int i = 0; i < tasklist_sources.length; i++) {
            System.out.println(Configuration.getResString("source_tasklist_verifying"));
            tasklist_sources[i] = new File(str_sources[i]);

            System.out.println("\t[" + Configuration.getResString("common_verifying") + "] "
                    + tasklist_sources[i].getAbsolutePath());
            System.out.print("\t");
            if (!tasklist_sources[i].exists()) {
                throw new FileNotFoundException(String.format(Configuration.getResString("err_filenotfound"), tasklist_sources[i].getAbsolutePath()));
            } else if (tasklist_sources[i].isDirectory()) {
                throw new RuntimeException(String.format(Configuration.getResString("err_path_is_folder"), tasklist_sources[i].getAbsolutePath()));
            } else {
                System.out.println("[" + Configuration.getResString("common_verified") + "] "
                        + tasklist_sources[i].getAbsolutePath());
            }

        }
    }

    @Override
    public void start() throws Exception {
        System.out.println(Configuration.getResString("import_tasklists"));
        for (File src : tasklist_sources) {
            System.out.print(
                    "\t[" + Configuration.getResString("common_importing") + "] " + src.getAbsolutePath() + "\n");
            System.out.print("\t");

            TaskList tasks = new TaskList(src);

            System.out
                    .print("[" + Configuration.getResString("common_imported") + "] " + src.getAbsolutePath() + "\n");

            System.out.println(Configuration.getResString("common_start_conversion"));

            ExecutorService executor = Executors.newSingleThreadExecutor();
            for (Task task : tasks) {
                ImagesDocumentAdaptor result = null;
                File dst = task.getDocumentArgument().getDestination();
                if (!overwrite_output && dst.exists()) {
                    System.err.printf(Configuration.getResString("err_overwrite") + "\n", dst.getAbsolutePath());
                    continue;
                }

                try {
                    PDFConverter converter = new PDFConverter(task, maxMainMemoryBytes, tempFolder);
                    converter.setListener(listener);
                    Future<ImagesDocumentAdaptor> future = executor.submit(converter);
                    try {
                        result = future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        continue;
                    }

//                  TODO: merge to another class
                    FileChecker.makeParentDirsIfNotExists(dst);
//                  TODO: merge to another class
                    FileChecker.checkWritableFile(dst);
                    result.save();

                    if (open_when_complete) {
                        Desktop desktop = Desktop.getDesktop();

                        if (dst.exists())
                            try {
                                desktop.open(dst);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    System.err.println(e.getMessage());
                } finally {
                    if (result != null)
                        result.closeDocument();
                }

            }
            executor.shutdown();
        }

    }

    private static Options getLocaleOptions() {
        Options options = new Options();
        Option opt_tasklist_source = PropertiesOption.getArgumentOption("lsrc", "tasklist_source", "help_convert_tasklist_source");
        opt_tasklist_source.setRequired(true);

        Option opt_open_when_complete = PropertiesOption.getOption("o", "open_when_complete", "help_convert_open_when_complete");

        Option opt_overwrite = PropertiesOption.getOption("ow", "overwrite", "help_convert_overwrite_output");

//      TODO: change description
        Option opt_tmp_folder = PropertiesOption.getArgumentOption("tmp", "temp_folder", "help_convert_tmp_folder",".org.vincentyeh.IMG2PDF.tmp");
//      TODO: change description
        Option opt_max_memory_usage = PropertiesOption.getArgumentOption("mx", "memory_max_usage", "help_convert_memory_max_usage","50MB");

        options.addOption(opt_help);
        options.addOption(opt_tasklist_source);
        options.addOption(opt_tmp_folder);
        options.addOption(opt_max_memory_usage);
        options.addOption(opt_open_when_complete);
        options.addOption(opt_overwrite);

        Option opt_mode = new Option("m", "mode", true, "mode");
        options.addOption(opt_mode);

        return options;
    }

    private final ConversionListener listener = new ConversionListener() {
        private double perImg;
        private double progress = 0;

        @Override
        public void onConversionPreparing(Task task) {
            int size_of_imgs = task.getImgs().length;
            perImg = (10. / size_of_imgs);
            System.out.printf("\t###%s###\n", Configuration.getResString("pdf_conversion_task"));
            System.out.printf("\t%s:%s\n", Configuration.getResString("arg_pdf_dst"), task.getDocumentArgument().getDestination());
            System.out.printf("\t%s:%s\n", Configuration.getResString("common_name"),
                    new File(task.getDocumentArgument().getDestination().getName()));
            System.out.printf("\t%s->", Configuration.getResString("common_progress"));
            System.out.print("0%[");

        }

        @Override
        public void onConverting(int index) {
            progress += perImg;
            while (progress >= 1) {
                System.out.print("=");
                progress -= 1;
            }
        }

        @Override
        public void onConversionComplete() {
            System.out.print("]%100\n\n");

        }

        @Override
        public void onConversionFail(int index, Exception e) {
            System.out.print("CONVERSION FAIL]\n\n");
            System.err.println(e.getMessage());
//			e.printStackTrace();

        }

        @Override
        public void onImageReadFail(int index, IOException e) {
            System.out.print("IMAGE READ FAIL]\n\n");
            System.err.println(e.getMessage());
//			e.printStackTrace();
        }

    };
}

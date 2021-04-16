package org.vincentyeh.IMG2PDF.commandline.action;

import java.awt.Desktop;
import java.io.File;
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
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.action.exception.HelperException;
import org.vincentyeh.IMG2PDF.commandline.parser.HandledException;
import org.vincentyeh.IMG2PDF.commandline.parser.PropertiesOption;
import org.vincentyeh.IMG2PDF.converter.ConversionListener;
import org.vincentyeh.IMG2PDF.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.BytesSize;

public class ConvertAction extends AbstractAction {
    private static final String DEFAULT_TEMP_FOLDER = ".org.vincentyeh.IMG2PDF.tmp";
    private static final String DEFAULT_MAX_MEMORY_USAGE = "50MB";

    protected final File[] tasklist_sources;
    protected final boolean open_when_complete;
    protected final boolean overwrite_output;
    private static final Option opt_help;

    static {
        opt_help = PropertiesOption.getOption("h", "help", "convert.help");
    }

    private final File tempFolder;
    private final long maxMainMemoryBytes;

    public ConvertAction(String[] args) throws ParseException, HandledException {
        super(getLocaleOptions());

        CommandLine cmd = (new CheckHelpParser(opt_help)).parse(options, args);

        if (cmd.hasOption("-h"))
            throw new HelperException(options);

        String[] str_sources = cmd.getOptionValues("tasklist_source");

        tempFolder = new File(cmd.getOptionValue("temp_folder", DEFAULT_TEMP_FOLDER));
        tempFolder.mkdirs();

        try {
            maxMainMemoryBytes = BytesSize.parseString(cmd.getOptionValue("memory_max_usage", DEFAULT_MAX_MEMORY_USAGE)).getBytes();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            throw new HandledException(e, getClass());
        }

        open_when_complete = cmd.hasOption("open_when_complete");
        overwrite_output = cmd.hasOption("overwrite");
        try {
            tasklist_sources = verifyFiles(str_sources);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new HandledException(e, getClass());
        }

    }

    @Override
    public void start() throws Exception {
        System.out.println(SharedSpace.getResString("convert.import_tasklists"));
        for (File src : tasklist_sources) {
            System.out.print(
                    "\t[" + SharedSpace.getResString("public.info.importing") + "] " + src.getAbsolutePath() + "\n");
            System.out.print("\t");

            TaskList tasks = new TaskList(src);

            System.out
                    .print("[" + SharedSpace.getResString("public.info.imported") + "] " + src.getAbsolutePath() + "\n");

            System.out.println(SharedSpace.getResString("convert.start_conversion"));

            ExecutorService executor = Executors.newSingleThreadExecutor();
            for (Task task : tasks.getArray()) {
                File result;
                File dst = task.getDocumentArgument().getDestination();

                if (!overwrite_output && dst.exists()) {
                    System.err.printf(SharedSpace.getResString("public.err.overwrite") + "\n", dst.getAbsolutePath());
                    continue;
                }

                try {
                    PDFConverter converter = new PDFConverter(task, maxMainMemoryBytes, tempFolder);
                    converter.setListener(listener);
                    Future<File> future = executor.submit(converter);
                    try {
                        result = future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        continue;
                    }

                    if (open_when_complete) {
                        Desktop desktop = Desktop.getDesktop();

                        if (result.exists())
                            try {
                                desktop.open(result);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    System.err.println(e.getMessage());
                }

            }
            executor.shutdown();
        }

    }

    private static Options getLocaleOptions() {
        Options options = new Options();
        Option opt_tasklist_source = PropertiesOption.getArgumentOption("lsrc", "tasklist_source", "convert.arg.tasklist_source.help");
        opt_tasklist_source.setRequired(true);

        Option opt_open_when_complete = PropertiesOption.getOption("o", "open_when_complete", "convert.arg.open_when_complete.help");

        Option opt_overwrite = PropertiesOption.getOption("ow", "overwrite", "convert.arg.overwrite_output.help");

        Option opt_tmp_folder = PropertiesOption.getArgumentOption("tmp", "temp_folder", "convert.arg.tmp_folder.help", ".org.vincentyeh.IMG2PDF.tmp");
        Option opt_max_memory_usage = PropertiesOption.getArgumentOption("mx", "memory_max_usage", "convert.arg.memory_max_usage.help", "50MB");

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
            System.out.printf("\t###%s###\n", SharedSpace.getResString("convert.pdf_conversion_task"));
            System.out.printf("\t%s:%s\n", SharedSpace.getResString("create.arg.pdf_destination.name"), task.getDocumentArgument().getDestination());
            System.out.printf("\t%s:%s\n", SharedSpace.getResString("public.info.name"),
                    new File(task.getDocumentArgument().getDestination().getName()));
            System.out.printf("\t%s->", SharedSpace.getResString("public.info.progress"));
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
        }

        @Override
        public void onImageReadFail(int index, IOException e) {
            System.out.print("IMAGE READ FAIL]\n\n");
            System.err.println(e.getMessage());
        }

    };
}

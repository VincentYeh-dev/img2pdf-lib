package org.vincentyeh.IMG2PDF.commandline.action;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jdom2.Document;
import org.jdom2.input.DOMBuilder;
import org.vincentyeh.IMG2PDF.commandline.CustomConversionListener;
import org.vincentyeh.IMG2PDF.commandline.parser.CheckHelpParser;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.action.exception.HelperException;
import org.vincentyeh.IMG2PDF.commandline.parser.HandledException;
import org.vincentyeh.IMG2PDF.commandline.parser.PropertiesOption;
import org.vincentyeh.IMG2PDF.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.BytesSize;
import org.vincentyeh.IMG2PDF.util.FileChecker;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

        tempFolder = new File(cmd.getOptionValue("temp_folder", DEFAULT_TEMP_FOLDER)).getAbsoluteFile();
        try {
            FileChecker.makeDirsIfNotExists(tempFolder);
        } catch (IOException e) {
            e.printStackTrace();
            throw new HandledException(e, getClass());
        }

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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        System.out.println(SharedSpace.getResString("convert.import_tasklists"));
        try {
            for (File src : tasklist_sources) {
                System.out.print(
                        "\t[" + SharedSpace.getResString("public.info.importing") + "] " + src.getAbsolutePath() + "\r");
                TaskList tasks;
                try {
                    tasks = new TaskList(getDocumentFromFile(src));
                }catch (SAXException e){
//                    TODO: add to language pack
                    System.err.println("\n\tWrong XML content."+e.getMessage());
                    throw new HandledException(e,getClass());
                }catch (Exception e){
                    e.printStackTrace();
                    throw e;
                }
//                TODO:Add more catch

                System.out
                        .print("\t[" + SharedSpace.getResString("public.info.imported") + "] " + src.getAbsolutePath() + "\r\n");

                System.out.println();
                System.out.println(SharedSpace.getResString("convert.start_conversion"));
//                TODO:No exception is thrown when task.getArray() is empty.Warning to the user when it happen.
                for (Task task : tasks.getArray()) {
                    File result;
                    try {
                        PDFConverter converter = new PDFConverter(task, maxMainMemoryBytes, tempFolder, overwrite_output);
                        converter.setListener(new CustomConversionListener());
                        Future<File> future = executor.submit(converter);
                        result = future.get();
                    } catch (ExecutionException e) {
                        if ((e.getCause() instanceof HandledException)) {
                            continue;
                        } else {
                            throw e;
                        }
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
                }
            }
        } finally {
            executor.shutdown();
        }
    }

    private static Document getDocumentFromFile(final File file)
            throws ParserConfigurationException, SAXException, IOException {
        FileChecker.checkReadableFile(file);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // If want to make namespace aware.
        // factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();

//      Disable printing message to console
        documentBuilder.setErrorHandler(null);

        InputSource source = new InputSource(new InputStreamReader(new FileInputStream(file), SharedSpace.Configuration.DEFAULT_CHARSET));
        org.w3c.dom.Document w3cDocument = documentBuilder.parse(source);
        return new DOMBuilder().build(w3cDocument);
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

}

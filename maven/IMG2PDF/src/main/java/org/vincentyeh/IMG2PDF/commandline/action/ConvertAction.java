package org.vincentyeh.IMG2PDF.commandline.action;

import java.awt.Desktop;
import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jdom2.Document;
import org.jdom2.input.DOMBuilder;
import org.vincentyeh.IMG2PDF.commandline.CustomConversionListener;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.FileChecker;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ConvertAction implements Action{
    private final File tempFolder;
    private final long maxMainMemoryBytes;
    private final File[] tasklist_sources;
    private final boolean open_when_complete;
    private final boolean overwrite_output;

//  TODO:Only be closed start() is finish.
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ConvertAction(File tempFolder, long maxMainMemoryBytes, File[] tasklist_sources, boolean open_when_complete, boolean overwrite_output) {
        this.tempFolder = tempFolder;
        this.maxMainMemoryBytes = maxMainMemoryBytes;
        this.tasklist_sources = tasklist_sources;
        this.open_when_complete = open_when_complete;
        this.overwrite_output = overwrite_output;
    }

    @Override
    public void start() throws Exception {
        try {

            System.out.println(SharedSpace.getResString("convert.import_tasklists"));
            for (File src : tasklist_sources) {
                System.out.print(
                        "\t[" + SharedSpace.getResString("public.info.importing") + "] " + src.getAbsolutePath() + "\r");
                TaskList tasks = getTaskListFromFile(src);
                System.out.print("\t[" + SharedSpace.getResString("public.info.imported") + "] " + src.getAbsolutePath() + "\r\n");
                System.out.println();
                System.out.println(SharedSpace.getResString("convert.start_conversion"));
                convertList(tasks);
            }

        } finally {
            executor.shutdown();
        }
    }

    private void convertList(TaskList tasks) throws Exception {
//                TODO:No exception is thrown when task.getArray() is empty.Warning to the user when it happen.
        for (Task task : tasks.getArray()) {
            try {
                File result = convertToFile(task);

                if (open_when_complete)
                    openPDF(result);

            } catch (HandledException ignored) {
            }
        }
    }


    private TaskList getTaskListFromFile(File src) throws HandledException, ParserConfigurationException, IOException {
        try {
            return new TaskList(getDocumentFromFile(src));
        } catch (SAXException e) {
//                    TODO: add to language pack
            System.err.println("\n\tWrong XML content." + e.getMessage());
            throw new HandledException(e, getClass());
//                TODO:Add more catch
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void openPDF(File file) {
        Desktop desktop = Desktop.getDesktop();
        if (file.exists())
            try {
                desktop.open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    private File convertToFile(Task task) throws Exception {
        try {
            PDFConverter converter = new PDFConverter(task, maxMainMemoryBytes, tempFolder, overwrite_output);
            converter.setListener(new CustomConversionListener());
            Future<File> future = executor.submit(converter);
            return future.get();
        } catch (ExecutionException e) {
            if ((e.getCause() instanceof HandledException)) {
                throw new HandledException(e.getCause());
            } else {
                throw e;
            }
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


}

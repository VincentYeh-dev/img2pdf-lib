package org.vincentyeh.IMG2PDF.commandline.command;

import org.vincentyeh.IMG2PDF.commandline.converter.AbsoluteFileConverter;
import org.vincentyeh.IMG2PDF.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.converter.listener.DefaultConversionListener;
import org.vincentyeh.IMG2PDF.commandline.converter.ByteSizeConverter;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskListConverter;
import org.vincentyeh.IMG2PDF.util.BytesSize;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;
import picocli.CommandLine;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "convert")
public class ConvertCommand implements Callable<Integer> {

    public static class Configurations {
        private final Locale locale;
        private final Charset TASKLIST_READ_CHARSET;

        public Configurations(Locale locale, Charset tasklist_read_charset) {
            this.locale = locale;
            TASKLIST_READ_CHARSET = tasklist_read_charset;
        }
    }

    public ConvertCommand(ResourceBundle resourceBundle, Configurations configurations) {
        this.resourceBundle = resourceBundle;
        this.configurations = configurations;
    }

    private final ResourceBundle resourceBundle;
    private final Configurations configurations;

    @CommandLine.ParentCommand
    IMG2PDFCommand img2PDFCommand;

    @CommandLine.Option(names = {"--temp_folder", "-tmp"}, converter = AbsoluteFileConverter.class, defaultValue = ".org.vincentyeh.IMG2PDF.tmp")
    File tempFolder;

    @CommandLine.Option(names = {"--memory_max_usage", "-mx"}, defaultValue = "50MB", converter = ByteSizeConverter.class)
    BytesSize maxMainMemoryBytes;

    @CommandLine.Option(names = {"--open_when_complete", "-o"})
    boolean open_when_complete;

    @CommandLine.Option(names = {"--overwrite", "-ow"})
    boolean overwrite_output;

    @CommandLine.Parameters(arity = "1..*", converter = AbsoluteFileConverter.class)
    List<File> tasklist_sources;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true)
    boolean usageHelpRequested;

    @Override
    public Integer call() throws TaskListException, PDFConversionException {
        checkParameters();
        System.out.println(resourceBundle.getString("execution.convert.start.import_tasklists"));
        for (File src : tasklist_sources) {
            try {
                System.out.print(
                        "\t[" + resourceBundle.getString("public.importing") + "] " + src.getAbsolutePath() + "\r");
                List<Task> tasks = getTaskListFromFile(src);
                System.out.print("\t[" + resourceBundle.getString("public.imported") + "] " + src.getAbsolutePath() + "\r\n\n");
                System.out.println(resourceBundle.getString("execution.convert.start.start_conversion"));
                convertAllToFile(tasks);
            } finally {
                System.out.print("\n");
            }
        }

        return CommandLine.ExitCode.OK;
    }

    private void convertAllToFile(List<Task> tasks) throws PDFConversionException {

        for (Task task : tasks) {
            try {
                File result = convertToFile(task);
                if (open_when_complete)
                    openPDF(result);
            } catch (PDFConversionException e) {
                boolean ignore = false;
                if (ignore == false)
                    throw e;
            }
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

    private File convertToFile(Task task) throws PDFConversionException {
        try {
            PDFConverter converter = new PDFConverter(task, maxMainMemoryBytes.getBytes(), tempFolder, overwrite_output);
            converter.setInfoListener(new DefaultConversionListener(configurations.locale));

            return converter.start();
        } catch (Exception e) {
            throw new PDFConversionException(e, task);
        }

    }

    private List<Task> getTaskListFromFile(final File file)
            throws TaskListException {

        if (!file.exists())
            throw new TaskListException(new FileNotFoundException("File not found: " + file.getPath()), file);
        if (!file.isFile())
            throw new TaskListException(new WrongFileTypeException(WrongFileTypeException.Type.FILE, WrongFileTypeException.Type.FOLDER), file);

        try {
            String xml = String.join("\n", readAllLinesFromTasklist(file));
            List<Task> tasks = (new TaskListConverter()).parse(xml);
            if (tasks.isEmpty())
                throw new TaskListException(new NoTaskException("No task was found: " + file), file);
            return tasks;
        } catch (Exception e) {
            throw new TaskListException(e, file);
        }

    }

    private List<String> readAllLinesFromTasklist(File tasklist) throws IOException {
        return Files.readAllLines(tasklist.toPath(), configurations.TASKLIST_READ_CHARSET);
    }

    private void checkParameters() {
        printDebugLog("tempFolder=%s",true,tempFolder);
        if (tempFolder == null)
            throw new IllegalArgumentException("tempFolder==null");
        if (!tempFolder.isAbsolute())
            throw new IllegalArgumentException("tempFolder is not absolute: " + tempFolder);
        if (FileUtils.isRoot(tempFolder))
            throw new IllegalArgumentException("tempFolder is root: " + tempFolder);

        printDebugLog("maxMainMemoryBytes=%s",true,maxMainMemoryBytes.getBytes());
        if (maxMainMemoryBytes == null)
            throw new IllegalArgumentException("maxMainMemoryBytes==null");

        if (tasklist_sources == null || tasklist_sources.isEmpty())
            throw new IllegalArgumentException("sourceFiles==null");

        printDebugLog("sources:",true);
        for (File source : tasklist_sources) {
            printDebugLog("\t- %s",true,source);
            if (!source.isAbsolute())
                throw new IllegalArgumentException("source is not absolute: " + source);
            if (FileUtils.isRoot(source))
                throw new IllegalArgumentException("source is root: " + source);
        }
        printDebugLog("-----------------------",true);
    }

    private void printDebugLog(String msg, boolean nextLine, Object... objects) {
        if (img2PDFCommand.isDebug()) {
            System.out.printf(msg, objects);
            if (nextLine)
                System.out.println();
        }
    }

    public static class NoTaskException extends RuntimeException {
        public NoTaskException(String message) {
            super(message);
        }
    }

    public static class TaskListException extends Exception {

        private final File file;

        public TaskListException(Throwable cause, File file) {
            super(cause);
            this.file = file;
        }

        public File getTasklist() {
            return file;
        }
    }

    public static class PDFConversionException extends Exception {
        private final Task task;

        public PDFConversionException(Throwable cause, Task task) {
            super(cause);
            this.task = task;
        }

        public Task getTask() {
            return task;
        }
    }

    public static class WrongFileTypeException extends RuntimeException {

        public enum Type {
            FOLDER, FILE
        }

        private final Type expected;
        private final Type value;

        public Type getExpected() {
            return expected;
        }

        public Type getValue() {
            return value;
        }

        public WrongFileTypeException(Type expected, Type value) {
            super(value + "!=" + expected + "(expected)");
            this.expected = expected;
            this.value = value;
        }
    }

}

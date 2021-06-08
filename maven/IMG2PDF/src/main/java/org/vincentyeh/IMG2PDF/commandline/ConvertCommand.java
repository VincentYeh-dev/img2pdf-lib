package org.vincentyeh.IMG2PDF.commandline;

import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.converter.listener.DefaultConversionInfoListener;
import org.vincentyeh.IMG2PDF.commandline.converter.ByteSizeConverter;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskListConverter;
import org.vincentyeh.IMG2PDF.util.BytesSize;
import picocli.CommandLine;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "convert")
public class ConvertCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"--temp_folder", "-tmp"}, defaultValue = ".org.vincentyeh.IMG2PDF.tmp")
    File tempFolder;

    @CommandLine.Option(names = {"--memory_max_usage", "-mx"}, defaultValue = "50MB", converter = ByteSizeConverter.class)
    BytesSize maxMainMemoryBytes;

    @CommandLine.Option(names = {"--open_when_complete", "-o"})
    boolean open_when_complete;

    @CommandLine.Option(names = {"--overwrite", "-ow"})
    boolean overwrite_output;

    @CommandLine.Parameters
    List<File> tasklist_sources;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true)
    boolean usageHelpRequested;

    @Override
    public Integer call() throws TaskListException, PDFConversionException {
        System.out.println(SharedSpace.getResString("convert.import_tasklists"));
        for (File src : tasklist_sources) {
            try {
                System.out.print(
                        "\t[" + SharedSpace.getResString("public.info.importing") + "] " + src.getAbsolutePath() + "\r");
                List<Task> tasks = getTaskListFromFile(src);
                System.out.print("\t[" + SharedSpace.getResString("public.info.imported") + "] " + src.getAbsolutePath() + "\r\n\n");
                System.out.println(SharedSpace.getResString("convert.start_conversion"));
                convertAllToFile(tasks);
            } finally {
                System.out.print("\n");
            }
        }

        return 0;
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
            converter.setInfoListener(new DefaultConversionInfoListener());

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
        return Files.readAllLines(tasklist.toPath(), SharedSpace.Configuration.TASKLIST_READ_CHARSET);
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

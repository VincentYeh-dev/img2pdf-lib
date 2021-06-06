package org.vincentyeh.IMG2PDF.newcommandline;

import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.converter.exception.ConversionException;
import org.vincentyeh.IMG2PDF.converter.exception.OverwriteDenyException;
import org.vincentyeh.IMG2PDF.converter.exception.ReadImageException;
import org.vincentyeh.IMG2PDF.converter.listener.DefaultConversionInfoListener;
import org.vincentyeh.IMG2PDF.newcommandline.converter.ByteSizeConverter;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskListConverter;
import org.vincentyeh.IMG2PDF.util.BytesSize;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;
import picocli.CommandLine;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "convert")
public class ConvertCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"--temp_folder","-tmp"},defaultValue = ".org.vincentyeh.IMG2PDF.tmp")
    File tempFolder;

    @CommandLine.Option(names = {"--memory_max_usage","-mx"},defaultValue = "50MB",converter = ByteSizeConverter.class)
    BytesSize maxMainMemoryBytes;

    @CommandLine.Option(names = {"--open_when_complete","-o"})
    boolean open_when_complete;

    @CommandLine.Option(names = {"--overwrite","-ow"})
    boolean overwrite_output;

    @CommandLine.Parameters
    List<File> tasklist_sources;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true)
    boolean usageHelpRequested;

    @Override
    public Integer call() throws Exception {
        System.out.println(SharedSpace.getResString("convert.import_tasklists"));
        for (File src : tasklist_sources) {
            System.out.print(
                    "\t[" + SharedSpace.getResString("public.info.importing") + "] " + src.getAbsolutePath() + "\r");
            try {
                List<Task> tasks = getTaskListFromFile(src);
                System.out.print("\t[" + SharedSpace.getResString("public.info.imported") + "] " + src.getAbsolutePath() + "\r\n\n");
                System.out.println(SharedSpace.getResString("convert.start_conversion"));
                convertAllToFile(tasks);
            } catch (HandledException ignored) {
            }

        }

        return 0;
    }

    private void convertAllToFile(List<Task> tasks) throws Exception {
//                TODO:No exception is thrown when task.getArray() is empty.Warning to the user when it happen.
        for (Task task : tasks) {
            try {
                File result = convertToFile(task);
                if (open_when_complete)
                    openPDF(result);
            } catch (HandledException ignored) {
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

    private File convertToFile(Task task) throws IOException, HandledException {
        PDFConverter converter;
        try {
            converter = new PDFConverter(task, maxMainMemoryBytes.getBytes(), tempFolder, overwrite_output);
            converter.setInfoListener(new DefaultConversionInfoListener());
        } catch (IOException e) {
//            TODO:print error message
            throw new HandledException(e, getClass());
        }

        try {
            return converter.start();

        } catch (OverwriteDenyException e) {
            System.err.printf("\t" + SharedSpace.getResString("convert.listener.err.overwrite") + "\n", e.getFile().getAbsolutePath());
            throw new HandledException(e, getClass());
        } catch (ReadImageException e) {
            System.err.printf("\n\t\t" + SharedSpace.getResString("convert.listener.err.image") + "\n", e.getMessage());
            throw new HandledException(e, getClass());
        } catch (ConversionException e) {
            e.printStackTrace();
            System.err.printf("\n\t\t" + SharedSpace.getResString("convert.listener.err.conversion") + "\n", e.getMessage());
            throw new HandledException(e, getClass());
        }

    }

    private List<Task> getTaskListFromFile(final File file)
            throws HandledException {
        try {
            FileUtils.checkAbsolute(file);
            FileUtils.checkExists(file);
            FileUtils.checkIsFile(file);
        } catch (FileUtils.WrongTypeException e) {
//            TODO:Print error message
            throw new HandledException(e, getClass());
        } catch (FileUtils.PathNotAbsoluteException e) {
//            TODO:Print error message
            throw new HandledException(e, getClass());
        } catch (FileNotFoundException e) {
//            TODO:Print error message
            throw new HandledException(e, getClass());
        }
        try {
            String xml = String.join("\n", readAllLinesFromTasklist(file));
            return (new TaskListConverter()).parse(xml);
        } catch (com.thoughtworks.xstream.converters.ConversionException e) {
            System.err.println(e.getCause().getMessage());
            throw new HandledException(e, getClass());
        }

    }

    private List<String> readAllLinesFromTasklist(File dirlist) throws HandledException {
        List<String> lines;
        try {
            lines = Files.readAllLines(dirlist.toPath(), SharedSpace.Configuration.TASKLIST_READ_CHARSET);
        } catch (UnmappableCharacterException | MalformedInputException e) {
//            TODO:wrong charset,print error message.
            System.err.println("Unmappable character or Malformed input,maybe cause by wrong charset." + e.getMessage());
            throw new HandledException(e, getClass());
        } catch (IOException e) {
            e.printStackTrace();
            throw new HandledException(e, getClass());
        }
        return lines;
    }

}

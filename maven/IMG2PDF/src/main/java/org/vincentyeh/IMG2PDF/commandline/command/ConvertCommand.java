package org.vincentyeh.IMG2PDF.commandline.command;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.fusesource.jansi.Ansi;
import org.vincentyeh.IMG2PDF.commandline.converter.*;
import org.vincentyeh.IMG2PDF.pdf.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.pdf.converter.exception.PDFConverterException;
import org.vincentyeh.IMG2PDF.pdf.converter.listener.DefaultConversionListener;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageSize;
import org.vincentyeh.IMG2PDF.task.DocumentArgument;
import org.vincentyeh.IMG2PDF.task.PageArgument;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.factory.DirlistTaskFactory;
import org.vincentyeh.IMG2PDF.util.BytesSize;
import org.vincentyeh.IMG2PDF.util.file.FileSorter;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;
import org.vincentyeh.IMG2PDF.util.file.GlobbingFileFilter;
import picocli.CommandLine;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;

import static org.fusesource.jansi.Ansi.ansi;

@CommandLine.Command(name = "convert")
public class ConvertCommand implements Callable<Integer> {

    @CommandLine.ParentCommand
    IMG2PDFCommand img2PDFCommand;

    @CommandLine.Option(names = {"--sorter", "-sr"}, defaultValue = "NAME$INCREASE", converter = FileSorterConverter.class)
    FileSorter fileSorter;

    @CommandLine.Option(names = {"--filter", "-f"}, defaultValue = "glob:*.{PNG,JPG}", converter = GlobbingFileFilterConverter.class)
    GlobbingFileFilter filter;

    @CommandLine.Option(names = {"--pdf_owner_password", "-popwd"})
    String pdf_owner_password;

    @CommandLine.Option(names = {"--pdf_user_password", "-pupwd"})
    String pdf_user_password;

    @CommandLine.Option(names = {"--pdf_permission", "-pp"}, defaultValue = "255", converter = AccessPermissionConverter.class)
    AccessPermission pdf_permission;

    @CommandLine.Option(names = {"--pdf_align", "-pa"}, defaultValue = "CENTER-CENTER", converter = PageAlignConverter.class)
    PageAlign pdf_align;

    @CommandLine.Option(names = {"--pdf_size", "-pz"}, required = true)
    PageSize pdf_size;

    @CommandLine.Option(names = {"--pdf_direction", "-pdi"}, defaultValue = "Portrait")
    PageDirection pdf_direction;

    @CommandLine.Option(names = {"--pdf_auto_rotate", "-par"})
    boolean pdf_auto_rotate;

    @CommandLine.Option(names = {"--pdf_destination", "-pdst"}, required = true)
    String pdf_dst;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true)
    boolean usageHelpRequested;

    @CommandLine.Option(names = {"--temp_folder", "-tmp"}, converter = AbsoluteFileConverter.class, defaultValue = ".org.vincentyeh.IMG2PDF.tmp")
    File tempFolder;

    @CommandLine.Option(names = {"--memory_max_usage", "-mx"}, defaultValue = "50MB", converter = ByteSizeConverter.class)
    BytesSize maxMainMemoryBytes;

    @CommandLine.Option(names = {"--open_when_complete", "-o"})
    boolean open_when_complete;

    @CommandLine.Option(names = {"--overwrite", "-ow"})
    boolean overwrite_output;

    @CommandLine.Parameters(arity = "1..*", converter = AbsoluteFileConverter.class)
    List<File> sourceFiles;


    private final Configurations configurations;

    public static class Configurations {
        public final Locale locale;
        public final Charset DIRLIST_READ_CHARSET;
        public final ResourceBundle resourceBundle;

        public Configurations(Locale locale, Charset dirlist_read_charset, ResourceBundle resourceBundle) {
            this.locale = locale;
            DIRLIST_READ_CHARSET = dirlist_read_charset;
            this.resourceBundle = resourceBundle;
        }
    }

    public ConvertCommand(Configurations create_config) {
        this.configurations = create_config;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println();

        checkParameters();

        DirlistTaskFactory.setArgument(getDocumentArgument(), getPageArgument(), pdf_dst);
        DirlistTaskFactory.setImageFilesRule(filter, fileSorter);

        List<Task> tasks = new ArrayList<>();

        for (File dirlist : sourceFiles) {
            tasks.addAll(DirlistTaskFactory.createFromDirlist(dirlist, configurations.DIRLIST_READ_CHARSET));
        }

        try {
            System.out.println(configurations.resourceBundle.getString("execution.convert.start.start_conversion"));
            convertAllToFile(tasks);
        } finally {
            System.out.print("\n");
        }

        return CommandLine.ExitCode.OK;
    }

    private void checkParameters() throws NoSuchFieldException, IllegalAccessException {
        printDebugLog("-------------------------------------", true);
        printDebugLog("@|yellow Check Parameters|@", true);
        printDebugLog("-------------------------------------", true);
        checkPrintNullParameter("overwrite_output");
        checkPrintNullParameter("fileSorter");
        checkPrintNullParameter("filter");
        checkPrintNullParameter("pdf_align");
        checkPrintNullParameter("pdf_size");
        checkPrintNullParameter("pdf_direction");
        checkPrintNullParameter("pdf_dst");
        checkPrintNullParameter("open_when_complete");
        checkPrintNullParameter("tempFolder");
        checkPrintNullParameter("maxMainMemoryBytes");
        checkPrintNullParameter("sourceFiles");

        for (File source : sourceFiles) {
            if (!source.isAbsolute())
                throw new IllegalArgumentException("source is not absolute: " + source);
            if (FileUtils.isRoot(source))
                throw new IllegalArgumentException("source is root: " + source);
        }

        if (tempFolder == null)
            throw new IllegalArgumentException("tempFolder==null");
        if (!tempFolder.isAbsolute())
            throw new IllegalArgumentException("tempFolder is not absolute: " + tempFolder);
        if (FileUtils.isRoot(tempFolder))
            throw new IllegalArgumentException("tempFolder is root: " + tempFolder);
        printDebugLog("-------------------------------------", true);
        printDebugLog("-------------------------------------", true);
    }

    private PageArgument getPageArgument() {
        PageArgument.Builder builder = new PageArgument.Builder();
        builder.setAlign(pdf_align);
        builder.setDirection(pdf_direction);
        builder.setSize(pdf_size);
        builder.setAutoRotate(pdf_auto_rotate);
        return builder.build();
    }

    private DocumentArgument getDocumentArgument() {
        DocumentArgument.Builder builder = new DocumentArgument.Builder();
        builder.setOwnerPassword(pdf_owner_password);
        builder.setUserPassword(pdf_user_password);
        builder.setAccessPermission(pdf_permission);
        return builder.build();
    }


    private void convertAllToFile(List<Task> tasks) throws IOException {

        for (Task task : tasks) {
            try {
                File result = convertToFile(task);
                if (open_when_complete)
                    openPDF(result);
            } catch (PDFConverterException e) {
                System.err.println(e.getMessage());
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

    private File convertToFile(Task task) throws PDFConverterException, IOException{
        PDFConverter converter = new PDFConverter(task, maxMainMemoryBytes.getBytes(), tempFolder, overwrite_output);
        converter.setInfoListener(new DefaultConversionListener(configurations.locale));

        return converter.start();

    }


    private void printDebugLog(String msg, boolean nextLine) {
        if (img2PDFCommand.isDebug()) {
            String content = String.format("[@|green DEBUG|@] %s", msg);
            System.out.print(ansi().render(content));
            if (nextLine)
                System.out.println();
        }
    }

    private void printErrorLog(String msg, boolean nextLine) {
        if (img2PDFCommand.isDebug()) {
            String content = String.format("[@|red ERROR|@] %s", msg);
            System.out.print(ansi().render(content));
            if (nextLine)
                System.out.println();
        }
    }


    private String getKeyValuePairString(String key, Object value, Ansi.Color colorOfValue) {
        return String.format("@|yellow %s|@=@|%s %s|@", key, colorOfValue, value);
    }

    private String getKeyValuePairString(String field_name) throws NoSuchFieldException, IllegalAccessException {
        Field field = ConvertCommand.class.getDeclaredField(field_name);
        field.setAccessible(true);
        return getKeyValuePairString(field.getName(), field.get(this), Ansi.Color.GREEN);
    }

    private void checkPrintNullParameter(String field_name) throws NoSuchFieldException, IllegalAccessException {
        if (checkNullParameter(field_name)) {
            printDebugLog(getKeyValuePairString(field_name), true);
        } else {
            printErrorLog(getKeyValuePairString(field_name, null, Ansi.Color.RED), true);
            throw new IllegalArgumentException(field_name + "==null");
        }

    }

    private boolean checkNullParameter(String field_name) throws NoSuchFieldException, IllegalAccessException {
        Field field = ConvertCommand.class.getDeclaredField(field_name);
        return (field.get(this) != null);
    }
}
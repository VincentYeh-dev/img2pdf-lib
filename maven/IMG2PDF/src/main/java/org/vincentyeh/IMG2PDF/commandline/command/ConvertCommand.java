package org.vincentyeh.IMG2PDF.commandline.command;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.vincentyeh.IMG2PDF.commandline.converter.*;
import org.vincentyeh.IMG2PDF.commandline.exception.PDFConversionException;
import org.vincentyeh.IMG2PDF.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.converter.listener.DefaultConversionListener;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

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

        public Configurations(Locale locale,Charset dirlist_read_charset, ResourceBundle resourceBundle) {
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

    private void printDebugLog(String msg, boolean nextLine,Object... objects) {
        if (img2PDFCommand.isDebug()) {
            System.out.printf(msg,objects);
            if (nextLine)
                System.out.println();
        }
    }
    private void checkParameters() {
        printDebugLog("fileSorter=%s", true, fileSorter);
        if (fileSorter == null)
            throw new IllegalArgumentException("fileSorter == null");

        printDebugLog("filter=%s", true, filter.getOperator());
        if (filter == null)
            throw new IllegalArgumentException("filter == null");

        printDebugLog("pdf_align=%s", true, pdf_align);
        if (pdf_align == null)
            throw new IllegalArgumentException("pdf_align == null");

        printDebugLog("pdf_size=%s", true, pdf_size);
        if (pdf_size == null)
            throw new IllegalArgumentException("pdf_size == null");

        printDebugLog("pdf_direction=%s", true, pdf_direction);
        if (pdf_direction == null)
            throw new IllegalArgumentException("pdf_direction == null");

        printDebugLog("pdf_dst=%s", true, pdf_dst);
        if (pdf_dst == null)
            throw new IllegalArgumentException("pdf_dst == null");

        if (sourceFiles == null || sourceFiles.isEmpty())
            throw new IllegalArgumentException("sourceFiles==null");

        printDebugLog("sources:", true);
        for (File source : sourceFiles) {
            printDebugLog("\t- %s", true, source);
            if (!source.isAbsolute())
                throw new IllegalArgumentException("source is not absolute: " + source);
            if (FileUtils.isRoot(source))
                throw new IllegalArgumentException("source is root: " + source);
        }
        printDebugLog("-----------------------", true);

        printDebugLog("tempFolder=%s", true, tempFolder);
        if (tempFolder == null)
            throw new IllegalArgumentException("tempFolder==null");
        if (!tempFolder.isAbsolute())
            throw new IllegalArgumentException("tempFolder is not absolute: " + tempFolder);
        if (FileUtils.isRoot(tempFolder))
            throw new IllegalArgumentException("tempFolder is root: " + tempFolder);

        printDebugLog("maxMainMemoryBytes=%s", true, maxMainMemoryBytes.getBytes());
        if (maxMainMemoryBytes == null)
            throw new IllegalArgumentException("maxMainMemoryBytes==null");

        printDebugLog("-----------------------", true);
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
}
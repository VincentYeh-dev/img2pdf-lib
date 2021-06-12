package org.vincentyeh.IMG2PDF.commandline.command;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.vincentyeh.IMG2PDF.commandline.converter.*;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.task.DocumentArgument;
import org.vincentyeh.IMG2PDF.task.PageArgument;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskListConverter;
import org.vincentyeh.IMG2PDF.task.factory.DirlistTaskFactory;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;
import org.vincentyeh.IMG2PDF.util.file.GlobbingFileFilter;
import org.vincentyeh.IMG2PDF.util.file.FileSorter;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "create")
public class CreateCommand implements Callable<Integer> {
    public static class Configurations {
        private final Charset TASKlIST_WRITE_CHARSET;
        private final Charset DIRLIST_READ_CHARSET;
        private final Locale locale;
        public Configurations(Locale locale, Charset TaskListWriteCharset, Charset DirlistReadCharset) {
            this.locale=locale;
            TASKlIST_WRITE_CHARSET = TaskListWriteCharset;
            DIRLIST_READ_CHARSET = DirlistReadCharset;
        }
    }

    public CreateCommand(ResourceBundle resourceBundle, Configurations configurations) {
        this.resourceBundle = resourceBundle;
        this.configurations=configurations;
    }

    private final ResourceBundle resourceBundle;
    private final Configurations configurations;


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

    @CommandLine.Option(names = {"--list_destination", "-ldst"}, converter = AbsoluteFileConverter.class, required = true)
    File tasklist_dst;

    @CommandLine.Option(names = {"--overwrite", "-ow"})
    boolean overwrite;

    @CommandLine.Parameters(arity = "1..*", converter = AbsoluteFileConverter.class)
    List<File> sourceFiles;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true)
    boolean usageHelpRequested;

    @Override
    public Integer call() throws Exception {

        checkParameters();

        if (!overwrite && tasklist_dst.exists()) {
            throw new OverwriteTaskListException(tasklist_dst);
        }

        DirlistTaskFactory.setArgument(getDocumentArgument(), getPageArgument(), pdf_dst);
        DirlistTaskFactory.setImageFilesRule(filter, fileSorter);

        List<Task> tasks = new ArrayList<>();

        for (File dirlist : sourceFiles) {
            tasks.addAll(DirlistTaskFactory.createFromDirlist(dirlist,configurations.DIRLIST_READ_CHARSET));
        }

        save(tasks, tasklist_dst);

        return CommandLine.ExitCode.OK;
    }

    private void checkParameters() {
        printDebugLog("fileSorter=%s",true,fileSorter);
        if (fileSorter == null)
            throw new IllegalArgumentException("fileSorter == null");


        printDebugLog("filter=%s",true,filter.getOperator());
        if (filter == null)
            throw new IllegalArgumentException("filter == null");

        printDebugLog("pdf_align=%s",true,pdf_align);
        if (pdf_align == null)
            throw new IllegalArgumentException("pdf_align == null");

        printDebugLog("pdf_size=%s",true,pdf_size);
        if (pdf_size == null)
            throw new IllegalArgumentException("pdf_size == null");

        printDebugLog("pdf_direction=%s",true,pdf_direction);
        if (pdf_direction == null)
            throw new IllegalArgumentException("pdf_direction == null");

        printDebugLog("pdf_dst=%s",true,pdf_dst);
        if (pdf_dst == null)
            throw new IllegalArgumentException("pdf_dst == null");

        printDebugLog("tasklist_dst=%s",true,tasklist_dst);
        if (tasklist_dst == null)
            throw new IllegalArgumentException("tasklist_dst==null");

        if (!tasklist_dst.isAbsolute())
            throw new IllegalArgumentException("tasklist is not absolute: " + tasklist_dst);

        if (FileUtils.isRoot(tasklist_dst))
            throw new IllegalArgumentException("tasklist is root: " + tasklist_dst);

        if (sourceFiles == null || sourceFiles.isEmpty())
            throw new IllegalArgumentException("sourceFiles==null");

        printDebugLog("sources:",true);
        for (File source : sourceFiles) {
            printDebugLog("\t- %s",true,source);
            if (!source.isAbsolute())
                throw new IllegalArgumentException("source is not absolute: " + source);
            if (FileUtils.isRoot(source))
                throw new IllegalArgumentException("source is root: " + source);
        }
        printDebugLog("-----------------------",true);
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

    public void save(List<Task> taskList, File destination) throws SaveException {

        String content = new TaskListConverter().toXml(taskList);
        try {
            FileUtils.getParentFile(destination).mkdirs();

            writeStringToFile(destination, content, configurations.TASKlIST_WRITE_CHARSET);

            System.out.printf("[" + resourceBundle.getString("public.exported") + "] %s\n", tasklist_dst.getAbsolutePath());
        } catch (IOException | FileUtils.NoParentException e) {
            throw new SaveException(e, destination);
        }

    }

    private void writeStringToFile(File file, String content, Charset charset) throws IOException {
        List<String> contents = new ArrayList<>();
        contents.add(content);
        Files.write(file.toPath(), contents, charset);
    }

    public static class OverwriteTaskListException extends Exception {
        private final File file;

        public OverwriteTaskListException(File file) {
            super("Overwrite deny: " + file.getAbsolutePath());
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }

    public static class SaveException extends Exception {
        private final File file;

        public SaveException(Throwable cause, File file) {
            super(cause);
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }

    private void printDebugLog(String msg, boolean nextLine,Object... objects) {
        if (img2PDFCommand.isDebug()) {
            System.out.printf(msg,objects);
            if (nextLine)
                System.out.println();
        }
    }
}

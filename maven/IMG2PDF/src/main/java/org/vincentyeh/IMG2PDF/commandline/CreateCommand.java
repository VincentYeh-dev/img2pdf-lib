package org.vincentyeh.IMG2PDF.commandline;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.converter.AccessPermissionConverter;
import org.vincentyeh.IMG2PDF.commandline.converter.FileSorterConverter;
import org.vincentyeh.IMG2PDF.commandline.converter.GlobbingFileFilterConverter;
import org.vincentyeh.IMG2PDF.commandline.converter.PageAlignConverter;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.task.DocumentArgument;
import org.vincentyeh.IMG2PDF.task.PageArgument;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskListConverter;
import org.vincentyeh.IMG2PDF.task.factory.DirlistTaskFactory;
import org.vincentyeh.IMG2PDF.task.factory.exception.DirListException;
import org.vincentyeh.IMG2PDF.task.factory.exception.SourceFileException;
import org.vincentyeh.IMG2PDF.util.file.GlobbingFileFilter;
import org.vincentyeh.IMG2PDF.util.file.FileSorter;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "create")
public class CreateCommand implements Callable<Integer> {

    @CommandLine.ParentCommand
    IMG2PDFCommand img2PDFCommand;

    @CommandLine.Option(names = {"--sorter", "-sr"}, defaultValue = "NAME$INCREASE", converter = FileSorterConverter.class)
    FileSorter fileSorter;

    @CommandLine.Option(names = {"--filter", "-f"}, defaultValue = "glob:*.{PNG,JPG}",converter = GlobbingFileFilterConverter.class)
    GlobbingFileFilter filter;

    @CommandLine.Option(names = {"--pdf_owner_password", "-popwd"})
    String pdf_owner_password;
    @CommandLine.Option(names = {"--pdf_user_password", "-pupwd"} )
    String pdf_user_password;

    @CommandLine.Option(names = {"--pdf_permission", "-pp"},defaultValue = "255", converter = AccessPermissionConverter.class)
    AccessPermission pdf_permission;

    @CommandLine.Option(names = {"--pdf_align", "-pa"}, defaultValue = "CENTER-CENTER", converter = PageAlignConverter.class)
    PageAlign pdf_align;

    @CommandLine.Option(names = {"--pdf_size", "-pz"})
    PageSize pdf_size;

    @CommandLine.Option(names = {"--pdf_direction", "-pdi"}, defaultValue = "Portrait")
    PageDirection pdf_direction;

    @CommandLine.Option(names = {"--pdf_auto_rotate", "-par"})
    boolean pdf_auto_rotate;

    @CommandLine.Option(names = {"--pdf_destination", "-pdst"})
    String pdf_dst;

    @CommandLine.Option(names = {"--list_destination", "-ldst"})
    File tasklist_dst;

    @CommandLine.Option(names = {"--overwrite", "-ow"})
    boolean overwrite;

    @CommandLine.Parameters
    List<File> sourceFiles;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true)
    boolean usageHelpRequested;

    @Override
    public Integer call() throws Exception {
        if (!overwrite && tasklist_dst.exists()) {
            System.err.printf(SharedSpace.getResString("public.err.overwrite") + "\n", tasklist_dst.getAbsolutePath());
            throw new HandledException(new RuntimeException("Overwrite deny"), getClass());
        }
        DirlistTaskFactory.setArgument(getDocumentArgument(), getPageArgument(), pdf_dst);
        DirlistTaskFactory.setImageFilesRule(filter, fileSorter);

        List<Task> tasks = new ArrayList<>();
        try {
            for (File dirlist : sourceFiles) {
                tasks.addAll(DirlistTaskFactory.createFromDirlist(dirlist, SharedSpace.Configuration.DIRLIST_READ_CHARSET));
            }
        } catch (SourceFileException | DirListException e) {
//            System.err.println(e.getMessage());
            throw new HandledException(e, getClass());
        }
        save(tasks, tasklist_dst);
        return 1;
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

    public void save(List<Task> taskList, File destination) throws HandledException {
        destination.getParentFile().mkdirs();

        String content = new TaskListConverter().toXml(taskList);
        try {
            writeStringToFile(destination, content, SharedSpace.Configuration.TASKlIST_WRITE_CHARSET);
            System.out.printf("[" + SharedSpace.getResString("public.info.exported") + "] %s\n", tasklist_dst.getAbsolutePath());
        } catch (IOException e) {
            System.err.printf(SharedSpace.getResString("create.err.tasklist_create") + "\n", e.getMessage());
            throw new HandledException(e, getClass());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandledException(e, getClass());
        }
    }

    private void writeStringToFile(File file, String content, Charset charset) throws IOException {
        List<String> contents = new ArrayList<>();
        contents.add(content);
        Files.write(file.toPath(), contents, charset);
    }
}

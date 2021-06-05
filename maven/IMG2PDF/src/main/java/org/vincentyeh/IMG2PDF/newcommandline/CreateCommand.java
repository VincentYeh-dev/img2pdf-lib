package org.vincentyeh.IMG2PDF.newcommandline;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.task.DocumentArgument;
import org.vincentyeh.IMG2PDF.task.PageArgument;
import org.vincentyeh.IMG2PDF.util.file.GlobbingFileFilter;
import org.vincentyeh.IMG2PDF.util.file.FileSorter;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "create", description = "create description")
public class CreateCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"sorter","sr"})
    FileSorter fileSorter;
    @CommandLine.Option(names = {"filter", "f"})
    GlobbingFileFilter ffh;

    @CommandLine.Option(names = {"pdf_owner_password","popwd"})
    String owner_password;
    @CommandLine.Option(names = {"pupwd", "pdf_user_password"})
    String user_password;

    @CommandLine.Option(names = {"pp", "pdf_permission"})
    AccessPermission ap;

//    DocumentArgument documentArgument;
    @CommandLine.Option(names ={"pa", "pdf_align"})
    PageAlign align;
    @CommandLine.Option(names = {"pz", "pdf_size"})
    PageSize size;
    @CommandLine.Option(names = {"pdi", "pdf_direction"})
    PageDirection direction;
    @CommandLine.Option(names = {"par", "pdf_auto_rotate"})
    boolean auto_rotate;
    //    PageArgument pageArgument;

    @CommandLine.Option(names = {"pdst", "pdf_destination"})
    String pdf_dst;

    @CommandLine.Option(names = {"ldst", "list_destination"})
    File tasklist_dst;
    @CommandLine.Option(names = {"debug","d"})
    boolean debug;
    @CommandLine.Option(names = {"ow", "overwrite"})
    boolean overwrite;
//    @CommandLine.Option(names = {"debug","d"})
    @CommandLine.Parameters
    File[] sourceFiles;

    @Override
    public Integer call() throws Exception {
        return null;
    }
}

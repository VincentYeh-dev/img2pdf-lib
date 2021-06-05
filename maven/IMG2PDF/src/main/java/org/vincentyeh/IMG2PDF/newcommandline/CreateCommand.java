package org.vincentyeh.IMG2PDF.newcommandline;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.vincentyeh.IMG2PDF.newcommandline.converter.AccessPermissionConverter;
import org.vincentyeh.IMG2PDF.newcommandline.converter.FileSorterConverter;
import org.vincentyeh.IMG2PDF.newcommandline.converter.GlobbingFileFilterConverter;
import org.vincentyeh.IMG2PDF.newcommandline.converter.PageAlignConverter;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.util.file.GlobbingFileFilter;
import org.vincentyeh.IMG2PDF.util.file.FileSorter;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "create", description = "create description")
public class CreateCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"--sorter", "-sr"}, defaultValue = "NAME&INCREASE", converter = FileSorterConverter.class)
    FileSorter fileSorter;

    @CommandLine.Option(names = {"--filter", "-f"}, defaultValue = "glob:*.{PNG,JPG}", converter = GlobbingFileFilterConverter.class)
    GlobbingFileFilter ffh;

    @CommandLine.Option(names = {"--pdf_owner_password", "-popwd"})
    String owner_password;
    @CommandLine.Option(names = {"--pdf_user_password", "-pupwd"})
    String  user_password;

    @CommandLine.Option(names = {"--pdf_permission","-pp"},converter = AccessPermissionConverter.class)
    AccessPermission ap;


    //    DocumentArgument documentArgument;
    @CommandLine.Option(names = {"--pdf_align", "-pa"}, defaultValue = "CENTER-CENTER", converter = PageAlignConverter.class)
    PageAlign align;
    @CommandLine.Option(names = {"--pdf_size", "-pz"})
    PageSize size;
    @CommandLine.Option(names = {"--pdf_direction", "-pdi"}, defaultValue = "Portrait")
    PageDirection direction;
    @CommandLine.Option(names = {"--pdf_auto_rotate", "-par"})
    boolean auto_rotate;
    //    PageArgument pageArgument;

    @CommandLine.Option(names = {"--pdf_destination","-pdst" })
    String pdf_dst;
    @CommandLine.Option(names = {"--list_destination","-ldst" })
    File tasklist_dst;
    @CommandLine.Option(names = {"--debug", "-d"})
    boolean debug;
    @CommandLine.Option(names = { "--overwrite","-ow"})
    boolean overwrite;

    @CommandLine.Parameters
    List<File> sourceFiles;

    @Override
    public Integer call() throws Exception {
        System.out.println(getClass().getSimpleName());

        return 15;
    }
}

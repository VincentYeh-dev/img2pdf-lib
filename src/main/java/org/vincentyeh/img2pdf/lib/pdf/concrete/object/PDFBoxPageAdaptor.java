package org.vincentyeh.img2pdf.lib.pdf.concrete.object;

import com.drew.lang.annotations.NotNull;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class PDFBoxPageAdaptor implements IPage {
    private final PDPage page;
    private final PDDocument document;
    private final int pageNumber;
    private final List<PDFBoxDrawingCommand> PDFBoxDrawingCommands = new LinkedList<>();
    private SizeF pageSize;

    public PDFBoxPageAdaptor(PDDocument document, int pageNumber, SizeF pageSize) {
        this.document = document;
        this.pageNumber = pageNumber;
        page = new PDPage();
        if (pageSize == null)
            throw new IllegalArgumentException("size==null");
        page.setMediaBox(new PDRectangle(pageSize.width, pageSize.height));
    }


    @Override
    public void drawImage(@NotNull BufferedImage image, @NotNull PointF imagePosition, @NotNull SizeF imageSize) throws RuntimeException {

        if (imagePosition == null)
            throw new IllegalArgumentException("position==null");

        if (image == null) throw new IllegalArgumentException("image==null");

        if (imageSize == null) throw new IllegalArgumentException("size==null");

        PDFBoxDrawingCommands.add(new PDFBoxDrawingCommand(image, imagePosition, imageSize));
    }


    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public void render() {
        for (PDFBoxDrawingCommand command : PDFBoxDrawingCommands) {
            command.execute(document, page);
        }
    }

    public PDPage getInternalPage() {
        return page;
    }

}

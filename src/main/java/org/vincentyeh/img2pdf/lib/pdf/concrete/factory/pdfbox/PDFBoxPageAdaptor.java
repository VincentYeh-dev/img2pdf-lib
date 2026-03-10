package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.vincentyeh.img2pdf.lib.annotation.NotNull;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class PDFBoxPageAdaptor implements IPage {
    private final PDPage page;
    private final int pageNumber;
    private final List<PDFBoxDrawingCommand> PDFBoxDrawingCommands = new LinkedList<>();

    public PDFBoxPageAdaptor(int pageNumber, SizeF pageSize) {
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
    public void render(IDocument document) {
        for (PDFBoxDrawingCommand command : PDFBoxDrawingCommands) {
            PDDocument doc = ((PDFBoxDocumentAdaptor) document).getInternalDocument();
            command.execute(doc, page);
        }
    }

    public PDPage getInternalPage() {
        return page;
    }

}

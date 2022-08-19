package org.vincentyeh.img2pdf.lib.pdf.concrete.objects;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PdfPage;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class PdfBoxPageAdaptor implements PdfPage<PDPage> {
    private final PDPage page;
    private final PDDocument document;

    public PdfBoxPageAdaptor(PDPage page, PDDocument document) {
        this.page = page;
        this.document = document;
    }

    @Override
    public PDPage get() {
        return page;
    }

    @Override
    public void setSize(SizeF size) {
        page.setMediaBox(new PDRectangle(size.width(), size.height()));
    }

    @Override
    public SizeF getSize() {
        PDRectangle rectangle = page.getMediaBox();
        return new SizeF(rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public void putImage(BufferedImage image, PointF position, SizeF size) throws IOException {
        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, image);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(pdImageXObject, position.x(), position.y(), size.width(), size.height());
        contentStream.close();

    }


}

package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import java.awt.image.BufferedImage;

public class PDFBoxDrawingCommand {
    private final BufferedImage image;
    private final PointF imagePosition;
    private final SizeF pageSize;

    PDFBoxDrawingCommand(BufferedImage image, PointF imagePosition, SizeF pageSize) {
        this.image = image;
        this.imagePosition = imagePosition;
        this.pageSize = pageSize;
    }

    void execute(PDDocument document, PDPage page){
        if (document == null)
            throw new IllegalStateException("document has not been created");

        if (imagePosition == null)
            throw new IllegalArgumentException("position==null");

        if (image == null) throw new IllegalArgumentException("image==null");

        if (pageSize == null) throw new IllegalArgumentException("size==null");

        try {
            PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, image);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(pdImageXObject, imagePosition.x, imagePosition.y, pageSize.width, pageSize.height);
            contentStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

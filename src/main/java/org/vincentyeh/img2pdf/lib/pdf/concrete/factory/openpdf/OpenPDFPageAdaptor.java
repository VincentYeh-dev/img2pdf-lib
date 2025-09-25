package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class OpenPDFPageAdaptor implements IPage {
    private final int pageNumber;
    private final SizeF pageSize;
    private final ByteArrayOutputStream buffer;
    private final Document document;
    private BufferedImage bufferedImage;
    private PointF imagePosition;
    private SizeF imageSize;
    private boolean isDrawn = false;
    private boolean isRendered = false;


    public OpenPDFPageAdaptor(int pageNumber, SizeF pageSize) {
        checkArgument(pageNumber, pageSize);
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        buffer = new ByteArrayOutputStream();
        document = new Document(new RectangleReadOnly(pageSize.width, pageSize.height), 0, 0, 0, 0);
        PdfWriter.getInstance(document, buffer);
        document.open();
    }


    @Override
    public void drawImage(BufferedImage image, PointF imagePosition, SizeF imageSize) throws RuntimeException {
        if(isRendered)
            throw new IllegalStateException("page has already been rendered, can not draw image anymore");

        Objects.requireNonNull(image, "image==null");
        Objects.requireNonNull(imagePosition, "imagePosition==null");
        Objects.requireNonNull(imageSize, "imageSize==null");

        if(isDrawn)
            throw new IllegalStateException("image has already been drawn on this page");

        if (imageSize.width <= 0 || imageSize.height <= 0)
            throw new IllegalArgumentException("imageSize is invalid");

        this.imagePosition = imagePosition;
        this.imageSize = imageSize;
        this.bufferedImage = image;
        isDrawn = true;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }


    @Override
    public void render(IDocument ignored) {
        if (isRendered)
            throw new IllegalStateException("page has already been rendered");
        isRendered = true;

        if(!isDrawn)
            return;

        document.newPage();
        try {
            Image image1 = Image.getInstance(bufferedImage, null);
            image1.setAbsolutePosition(imagePosition.x, imagePosition.y);
            image1.scaleToFit(imageSize.width, imageSize.height);
            document.add(image1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public SizeF getPageSize() {
        return pageSize;
    }

    public byte[] getPDFBytesContent() {
        document.close();
        return buffer.toByteArray();
    }

    private static void checkArgument(int pageNumber, SizeF pageSize) {
        if (pageNumber <= 0)
            throw new IllegalArgumentException("pageNumber must be greater than zero");
        if (pageSize == null)
            throw new IllegalArgumentException("size==null");
        if (pageSize.width <= 0 || pageSize.height <= 0)
            throw new IllegalArgumentException("size can not be zero or negative");

    }
}

package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import java.awt.image.BufferedImage;

import static org.mockito.Mockito.when;

public class PDFBoxPageAdaptorTest {

    @Test
    public void testConstructorAndGetInternalPage() {
        SizeF size = new SizeF(200, 300);
        PDFBoxPageAdaptor adaptor = new PDFBoxPageAdaptor(1, size);
        PDPage page = adaptor.getInternalPage();
        Assertions.assertNotNull(page);
        Assertions.assertEquals(200, page.getMediaBox().getWidth());
        Assertions.assertEquals(300, page.getMediaBox().getHeight());
    }

    @Test
    public void testConstructorWithNullSizeThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new PDFBoxPageAdaptor(1, null);
        });
    }

    @Test
    public void testDrawImageAndRender() {
        SizeF size = new SizeF(100, 100);
        PDFBoxPageAdaptor adaptor = new PDFBoxPageAdaptor(1, size);

        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        PointF pos = new PointF(5, 5);
        SizeF imgSize = new SizeF(10, 10);

        adaptor.drawImage(img, pos, imgSize);
        PDFBoxDocumentAdaptor mockDoc = Mockito.mock(PDFBoxDocumentAdaptor.class);
        when(mockDoc.getInternalDocument()).thenReturn(new PDDocument());

        // render 不應丟出異常
        Assertions.assertDoesNotThrow(() -> {
            adaptor.render(mockDoc);
        });
    }

    @Test
    public void testDrawImageWithNullImageThrows() {
        SizeF size = new SizeF(100, 100);
        PDFBoxPageAdaptor adaptor = new PDFBoxPageAdaptor(1, size);
        PointF pos = new PointF(0, 0);
        SizeF imgSize = new SizeF(10, 10);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            adaptor.drawImage(null, pos, imgSize);
        });
    }

    @Test
    public void testDrawImageWithNullPositionThrows() {
        SizeF size = new SizeF(100, 100);
        PDFBoxPageAdaptor adaptor = new PDFBoxPageAdaptor(1, size);
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        SizeF imgSize = new SizeF(10, 10);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            adaptor.drawImage(img, null, imgSize);
        });
    }

    @Test
    public void testDrawImageWithNullSizeThrows() {
        SizeF size = new SizeF(100, 100);
        PDFBoxPageAdaptor adaptor = new PDFBoxPageAdaptor(1, size);
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        PointF pos = new PointF(0, 0);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            adaptor.drawImage(img, pos, null);
        });
    }

    @Test
    public void testGetPageNumber() {
        SizeF size = new SizeF(100, 100);
        PDFBoxPageAdaptor adaptor = new PDFBoxPageAdaptor(5, size);
        Assertions.assertEquals(5, adaptor.getPageNumber());
    }
}


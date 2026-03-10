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

/**
 * Tests for {@link PDFBoxPageAdaptor}, covering construction, internal page dimensions,
 * drawImage guard clauses, render, and page number retrieval.
 */
public class PDFBoxPageAdaptorTest {

    /** Verifies that the internal PDPage media box matches the dimensions passed to the constructor. */
    @Test
    public void testConstructorAndGetInternalPage() {
        SizeF size = new SizeF(200, 300);
        PDFBoxPageAdaptor adaptor = new PDFBoxPageAdaptor(1, size);
        PDPage page = adaptor.getInternalPage();
        Assertions.assertNotNull(page);
        Assertions.assertEquals(200, page.getMediaBox().getWidth());
        Assertions.assertEquals(300, page.getMediaBox().getHeight());
    }

    /** Verifies that passing a null size to the constructor throws IllegalArgumentException. */
    @Test
    public void testConstructorWithNullSizeThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new PDFBoxPageAdaptor(1, null);
        });
    }

    /** Verifies that drawImage followed by render does not throw an exception. */
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

        // render should not throw exception
        Assertions.assertDoesNotThrow(() -> {
            adaptor.render(mockDoc);
        });
    }

    /** Verifies that drawImage throws IllegalArgumentException when the image argument is null. */
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

    /** Verifies that drawImage throws IllegalArgumentException when the position argument is null. */
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

    /** Verifies that drawImage throws IllegalArgumentException when the image size argument is null. */
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

    /** Verifies that getPageNumber() returns the page number provided at construction. */
    @Test
    public void testGetPageNumber() {
        SizeF size = new SizeF(100, 100);
        PDFBoxPageAdaptor adaptor = new PDFBoxPageAdaptor(5, size);
        Assertions.assertEquals(5, adaptor.getPageNumber());
    }
}

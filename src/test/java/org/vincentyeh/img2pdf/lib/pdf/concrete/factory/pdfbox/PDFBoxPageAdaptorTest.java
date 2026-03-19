package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import java.awt.image.BufferedImage;

/**
 * Tests for {@link PDFBoxPageAdaptor}, covering construction, internal page dimensions,
 * drawImage guard clauses, and page number retrieval.
 */
public class PDFBoxPageAdaptorTest {

    /** Verifies that getOwnDocument() returns a PDDocument with one page having the correct media box. */
    @Test
    public void testConstructorAndGetOwnDocument() {
        SizeF size = new SizeF(200, 300);
        PDFBoxPageAdaptor adaptor = new PDFBoxPageAdaptor(1, size);
        PDDocument ownDoc = adaptor.getOwnDocument();
        Assertions.assertNotNull(ownDoc);
        Assertions.assertEquals(1, ownDoc.getNumberOfPages());
        Assertions.assertEquals(200, ownDoc.getPage(0).getMediaBox().getWidth());
        Assertions.assertEquals(300, ownDoc.getPage(0).getMediaBox().getHeight());
    }

    /** Verifies that passing a null size to the constructor throws IllegalArgumentException. */
    @Test
    public void testConstructorWithNullSizeThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new PDFBoxPageAdaptor(1, null);
        });
    }

    /** Verifies that drawImage followed by addPage in a real document does not throw an exception. */
    @Test
    public void testDrawImageAndAddPage() {
        SizeF size = new SizeF(100, 100);
        PDFBoxPageAdaptor adaptor = new PDFBoxPageAdaptor(1, size);

        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        PointF pos = new PointF(5, 5);
        SizeF imgSize = new SizeF(10, 10);

        // drawImage should not throw
        Assertions.assertDoesNotThrow(() -> adaptor.drawImage(img, pos, imgSize));

        // getOwnDocument should contain the page
        PDDocument ownDoc = adaptor.getOwnDocument();
        Assertions.assertEquals(1, ownDoc.getNumberOfPages());
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

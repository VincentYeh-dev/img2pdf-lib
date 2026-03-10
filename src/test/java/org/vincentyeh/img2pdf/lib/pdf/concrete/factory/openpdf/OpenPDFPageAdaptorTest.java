package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import java.awt.image.BufferedImage;

import static org.mockito.Mockito.mock;

/**
 * Tests for {@link OpenPDFPageAdaptor}, covering construction, drawImage, render,
 * state transitions, and PDF byte output.
 */
public class OpenPDFPageAdaptorTest {

    /** Verifies that getPageSize() returns the size provided at construction. */
    @Test
    public void testGetPageSize() {
        SizeF size = new SizeF(200, 300);
        OpenPDFPageAdaptor adaptor = new OpenPDFPageAdaptor(1, size);
        Assertions.assertEquals(size, adaptor.getPageSize());
    }

    /** Verifies that the constructor throws IllegalArgumentException for null size, zero size, negative size, or non-positive page number. */
    @Test
    public void testConstructorWithInvalidArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new OpenPDFPageAdaptor(1, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new OpenPDFPageAdaptor(1, new SizeF(0, 0));
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new OpenPDFPageAdaptor(1, new SizeF(-100, -100));
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new OpenPDFPageAdaptor(0, new SizeF(100, 100));
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new OpenPDFPageAdaptor(-1, new SizeF(100, 100));
        });
    }

    /** Verifies drawImage null-argument guards, double-draw guard, post-render draw guard, and that a normal draw followed by render does not throw. */
    @Test
    public void testDrawImage() {
        SizeF size = new SizeF(100, 100);
        OpenPDFPageAdaptor adaptor = new OpenPDFPageAdaptor(1, size);

        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        PointF pos = new PointF(5, 5);
        SizeF imgSize = new SizeF(10, 10);

        adaptor.drawImage(img, pos, imgSize);

        // render should not throw exception
        Assertions.assertDoesNotThrow(() -> {
            adaptor.render(mock(OpenPDFDocumentAdaptor.class));
        });

        OpenPDFPageAdaptor adaptor2 = new OpenPDFPageAdaptor(1, size);

        Assertions.assertThrows(NullPointerException.class, () -> {
            adaptor2.drawImage(null, pos, imgSize);
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            adaptor2.drawImage(img, null, imgSize);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            adaptor2.drawImage(img, pos, null);
        });

        Assertions.assertThrows(IllegalStateException.class, () -> {
            OpenPDFPageAdaptor adaptor3 = new OpenPDFPageAdaptor(1, size);
            adaptor3.drawImage(img, pos, imgSize);
            adaptor3.drawImage(img, pos, imgSize);
        });

        Assertions.assertThrows(IllegalStateException.class, () -> {
            OpenPDFPageAdaptor adaptor3 = new OpenPDFPageAdaptor(1, size);
            adaptor3.render(mock(OpenPDFDocumentAdaptor.class));
            adaptor3.drawImage(img, pos, imgSize);
        });
    }

    /** Verifies that the first render call succeeds and a second render call on the same adaptor throws IllegalStateException. */
    @Test
    public void testRender() {
        SizeF size = new SizeF(100, 100);
        OpenPDFPageAdaptor adaptor = new OpenPDFPageAdaptor(1, size);

        Assertions.assertDoesNotThrow(() -> {
            adaptor.render(mock(OpenPDFDocumentAdaptor.class));
        });

        Assertions.assertThrows(IllegalStateException.class, () ->
                adaptor.render(mock(OpenPDFDocumentAdaptor.class)));
    }

    /** Verifies that getPDFBytesContent() returns a non-null, non-empty byte array after draw and render. */
    @Test
    public void testGetPDFBytesContent() {
        SizeF size = new SizeF(100, 100);
        OpenPDFPageAdaptor adaptor = new OpenPDFPageAdaptor(1, size);

        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        adaptor.drawImage(img ,
                new PointF(0, 0), new SizeF(10, 10));
        adaptor.render(mock(OpenPDFDocumentAdaptor.class));

        Assertions.assertNotNull(adaptor.getPDFBytesContent());
        Assertions.assertTrue(adaptor.getPDFBytesContent().length > 0);
    }


    /** Verifies that getPageNumber() returns the page number provided at construction. */
    @Test
    public void testGetPageNumber() {
        SizeF size = new SizeF(100, 100);
        OpenPDFPageAdaptor adaptor = new OpenPDFPageAdaptor(5, size);
        Assertions.assertEquals(5, adaptor.getPageNumber());
    }

}

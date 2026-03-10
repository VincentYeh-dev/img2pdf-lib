package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link PageDirection}, verifying direction detection logic
 * from both SizeF objects and raw width/height values.
 */
class PageDirectionTest {

    /** Verifies that detectDirection(SizeF) returns Portrait for height &gt; width and Landscape for width &gt; height. */
    @Test
    void testDetectDirectionBySizeF() {
        SizeF portrait = new SizeF(500, 1000);
        SizeF landscape = new SizeF(1000, 500);
        assertEquals(PageDirection.Portrait, PageDirection.detectDirection(portrait));
        assertEquals(PageDirection.Landscape, PageDirection.detectDirection(landscape));
    }

    /** Verifies that detectDirection(width, height) returns the correct direction, including Landscape for a square. */
    @Test
    void testDetectDirectionByWidthHeight() {
        assertEquals(PageDirection.Portrait, PageDirection.detectDirection(500, 1000));
        assertEquals(PageDirection.Landscape, PageDirection.detectDirection(1000, 500));
        assertEquals(PageDirection.Landscape, PageDirection.detectDirection(1000, 1000)); // height/width == 1
    }
}

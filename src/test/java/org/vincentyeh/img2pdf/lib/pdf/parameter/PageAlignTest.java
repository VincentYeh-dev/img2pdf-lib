package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link PageAlign}, covering constructor field storage, valueOf parsing,
 * and toString formatting.
 */
class PageAlignTest {

    /** Verifies that the constructor stores vertical and horizontal alignment values in their respective fields. */
    @Test
    void testConstructorAndFields() {
        PageAlign align = new PageAlign(PageAlign.VerticalAlign.TOP, PageAlign.HorizontalAlign.LEFT);
        assertEquals(PageAlign.VerticalAlign.TOP, align.vertical_align);
        assertEquals(PageAlign.HorizontalAlign.LEFT, align.horizontal_align);
    }

    /** Verifies that valueOf("BOTTOM-RIGHT") produces the correct VerticalAlign and HorizontalAlign combination. */
    @Test
    void testValueOf() {
        PageAlign align = PageAlign.valueOf("BOTTOM-RIGHT");
        assertEquals(PageAlign.VerticalAlign.BOTTOM, align.vertical_align);
        assertEquals(PageAlign.HorizontalAlign.RIGHT, align.horizontal_align);
    }

    /** Verifies that toString() returns the "VERTICAL-HORIZONTAL" formatted string. */
    @Test
    void testToString() {
        PageAlign align = new PageAlign(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER);
        assertEquals("CENTER-CENTER", align.toString());
    }
}

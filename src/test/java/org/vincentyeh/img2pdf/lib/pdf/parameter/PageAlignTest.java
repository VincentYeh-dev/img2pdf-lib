package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PageAlignTest {

    @Test
    void testConstructorAndFields() {
        PageAlign align = new PageAlign(PageAlign.VerticalAlign.TOP, PageAlign.HorizontalAlign.LEFT);
        assertEquals(PageAlign.VerticalAlign.TOP, align.vertical_align);
        assertEquals(PageAlign.HorizontalAlign.LEFT, align.horizontal_align);
    }

    @Test
    void testValueOf() {
        PageAlign align = PageAlign.valueOf("BOTTOM-RIGHT");
        assertEquals(PageAlign.VerticalAlign.BOTTOM, align.vertical_align);
        assertEquals(PageAlign.HorizontalAlign.RIGHT, align.horizontal_align);
    }

    @Test
    void testToString() {
        PageAlign align = new PageAlign(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER);
        assertEquals("CENTER-CENTER", align.toString());
    }
}


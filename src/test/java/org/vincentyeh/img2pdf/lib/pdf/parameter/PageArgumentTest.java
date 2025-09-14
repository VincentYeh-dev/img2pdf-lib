package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PageArgumentTest {

    @Test
    void testConstructorAllArgs() {
        PageAlign align = new PageAlign(PageAlign.VerticalAlign.TOP, PageAlign.HorizontalAlign.LEFT);
        PageArgument arg = new PageArgument(align, PageSize.A4, PageDirection.Landscape, true);
        assertEquals(align, arg.getAlign());
        assertEquals(PageSize.A4, arg.getSize());
        assertEquals(PageDirection.Landscape, arg.getDirection());
        assertTrue(arg.isAutoRotate());
    }

    @Test
    void testConstructorWithAlignEnums() {
        PageArgument arg = new PageArgument(PageAlign.VerticalAlign.BOTTOM, PageAlign.HorizontalAlign.RIGHT, PageSize.A3, PageDirection.Portrait, false);
        assertEquals(PageAlign.VerticalAlign.BOTTOM, arg.getAlign().vertical_align);
        assertEquals(PageAlign.HorizontalAlign.RIGHT, arg.getAlign().horizontal_align);
        assertEquals(PageSize.A3, arg.getSize());
        assertEquals(PageDirection.Portrait, arg.getDirection());
        assertFalse(arg.isAutoRotate());
    }

    @Test
    void testConstructorWithAlignEnumsAndSize() {
        PageArgument arg = new PageArgument(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER, PageSize.A5);
        assertEquals(PageAlign.VerticalAlign.CENTER, arg.getAlign().vertical_align);
        assertEquals(PageAlign.HorizontalAlign.CENTER, arg.getAlign().horizontal_align);
        assertEquals(PageSize.A5, arg.getSize());
        assertEquals(PageDirection.Portrait, arg.getDirection());
        assertTrue(arg.isAutoRotate());
    }

    @Test
    void testConstructorWithAlignEnumsSizeDirection() {
        PageArgument arg = new PageArgument(PageAlign.VerticalAlign.TOP, PageAlign.HorizontalAlign.LEFT, PageSize.A6, PageDirection.Landscape);
        assertEquals(PageAlign.VerticalAlign.TOP, arg.getAlign().vertical_align);
        assertEquals(PageAlign.HorizontalAlign.LEFT, arg.getAlign().horizontal_align);
        assertEquals(PageSize.A6, arg.getSize());
        assertEquals(PageDirection.Landscape, arg.getDirection());
        assertFalse(arg.isAutoRotate());
    }

    @Test
    void testConstructorWithSize() {
        PageArgument arg = new PageArgument(PageSize.LETTER);
        assertEquals(PageAlign.VerticalAlign.CENTER, arg.getAlign().vertical_align);
        assertEquals(PageAlign.HorizontalAlign.CENTER, arg.getAlign().horizontal_align);
        assertEquals(PageSize.LETTER, arg.getSize());
    }

    @Test
    void testDefaultConstructor() {
        PageArgument arg = new PageArgument();
        assertEquals(PageAlign.VerticalAlign.CENTER, arg.getAlign().vertical_align);
        assertEquals(PageAlign.HorizontalAlign.CENTER, arg.getAlign().horizontal_align);
        assertEquals(PageSize.DEPEND_ON_IMG, arg.getSize());
    }

    @Test
    void testSettersAndGetters() {
        PageArgument arg = new PageArgument();
        PageAlign align = new PageAlign(PageAlign.VerticalAlign.BOTTOM, PageAlign.HorizontalAlign.RIGHT);
        arg.setAlign(align);
        arg.setSize(PageSize.A0);
        arg.setDirection(PageDirection.Landscape);
        arg.setAutoRotate(false);

        assertEquals(align, arg.getAlign());
        assertEquals(PageSize.A0, arg.getSize());
        assertEquals(PageDirection.Landscape, arg.getDirection());
        assertFalse(arg.isAutoRotate());
    }
}

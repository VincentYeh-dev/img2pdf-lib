package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;

import static org.junit.jupiter.api.Assertions.*;

class PageDirectionTest {

    @Test
    void testDetectDirectionBySizeF() {
        SizeF portrait = new SizeF(500, 1000);
        SizeF landscape = new SizeF(1000, 500);
        assertEquals(PageDirection.Portrait, PageDirection.detectDirection(portrait));
        assertEquals(PageDirection.Landscape, PageDirection.detectDirection(landscape));
    }

    @Test
    void testDetectDirectionByWidthHeight() {
        assertEquals(PageDirection.Portrait, PageDirection.detectDirection(500, 1000));
        assertEquals(PageDirection.Landscape, PageDirection.detectDirection(1000, 500));
        assertEquals(PageDirection.Landscape, PageDirection.detectDirection(1000, 1000)); // height/width == 1
    }
}


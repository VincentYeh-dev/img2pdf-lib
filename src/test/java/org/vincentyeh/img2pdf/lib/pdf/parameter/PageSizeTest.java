package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link PageSize}, verifying that all enum constants expose consistent
 * pixel, millimetre, and inch dimensions.
 */
class PageSizeTest {

    /** Verifies that every PageSize enum value returns non-null pixel, millimetre, and inch sizes. */
    @Test
    void testEnumValues() {
        for (PageSize size : PageSize.values()) {
            assertNotNull(size.getSizeInPixels());
            assertNotNull(size.getSizeInMillimeters());
            assertNotNull(size.getSizeInInches());
        }
    }

    /** Verifies that getSizeInMillimeters() converts pixels to millimetres using the expected DPI factor. */
    @Test
    void testGetSizeInMillimeters() {
        PageSize size = PageSize.A4;
        SizeF mm = size.getSizeInMillimeters();
        assertEquals(size.getSizeInPixels().width / 2.8346457F, mm.width, 0.0001);
        assertEquals(size.getSizeInPixels().height / 2.8346457F, mm.height, 0.0001);
    }

    /** Verifies that getSizeInInches() converts pixels to inches using the expected 72 DPI factor. */
    @Test
    void testGetSizeInInches() {
        PageSize size = PageSize.A4;
        SizeF inch = size.getSizeInInches();
        assertEquals(size.getSizeInPixels().width / 72.0F, inch.width, 0.0001);
        assertEquals(size.getSizeInPixels().height / 72.0F, inch.height, 0.0001);
    }
}

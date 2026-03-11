package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

/**
 * Tests for the {@link SizeF} value-object class,
 * covering construction validation, comparison, and equality.
 */
public class SizeFTest {

    /** Verifies that SizeF rejects negative dimensions, compares sizes correctly, and respects equals. */
    @Test
    public void testSizeF() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SizeF(-1, -1));
        SizeF s1 = new SizeF(10, 10);
        SizeF s2 = new SizeF(5, 10);
        SizeF s3 = new SizeF(10, 10);
        Assertions.assertTrue(s1.compareTo(s2) > 0);
        Assertions.assertFalse(s1.compareTo(s2) < 0);
        Assertions.assertNotEquals(0, s1.compareTo(s2));
        Assertions.assertThrows(IllegalArgumentException.class, () -> s1.compareTo(null));

        Assertions.assertNotEquals(s1, s2);
        Assertions.assertEquals(s1, s3);
    }
}

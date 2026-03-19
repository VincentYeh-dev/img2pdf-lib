package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;

/**
 * Tests for the {@link PointF} value-object class,
 * covering comparison and equality.
 */
public class PointFTest {

    /** Verifies that PointF compares coordinates correctly and respects equals. */
    @Test
    public void testPointF() {
        PointF p1 = new PointF();
        PointF p2 = new PointF(1, 1);
        PointF p3 = new PointF(2, 2);
        PointF p4 = new PointF(2, 2);
        Assertions.assertEquals(-1, p1.compareTo(p2));
        Assertions.assertEquals(1, p3.compareTo(p2));
        Assertions.assertEquals(p3, p4);
    }
}

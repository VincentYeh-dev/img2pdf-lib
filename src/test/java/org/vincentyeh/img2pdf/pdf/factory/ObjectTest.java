package org.vincentyeh.img2pdf.pdf.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.pdf.framework.objects.SizeF;

public class ObjectTest {

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

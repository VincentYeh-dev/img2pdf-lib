package org.vincentyeh.IMG2PDF.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.vincentyeh.IMG2PDF.util.NameFormatter.ParentOverPointException;

/**
 * Test on https://regex101.com/.
 *
 * @author VincentYeh
 */
public class NameFormatterTester {
    @Test
    public void testName() {
        File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
        assertTrue(file.exists());

        NameFormatter formatter = new NameFormatter(file);
        assertEquals(formatter.format("$NAME"), "TestFile");
    }

    @Test
    public void testCurrentDate() {
        File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
        assertTrue(file.exists());
        NameFormatter formatter = new NameFormatter(file);
        System.out.println(formatter.format("$CY$CM$CD"));

    }

    @Test
    public void testModifyDate() {
        File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
        assertTrue(file.exists());
        NameFormatter formatter = new NameFormatter(file);
        System.out.println(formatter.format("$MY$MM$MD"));
    }

    @Test
    public void testNoSense() {
        File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
        assertTrue(file.exists());

        NameFormatter formatter = new NameFormatter(file);
        System.out.println(formatter.format("AAAAAAAAA$@$@%#@@"));

    }

    @Test
    public void testParent() {
        File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
        assertTrue(file.exists());
        NameFormatter formatter = new NameFormatter(file);
        Assert.assertEquals("resources",formatter.format("$PARENT{0}"));
        Assert.assertEquals("test",formatter.format("$PARENT{1}"));
        Assert.assertEquals("src",formatter.format("$PARENT{2}"));
    }

    @Test(expected = ParentOverPointException.class)
    public void testParentOverPoint() {
        File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
        assertTrue(file.exists());
        int i = 0;
        File buf = file.getParentFile();
        while (true) {
            String format = "$PARENT{" + i + "}";
            NameFormatter formatter = new NameFormatter(file);
            String result = formatter.format(format);
            System.out.printf("%s-->%s\n", format, result);

            if (buf == null) break;
            String real = buf.getName();
            assertEquals(result, real);
            buf = buf.getParentFile();
            i++;
        }
    }
}

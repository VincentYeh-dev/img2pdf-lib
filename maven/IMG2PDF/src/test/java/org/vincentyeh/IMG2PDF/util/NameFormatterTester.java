package org.vincentyeh.IMG2PDF.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

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
    public void testName() throws IOException {
        File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
        assertTrue(file.exists());

        NameFormatter formatter = new NameFormatter(file);
        assertEquals(formatter.format("$NAME"), "TestFile");
    }

    @Test
    public void testCurrentDate() throws IOException {
        File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
        assertTrue(file.exists());
        NameFormatter formatter = new NameFormatter(file);
        System.out.println(formatter.format("$CY$CM$CD"));

    }

    @Test
    public void testModifyDate() throws IOException {
        File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
        assertTrue(file.exists());
        NameFormatter formatter = new NameFormatter(file);
        System.out.println(formatter.format("$MY$MM$MD"));
    }

    @Test
    public void testNoSense() throws IOException {
        File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
        assertTrue(file.exists());

        NameFormatter formatter = new NameFormatter(file);
        System.out.println(formatter.format("AAAAAAAAA$@$@%#@@"));

    }

    @Test
    public void testParent() throws IOException {
        File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
        assertTrue(file.exists());
        NameFormatter formatter = new NameFormatter(file);
        Assert.assertEquals("resources",formatter.format("$PARENT{0}"));
        Assert.assertEquals("test",formatter.format("$PARENT{1}"));
        Assert.assertEquals("src",formatter.format("$PARENT{2}"));
    }

    @Test(expected = ParentOverPointException.class)
    public void testParentOverPoint() throws IOException {
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

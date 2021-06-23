package org.vincentyeh.IMG2PDF.util.file;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;


public class FileNameFormatterTest {
    private static File normal;

    @BeforeAll
    static void initialize() {
        normal = new File("E:\\aa\\bb\\cc\\dd.txt");
    }

    @Test
    public void testNormalInputFormat() {
        FileNameFormatter fileNameFormatter = new FileNameFormatter(normal);
        try {
            Assertions.assertEquals(fileNameFormatter.format("$NAME"), "dd");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Assertions.assertEquals(fileNameFormatter.format("$PARENT{0}"), "cc");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Assertions.assertEquals(fileNameFormatter.format("$PARENT{1}"), "bb");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Assertions.assertEquals(fileNameFormatter.format("$PARENT{2}"), "aa");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Assertions.assertEquals(fileNameFormatter.format("$ROOT"), "E:\\");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void usingNotMappedParent() {
        FileNameFormatter fileNameFormatter = new FileNameFormatter(normal);
        Assertions.assertThrows(FileNameFormatter.NotMappedPattern.class,
                ()->fileNameFormatter.format("$PARENT{3}"));

    }


}

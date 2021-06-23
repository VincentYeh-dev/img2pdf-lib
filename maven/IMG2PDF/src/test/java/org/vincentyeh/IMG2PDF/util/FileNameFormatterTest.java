package org.vincentyeh.IMG2PDF.util;


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
        Assertions.assertEquals(fileNameFormatter.format("$NAME"), "dd");
        Assertions.assertEquals(fileNameFormatter.format("$PARENT{0}"), "cc");
        Assertions.assertEquals(fileNameFormatter.format("$PARENT{1}"), "bb");
        Assertions.assertEquals(fileNameFormatter.format("$PARENT{2}"), "aa");
        Assertions.assertEquals(fileNameFormatter.format("$ROOT"), "E:\\");
    }

    @Test
    public void usingNotExistedParent() {
        FileNameFormatter fileNameFormatter = new FileNameFormatter(normal);
        Assertions.assertEquals(fileNameFormatter.format("$PARENT{3}"), "$PARENT{3}");
    }

//    @Test
//    public void testIllegalFileName() {
//        Assertions.assertThrows(Exception.class,
//                () -> new FileNameFormatter(new File("*.aa&&")));
//    }



}

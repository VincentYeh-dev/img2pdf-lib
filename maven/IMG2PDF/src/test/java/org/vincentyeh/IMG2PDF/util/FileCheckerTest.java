package org.vincentyeh.IMG2PDF.util;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.SharedSpace;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class FileCheckerTest {
//    static {
////		Language Setting:
//
////	Local:
//        SharedSpace.setLanguageRes(ResourceBundle.getBundle("language_package", Locale.getDefault()));
////	root:
//
////		Configuration.setLagugRes(ResourceBundle.getBundle("language_package", Locale.ROOT));
//
//    }


    @Test(expected = IOException.class)
    public void testAbsolute() throws IOException {
        FileChecker.checkAbsolute(new File("aa\\bb.txt"));
    }

    @Test(expected = IOException.class)
    public void testWriteFileInSystemFolder() throws IOException {
        FileChecker.checkWritableFile(new File("C:\\test.xml"));
    }

    @Test(expected = IOException.class)
    public void testWriteInSystemFolder() throws IOException {
        FileChecker.checkWritableFolder(new File("C:\\"));
    }

    @Test
    public void testWriteFileInDataFolder() throws IOException {
        FileChecker.checkWritableFile(new File("D:\\test.xml"));
    }

    @Test
    public void testWriteInDataFolder() throws IOException {
        FileChecker.checkWritableFolder(new File("D:\\"));
    }

    @Test(expected = IOException.class)
    public void testReadFileInSystemFolder() throws IOException {
        FileChecker.checkReadableFile(new File("C:\\AAA"));
    }

    @Test
    public void testReadInSystemFolder() throws IOException {
        FileChecker.checkReadableFolder(new File("C:\\"));
    }

    @Test
    public void testReadFileInDataFolder() throws IOException {

        Path p = Paths.get("D:\\test.xml");
        Files.createFile(p);

        FileChecker.checkReadableFolder(p.toFile());
        Files.delete(p);
    }

    @Test
    public void testReadInDataFolder() throws IOException {
        FileChecker.checkReadableFolder(new File("D:\\"));
    }


}

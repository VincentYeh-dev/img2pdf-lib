package org.vincentyeh.IMG2PDF.util;

import org.junit.Test;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.nio.file.*;
import java.security.AccessController;

public class FileUtilTest {

    @Test (expected = IOException.class)
    public void testWriteFileInSystemFolder() throws IOException {
        FileUtil.checkWritableFile(new File("C:\\test.xml"));
    }

    @Test (expected = IOException.class)
    public void testWriteInSystemFolder() throws IOException {
        FileUtil.checkWritableFolder(new File("C:\\"));
    }

    @Test
    public void testWriteFileInDataFolder() throws IOException {
        FileUtil.checkWritableFile(new File("D:\\test.xml"));
    }

    @Test
    public void testWriteInDataFolder() throws IOException {
        FileUtil.checkWritableFolder(new File("D:\\"));
    }

    @Test(expected = IOException.class)
    public void testReadFileInSystemFolder() throws IOException {
        FileUtil.checkReadableFile(new File("C:\\AAA"));
    }

    @Test
    public void testReadInSystemFolder() throws IOException {
        FileUtil.checkReadableFolder(new File("C:\\"));
    }

    @Test
    public void testReadFileInDataFolder() throws IOException {

        Path p=Paths.get("D:\\test.xml");
        Files.createFile(p);

        FileUtil.checkReadableFolder(p.toFile());
        Files.delete(p);
    }

    @Test
    public void testReadInDataFolder() throws IOException {
        FileUtil.checkReadableFolder(new File("D:\\"));
    }


}

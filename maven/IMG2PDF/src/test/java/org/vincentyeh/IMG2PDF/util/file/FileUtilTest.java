package org.vincentyeh.IMG2PDF.util.file;

import org.junit.jupiter.api.Test;
import org.vincentyeh.IMG2PDF.util.exception.WrongFileTypeException;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilTest {
    @Test
    public void TestNullArgumentInput() {
        assertThrows(IllegalArgumentException.class,
                () -> FileUtils.checkExists(null));

        assertThrows(IllegalArgumentException.class,
                () -> FileUtils.isRoot(null));

        assertThrows(IllegalArgumentException.class,
                () -> FileUtils.checkType(null, null));

        assertThrows(IllegalArgumentException.class,
                () -> FileUtils.checkOverwrite(null, null));

        assertThrows(IllegalArgumentException.class,
                () -> FileUtils.makeDirectories(null));
    }

    @Test
    public void TestNotExistsFile() {
        File file = new File("hello12345");
        if (file.exists())
            throw new IllegalArgumentException();
        assertThrows(FileNotFoundException.class,
                () -> FileUtils.checkType(file, WrongFileTypeException.Type.FILE));
        assertThrows(FileNotFoundException.class,
                () -> FileUtils.getParentFile(file));
        assertThrows(FileNotFoundException.class,
                () -> FileUtils.checkExists(file));
        assertDoesNotThrow(() -> FileUtils.checkOverwrite(file, "No no no"));
    }
    @Test
    public void checkExistTest() throws IOException {
        Path dir = Files.createTempDirectory("directory");
        FileUtils.checkExists(dir.toFile());
        Files.delete(dir);

        Path file=Files.createTempFile("testfile",null);
        FileUtils.checkExists(file.toFile());
        Files.delete(file);

        assertThrows(FileNotFoundException.class,
                ()-> FileUtils.checkExists(dir.toFile()));

        assertThrows(FileNotFoundException.class,
                ()-> FileUtils.checkExists(file.toFile()));
    }

}

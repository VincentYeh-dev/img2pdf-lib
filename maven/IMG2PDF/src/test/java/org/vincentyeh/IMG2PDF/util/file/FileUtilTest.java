package org.vincentyeh.IMG2PDF.util.file;

import org.junit.jupiter.api.Test;
import org.vincentyeh.IMG2PDF.util.file.exception.WrongFileTypeException;


import java.io.File;
import java.io.FileNotFoundException;

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


}

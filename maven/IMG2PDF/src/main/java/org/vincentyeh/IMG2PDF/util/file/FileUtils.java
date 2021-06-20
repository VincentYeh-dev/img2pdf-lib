package org.vincentyeh.IMG2PDF.util.file;

import org.vincentyeh.IMG2PDF.util.file.exception.OverwriteException;
import org.vincentyeh.IMG2PDF.util.file.exception.WrongFileTypeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtils {
    public static File getParentFile(File file) throws NoParentException {
        if (isRoot(file)) {
            throw new TargetRootParentException("No parent in: " + file + "(root)");
        }

        File parent = file.getParentFile();

        if (isNull(parent)) {
            throw new NoParentException("No parent in: " + file);
        }

        return parent;
    }

    public static void checkExists(File file) throws FileNotFoundException {
        if (!file.exists())
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
    }

    public static void checkType(File file, WrongFileTypeException.Type excepted) throws WrongFileTypeException {
        WrongFileTypeException.Type value = file.isFile() ? WrongFileTypeException.Type.FILE : WrongFileTypeException.Type.FOLDER;
        if (value != excepted) {
            throw new WrongFileTypeException(excepted,value);
        }

    }
    public static void checkOverwrite(File file,String reason) throws OverwriteException {
        if (file.exists())
            throw new OverwriteException(reason,file);
    }

    public static boolean isRoot(File file) {
        return file.toPath().getNameCount() == 0;
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static class NoParentException extends IOException {
        public NoParentException(String message) {
            super(message);
        }
    }

    public static class TargetRootParentException extends NoParentException {
        public TargetRootParentException(String message) {
            super(message);
        }
    }
}

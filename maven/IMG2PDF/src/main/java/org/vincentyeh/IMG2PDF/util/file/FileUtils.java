package org.vincentyeh.IMG2PDF.util.file;

import org.vincentyeh.IMG2PDF.util.file.exception.*;

import java.io.File;
import java.io.FileNotFoundException;

public class FileUtils {
    public static File getParentFile(File file) throws NoParentException, FileNotFoundException {
        checkNull(file);
        checkExists(file);
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
        checkNull(file);
        if (!file.exists())
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
    }

    public static void checkType(File file, WrongFileTypeException.Type excepted) throws WrongFileTypeException, FileNotFoundException {
        checkExists(file);
        checkNull(file);
        checkNull(excepted);

        WrongFileTypeException.Type value = file.isFile() ? WrongFileTypeException.Type.FILE : WrongFileTypeException.Type.FOLDER;
        if (value != excepted) {
            throw new WrongFileTypeException(excepted,value);
        }

    }
    public static void checkOverwrite(File file,String reason) throws OverwriteException {
        checkNull(file);
        checkNull(reason);
        if (file.exists())
            throw new OverwriteException(reason,file);
    }

    public static void makeDirectories(File directory) throws MakeDirectoryException {
        checkNull(directory);
        if(!directory.mkdirs()&&!directory.exists()){
            throw new MakeDirectoryException("No directory was created:"+directory);
        }
    }

    public static boolean isRoot(File file) {
        checkNull(file);
        return file.toPath().getNameCount() == 0;
    }

    private static boolean isNull(Object o) {
        return o == null;
    }

    private static void checkNull(Object obj){
        if(obj==null)
            throw new IllegalArgumentException();
    }
}

package org.vincentyeh.IMG2PDF.util.file;

import org.vincentyeh.IMG2PDF.SharedSpace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class FileChecker {

    public static void checkWritableFile(File raw) throws IOException {
        checkAbsolute(raw);
        checkFile(raw);
        checkWritableFolder(raw.getParentFile());
    }

    public static void checkWritableFolder(File folder) throws IOException {
        checkAbsolute(folder);

        checkDirectory(folder);
        checkExists(folder);

        if(!Files.isWritable(folder.toPath()))
            throw new IOException("Folder is not writable:"+folder);
    }


    public static void checkReadableFile(File raw) throws IOException {
        checkAbsolute(raw);
        checkExists(raw);
        checkFile(raw);
        checkReadableFolder(raw.getParentFile());
    }

    public static void checkReadableFolder(File folder) throws IOException {
        checkAbsolute(folder);
        checkDirectory(folder);
        checkExists(folder);
        if(!Files.isReadable(folder.toPath()))
            throw new IOException("Folder is not readable:"+ folder);
    }
    public static void checkFile(File file) throws IOException {
        if(file.isDirectory())
            throw new IOException(file.getAbsolutePath()+" is directory.");
    }
    public static void checkDirectory(File file) throws IOException {
        if(file.isFile())
            throw new IOException(file.getAbsolutePath()+" is file");
    }

    public static void checkExists(File raw) throws FileNotFoundException {
        if (!raw.exists())
            throw new FileNotFoundException(
                    String.format(SharedSpace.getResString("public.err.filenotfound"), raw.getPath()));
    }

    public static void checkAbsolute(File raw) throws IOException {
        if (!raw.isAbsolute())
            throw new IOException("File path is not absolute:"+raw.getPath());
    }

    public static void makeParentDirsIfNotExists(File file) throws IOException {
        checkAbsolute(file);
        makeDirsIfNotExists(file.getParentFile());
    }
    public static void makeDirsIfNotExists(File file) throws IOException {
        checkAbsolute(file);
        file.mkdirs();
//            System.err.println("No folders was created");
    }
}

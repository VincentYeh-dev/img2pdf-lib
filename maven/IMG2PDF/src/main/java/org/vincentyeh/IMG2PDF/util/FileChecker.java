package org.vincentyeh.IMG2PDF.util;

import org.vincentyeh.IMG2PDF.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class FileChecker {

    public static void checkWritableFile(File raw) throws IOException {
        checkAbsolute(raw);
        checkWritableFolder(raw.getParentFile());
    }

    public static void checkWritableFolder(File folder) throws IOException {
        checkExists(folder);
        if(!Files.isWritable(folder.toPath()))
            throw new IOException("Folder is not writable:"+folder.toString());
    }


    public static void checkReadableFile(File raw) throws IOException {
        checkAbsolute(raw);
        checkExists(raw);
        checkReadableFolder(raw.getParentFile());
    }

    public static void checkReadableFolder(File folder) throws IOException {
        checkExists(folder);
        if(!Files.isReadable(folder.toPath()))
            throw new IOException("Folder is not readable:"+folder.toString());
    }

    public static void checkExists(File raw) throws FileNotFoundException {
        if (!raw.exists())
            throw new FileNotFoundException(
                    String.format(Configuration.getResString("err_filenotfound"), raw.getPath()));
    }

    public static void checkAbsolute(File raw) throws IOException {
        if (!raw.isAbsolute())
            throw new IOException("File path is not absolute:"+raw.getPath());
    }

    public static boolean makeParentDirsIfNotExists(File file) throws IOException {
        checkAbsolute(file);
        boolean done=file.getParentFile().mkdirs();
        if (done) {
            System.out.printf(Configuration.getResString("info_required_folder_created") + "\n", file.getParent());
        }
        return done;
    }
}

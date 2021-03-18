package org.vincentyeh.IMG2PDF.util;

import org.vincentyeh.IMG2PDF.commandline.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtil {
    public static void checkReadableFile(File raw) throws IOException {
        checkExists(raw);
        checkAbsolute(raw);

        if (raw.isDirectory())
            throw new IOException(String.format(Configuration.getResString("err_path_is_file") + "\n", raw.getAbsolutePath()));

        if (!raw.canRead())
            throw new IOException("File is not readable:" + raw.getAbsolutePath());

    }


    public static void checkWritableFile(File raw) throws IOException {
        checkAbsolute(raw);

        if(!raw.canWrite())
            throw new IOException("File is not writable:"+raw.getAbsolutePath());
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


}

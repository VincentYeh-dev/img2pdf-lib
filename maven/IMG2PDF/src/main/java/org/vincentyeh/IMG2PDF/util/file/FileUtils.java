package org.vincentyeh.IMG2PDF.util.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtils {

//    public static void checkWritableFile(File raw) throws IOException {
//        checkAbsolute(raw);
//        checkFile(raw);
//        checkWritableFolder(raw.getParentFile());
//    }
//
//    public static void checkWritableFolder(File folder) throws IOException {
//        checkAbsolute(folder);
//        checkDirectory(folder);
//        checkExists(folder);
//
//        if(!Files.isWritable(folder.toPath()))
//            throw new IOException("Folder is not writable:"+folder);
//    }
//
//
//    public static void checkReadableFile(File raw) throws IOException {
//        checkAbsolute(raw);
//        checkExists(raw);
//        checkFile(raw);
//        checkReadableFolder(raw.getParentFile());
//    }
//
//    public static void checkReadableFolder(File folder) throws IOException {
//        checkAbsolute(folder);
//        checkDirectory(folder);
//        checkExists(folder);
//        if(!Files.isReadable(folder.toPath()))
//            throw new IOException("Folder is not readable:"+ folder);
//    }

    public static void checkIsFile(File file) throws WrongTypeException {
        if(file.isDirectory())
            throw new WrongTypeException("file",file);
    }

    public static void checkIsDirectory(File file) throws WrongTypeException {
        if(file.isFile())
            throw new WrongTypeException("directory",file);
    }

    public static void checkExists(File raw) throws FileNotFoundException {
        if (!raw.exists())
            throw new FileNotFoundException(
                    String.format("Not exist :%s", raw.getPath()));
    }

    public static void checkAbsolute(File file) throws IOException {
        if (!file.isAbsolute())
            throw new PathNotAbsoluteException(file);
    }


    public static boolean makeDirsIfNotExists(File directory) {
        return directory.mkdirs();
//            System.err.println("No folders was created");
    }

    public static class PathNotAbsoluteException extends IOException{
        private final File path;

        public PathNotAbsoluteException(File path) {
            super("File path is not absolute:"+path.getPath());
            this.path = path;
        }

        public File getPath() {
            return path;
        }
    }

    public static class WrongTypeException extends IOException{
        private final String type;
        private final File file;

        public WrongTypeException(String type, File file) {
           super(String.format("Path is not a %s :%s",type,file.getAbsolutePath()));
            this.type = type;
            this.file = file;
        }

        public String getType() {
            return type;
        }

        public File getFile() {
            return file;
        }
    }
}

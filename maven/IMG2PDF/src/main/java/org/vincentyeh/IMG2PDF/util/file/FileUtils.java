package org.vincentyeh.IMG2PDF.util.file;

import java.io.File;

public class FileUtils {
    public static File getParentFile(File file) throws NoParentException {
        if(isRoot(file)){
            throw new NoParentException("No parent in: "+file+"(root)");
        }

        File parent=file.getParentFile();

        if(isNull(parent)){
            throw new NoParentException("No parent in: "+file);
        }

        return parent;
    }

    public static boolean isRoot(File file){
        return file.toPath().getNameCount()==0;
    }
    public static boolean isNull(Object o){
        return o==null;
    }

    public static class NoParentException extends Exception{
        public NoParentException(String message) {
            super(message);
        }
    }

}

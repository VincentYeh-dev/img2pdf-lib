package org.vincentyeh.IMG2PDF.task.factory.exception;

import java.io.File;

public class DirListException extends Exception{

    private final File dirlist;

    public DirListException(String message, File dirlist) {
        super(message);
        this.dirlist = dirlist;
    }

    public DirListException(Throwable cause, File dirlist) {
        super(cause);
        this.dirlist = dirlist;
    }

    public File getDirlist() {
        return dirlist;
    }
}

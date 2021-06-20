package org.vincentyeh.IMG2PDF.task.factory.exception;

import java.io.File;

public class DirListException extends DirlistTaskFactoryException{

    private final File dirlist;

    public DirListException(Throwable cause, File dirlist) {
        super(cause);
        this.dirlist = dirlist;
    }

    public File getDirlist() {
        return dirlist;
    }
}

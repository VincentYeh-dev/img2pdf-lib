package org.vincentyeh.IMG2PDF.task.framework.factory.exception;

import java.io.File;

public class TaskFactoryProcessException extends Exception{

    private final File file;

    public TaskFactoryProcessException(File file, Throwable e) {
        super(e);
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}

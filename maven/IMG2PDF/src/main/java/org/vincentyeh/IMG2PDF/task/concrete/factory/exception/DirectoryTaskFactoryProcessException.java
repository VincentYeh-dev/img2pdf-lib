package org.vincentyeh.IMG2PDF.task.concrete.factory.exception;

import org.vincentyeh.IMG2PDF.task.framework.factory.exception.TaskFactoryProcessException;

import java.io.File;

public class DirectoryTaskFactoryProcessException extends TaskFactoryProcessException {


    private final File directory;

    public DirectoryTaskFactoryProcessException(File directory,Throwable e) {
        super(e);
        this.directory = directory;
    }

    public File getDirectory() {
        return directory;
    }
}

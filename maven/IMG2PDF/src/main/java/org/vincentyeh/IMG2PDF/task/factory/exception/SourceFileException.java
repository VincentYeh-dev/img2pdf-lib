package org.vincentyeh.IMG2PDF.task.factory.exception;

import java.io.File;

public class SourceFileException extends DirlistTaskFactoryException{

    private final File source;

    public SourceFileException(Throwable cause, File source) {
        super(cause);
        this.source = source;
    }

    public File getSource() {
        return source;
    }
}

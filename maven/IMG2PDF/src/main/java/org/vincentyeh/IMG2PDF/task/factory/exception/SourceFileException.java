package org.vincentyeh.IMG2PDF.task.factory.exception;

import java.io.File;

public class SourceFileException extends Exception{

    private final File source;

    public SourceFileException(String message, File source) {
        super(message);
        this.source = source;
    }

    public SourceFileException(Throwable cause, File source) {
        super(cause);
        this.source = source;
    }

    public File getSource() {
        return source;
    }
}

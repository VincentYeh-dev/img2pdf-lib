package org.vincentyeh.IMG2PDF.task.factory.exception;

import java.io.File;

public class SourceFileException extends DirlistTaskFactoryException{

    private final File source;
    private final int line;
    public SourceFileException(Throwable cause, File source, int line) {
        super(cause);
        this.source = source;
        this.line = line;
    }

    public File getSource() {
        return source;
    }

    public int getLine() {
        return line;
    }
}

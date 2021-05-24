package org.vincentyeh.IMG2PDF.converter.exception;

import java.io.File;

public class ReadImageException extends RuntimeException {
    private final File file;
    private final Throwable cause;

    public ReadImageException(File file, Throwable cause) {
        super(String.format("Unable to import image:%s", cause.getMessage()));
        this.file = file;
        this.cause = cause;
    }

    public File getFile() {
        return file;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}

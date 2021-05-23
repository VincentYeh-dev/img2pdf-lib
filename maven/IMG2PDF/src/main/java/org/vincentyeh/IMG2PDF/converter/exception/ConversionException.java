package org.vincentyeh.IMG2PDF.converter.exception;

import java.io.File;

public class ConversionException extends RuntimeException {
    private final File file;
    private final Throwable cause;

    public ConversionException(File file, Throwable cause) {
        super(String.format("Error occur during conversion:%s", cause.getMessage()));
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

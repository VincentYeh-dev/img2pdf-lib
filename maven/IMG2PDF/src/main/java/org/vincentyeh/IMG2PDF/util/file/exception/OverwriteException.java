package org.vincentyeh.IMG2PDF.util.file.exception;

import java.io.File;
import java.io.IOException;

public class OverwriteException extends IOException {
    private final File file;
    public OverwriteException(String message, File file) {
        super(message);
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}

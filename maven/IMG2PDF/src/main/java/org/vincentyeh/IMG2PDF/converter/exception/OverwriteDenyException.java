package org.vincentyeh.IMG2PDF.converter.exception;
import java.io.File;

public class OverwriteDenyException extends RuntimeException {
    private final File file;

    public OverwriteDenyException(File file) {
        super(String.format("Overwrite DENY:%s", file.getPath()));
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}

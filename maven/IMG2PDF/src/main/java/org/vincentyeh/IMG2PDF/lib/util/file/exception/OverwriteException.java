package org.vincentyeh.IMG2PDF.lib.util.file.exception;

import java.io.File;

public class OverwriteException extends FileException{

    public OverwriteException(String message, File file) {
        super(message, file);
    }

}

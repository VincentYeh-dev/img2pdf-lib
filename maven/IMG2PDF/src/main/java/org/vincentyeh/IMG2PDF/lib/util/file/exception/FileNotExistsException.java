package org.vincentyeh.IMG2PDF.lib.util.file.exception;

import java.io.File;

public class FileNotExistsException extends FileException{
    public FileNotExistsException(File file) {
        super(file+" not found.",file);
    }
}

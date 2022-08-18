package org.vincentyeh.IMG2PDF.lib.util.file.exception;

import java.io.File;

public class NoParentException extends FileException{

    public NoParentException(File file) {
        super("No parent in: " + file, file);
    }
}

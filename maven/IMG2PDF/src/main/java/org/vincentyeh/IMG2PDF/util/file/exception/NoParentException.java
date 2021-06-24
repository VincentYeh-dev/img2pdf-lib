package org.vincentyeh.IMG2PDF.util.file.exception;

import java.io.IOException;

public class NoParentException extends IOException {
    public NoParentException(String message) {
        super(message);
    }
}

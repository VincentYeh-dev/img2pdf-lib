package org.vincentyeh.IMG2PDF.util.exception;

import java.io.IOException;

public class MakeDirectoryException extends IOException {

    public MakeDirectoryException(String message,Throwable e) {
        super(message,e);
    }
}

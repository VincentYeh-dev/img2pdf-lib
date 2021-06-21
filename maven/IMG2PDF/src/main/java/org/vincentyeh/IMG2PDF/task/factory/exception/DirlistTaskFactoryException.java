package org.vincentyeh.IMG2PDF.task.factory.exception;

public class DirlistTaskFactoryException extends Exception{

    public DirlistTaskFactoryException() {
    }

    public DirlistTaskFactoryException(String message) {
        super(message);
    }

    public DirlistTaskFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirlistTaskFactoryException(Throwable cause) {
        super(cause);
    }

    public DirlistTaskFactoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

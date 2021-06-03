package org.vincentyeh.IMG2PDF.commandline.parser.core;

public class HandledException extends Exception{
    private final Class<?> where;

    public HandledException(Exception e, Class<?> where){
        super(String.format("Handled in %s:%s",where.getSimpleName(),e.getMessage()));
        this.where = where;
        setStackTrace(e.getStackTrace());
    }

    public HandledException(String message,Class<?> where) {
        super(String.format("Handled in %s:%s",where.getSimpleName(),message));
        this.where = where;
    }
}

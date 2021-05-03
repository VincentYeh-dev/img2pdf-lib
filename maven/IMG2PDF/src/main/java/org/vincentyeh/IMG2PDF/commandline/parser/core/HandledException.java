package org.vincentyeh.IMG2PDF.commandline.parser.core;

public class HandledException extends Exception{
    public HandledException(Exception e,Class<?> where){
        super(String.format("Handled in %s:%s",where.getSimpleName(),e.getMessage()));
        setStackTrace(e.getStackTrace());
    }
}

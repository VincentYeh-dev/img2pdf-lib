package org.vincentyeh.IMG2PDF.commandline.handler;

import org.vincentyeh.IMG2PDF.commandline.handler.core.ExceptionHandler;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.util.file.exception.*;

import java.io.File;

import static java.lang.String.format;
public class FileExceptionHandler extends ExceptionHandler {
    public FileExceptionHandler(Handler<String, Exception> next) {
        super(next,"io.file");
    }

    @Override
    public String handle(Exception data) throws CantHandleException {
        if(data instanceof FileException){
            if(data instanceof FileNotExistsException){
                String a=getLocaleString("FileNotExistsException");
                return format(a,getExceptionFile(data));
            }else if(data instanceof InvalidFileException){
                return format(getLocaleString("InvalidFileException"),getExceptionFile(data));
            }else if(data instanceof WrongFileTypeException){
                WrongFileTypeException ex1=(WrongFileTypeException) data;
                return format(getLocaleString("WrongFileTypeException"),getFileTypeString(ex1.getExpected()), getFileTypeString(ex1.getValue()),getExceptionFile(ex1));
            }else if(data instanceof NoParentException){
                return format(getLocaleString("NoParentException"),getExceptionFile(data));
            }
        }
        return doNext(data);
    }

    private File getExceptionFile(Exception e){
        return ((FileException)e).getFile();
    }
}

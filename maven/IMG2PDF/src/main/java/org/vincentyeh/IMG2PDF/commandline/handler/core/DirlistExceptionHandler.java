package org.vincentyeh.IMG2PDF.commandline.handler.core;


import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.task.factory.DirlistTaskFactory;
import org.vincentyeh.IMG2PDF.util.file.exception.WrongFileTypeException;

import java.io.FileNotFoundException;


public class DirlistExceptionHandler extends ExceptionHandler{

    public DirlistExceptionHandler(Handler<String, Exception> next) {
        super(next,"dirlist");
    }

    @Override
    public String handle(Exception data) throws CantHandleException {
        if (data instanceof DirlistTaskFactory.DirListException) {
            DirlistTaskFactory.DirListException ex1 = (DirlistTaskFactory.DirListException) data;
            if (ex1.getCause() instanceof FileNotFoundException) {
                return String.format(getLocaleString("not_found"), ex1.getDirlist());
            } else if (ex1.getCause() instanceof WrongFileTypeException) {
                WrongFileTypeException ex2 = (WrongFileTypeException) ex1.getCause();
                return String.format(getLocaleString("wrong_type"), getFileTypeString(ex2.getExpected()), getFileTypeString(ex2.getValue()), ex1.getDirlist());
            }
        }

        return doNext(data);
    }
}

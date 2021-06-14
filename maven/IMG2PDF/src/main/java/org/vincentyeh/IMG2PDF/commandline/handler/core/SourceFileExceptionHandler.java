package org.vincentyeh.IMG2PDF.commandline.handler.core;


import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.task.factory.DirlistTaskFactory;
import org.vincentyeh.IMG2PDF.util.file.exception.WrongFileTypeException;

import java.io.FileNotFoundException;

public class SourceFileExceptionHandler extends ResourceBundleHandler<String,Exception>{

    public SourceFileExceptionHandler(Handler<String, Exception> next) {
        super(next,"source");
    }

    @Override
    public String handle(Exception data) {
        if (data instanceof DirlistTaskFactory.SourceFileException) {
            DirlistTaskFactory.SourceFileException ex1 = (DirlistTaskFactory.SourceFileException) data;
            if (ex1.getCause() instanceof FileNotFoundException) {
                return String.format(getLocaleString("not_found"), ex1.getSource());
            } else if (ex1.getCause() instanceof DirlistTaskFactory.EmptyImagesException) {
                return String.format(getLocaleString("empty_image"), ex1.getSource());
            } else if (ex1.getCause() instanceof WrongFileTypeException) {
                WrongFileTypeException ex2 = (WrongFileTypeException) ex1.getCause();
                return String.format(getLocaleString("wrong_type"), getFileTypeString(ex2.getExpected()), getFileTypeString(ex2.getValue()), ex1.getSource());
            }
        }

        return doNext(data);
    }
}

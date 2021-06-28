package org.vincentyeh.IMG2PDF.commandline.handler.core;

import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.task.factory.exception.DirListException;
import org.vincentyeh.IMG2PDF.task.factory.exception.DirlistTaskFactoryException;
import org.vincentyeh.IMG2PDF.task.factory.exception.EmptyImagesException;
import org.vincentyeh.IMG2PDF.task.factory.exception.SourceFileException;
import org.vincentyeh.IMG2PDF.util.file.exception.WrongFileTypeException;
import org.vincentyeh.IMG2PDF.util.file.FileNameFormatter;

import java.io.FileNotFoundException;

public class DirlistTaskFactoryExceptionHandler extends ExceptionHandler {

    public DirlistTaskFactoryExceptionHandler(Handler<String, Exception> next) {
        super(next,"dirlist_task_factory");
    }

    @Override
    public String handle(Exception data) throws CantHandleException {
        if(data instanceof DirlistTaskFactoryException) {
            if (data instanceof DirListException) {
                DirListException ex1 = (DirListException) data;
                if (ex1.getCause() instanceof FileNotFoundException) {
                    return String.format(getLocaleString("dirlist.not_found"), ex1.getDirlist());
                } else if (ex1.getCause() instanceof WrongFileTypeException) {
                    WrongFileTypeException ex2 = (WrongFileTypeException) ex1.getCause();
                    return String.format(getLocaleString("dirlist.wrong_type"), getFileTypeString(ex2.getExpected()), getFileTypeString(ex2.getValue()), ex1.getDirlist());
                }
            }

            if (data instanceof SourceFileException) {
                SourceFileException ex1 = (SourceFileException) data;
                if (ex1.getCause() instanceof FileNotFoundException) {
                    return String.format(getLocaleString("source.not_found"), ex1.getSource());
                } else if (ex1.getCause() instanceof EmptyImagesException) {
                    return String.format(getLocaleString("source.empty_image"), ex1.getSource());
                } else if (ex1.getCause() instanceof WrongFileTypeException) {
                    WrongFileTypeException ex2 = (WrongFileTypeException) ex1.getCause();
                    return String.format(getLocaleString("source.wrong_type"), getFileTypeString(ex2.getExpected()), getFileTypeString(ex2.getValue()), ex1.getSource());
                }else if(ex1.getCause() instanceof FileNameFormatter.NotMappedPattern){
                    FileNameFormatter.NotMappedPattern ex2=(FileNameFormatter.NotMappedPattern) ex1.getCause();
                    return String.format(getLocaleString("source.no_map_pattern"),ex2.getPattern());
                }
            }

        }

        return doNext(data);
    }
}

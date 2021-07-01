package org.vincentyeh.IMG2PDF.commandline.handler.core;

import org.vincentyeh.IMG2PDF.commandline.handler.FileExceptionHandler;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.task.factory.exception.DirListException;
import org.vincentyeh.IMG2PDF.task.factory.exception.DirlistTaskFactoryException;
import org.vincentyeh.IMG2PDF.task.factory.exception.EmptyImagesException;
import org.vincentyeh.IMG2PDF.task.factory.exception.SourceFileException;
import org.vincentyeh.IMG2PDF.util.file.FileNameFormatter;
import org.vincentyeh.IMG2PDF.util.file.exception.FileException;

import static java.lang.String.format;

public class DirlistTaskFactoryExceptionHandler extends ExceptionHandler {

    public DirlistTaskFactoryExceptionHandler(Handler<String, Exception> next) {
        super(next, "dirlist_task_factory");
    }

    @Override
    public String handle(Exception data) throws CantHandleException {
        if (data instanceof DirlistTaskFactoryException) {
            if (data instanceof DirListException) {
                return "In dirlist : " + ((ExceptionHandler) new FileExceptionHandler(null)).handle((Exception) data.getCause());
            }

            if (data instanceof SourceFileException) {
                SourceFileException ex1 = (SourceFileException) data;
                if (data.getCause() instanceof FileException) {
                    return targetLine(ex1.getLine(), ((ExceptionHandler) new FileExceptionHandler(null)).handle((Exception) data.getCause()));
                } else if (ex1.getCause() instanceof EmptyImagesException) {
                    return targetLine(ex1.getLine(),format(getLocaleString("source.empty_image"), ex1.getSource()));
                } else if (ex1.getCause() instanceof FileNameFormatter.NotMappedPattern) {
                    FileNameFormatter.NotMappedPattern ex2 = (FileNameFormatter.NotMappedPattern) ex1.getCause();
                    return targetLine(ex1.getLine(),format(getLocaleString("source.no_map_pattern"), ex1.getSource(), ex2.getPattern()));
                }
            }

        }

        return doNext(data);
    }

    private String targetLine(int line, String msg) {
        return format(getPublicString("at_line") + " : %s",line,msg);
    }
}

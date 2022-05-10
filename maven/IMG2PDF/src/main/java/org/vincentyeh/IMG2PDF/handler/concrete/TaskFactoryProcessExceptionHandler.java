package org.vincentyeh.IMG2PDF.handler.concrete;

import org.vincentyeh.IMG2PDF.handler.concrete.core.FileExceptionHandler;
import org.vincentyeh.IMG2PDF.handler.framework.CantHandleException;
import org.vincentyeh.IMG2PDF.handler.framework.ExceptionHandler;
import org.vincentyeh.IMG2PDF.handler.framework.Handler;
import org.vincentyeh.IMG2PDF.task.concrete.factory.exception.DirectoryTaskFactoryProcessException;
import org.vincentyeh.IMG2PDF.task.concrete.factory.exception.EmptyImagesException;
import org.vincentyeh.IMG2PDF.util.file.FileNameFormatter;
import org.vincentyeh.IMG2PDF.util.file.exception.FileException;

import java.io.File;
import java.util.ResourceBundle;

public class TaskFactoryProcessExceptionHandler extends ExceptionHandler {

    public TaskFactoryProcessExceptionHandler(Handler<String, Exception> next, ResourceBundle resourceBundle) {
        super(next, "dirlist_task_factory", resourceBundle);
    }

    @Override
    public String handle(Exception data) throws CantHandleException {

        if (data instanceof DirectoryTaskFactoryProcessException) {
            DirectoryTaskFactoryProcessException ex1 = (DirectoryTaskFactoryProcessException) data;
            if (data.getCause() instanceof FileException) {
                return ((ExceptionHandler) new FileExceptionHandler(null, getResourceBundle())).handle((Exception) data.getCause());
            } else if (ex1.getCause() instanceof EmptyImagesException) {
                return targetFile(ex1.getDirectory(), getLocaleString("source.empty_image"));
            } else if (ex1.getCause() instanceof FileNameFormatter.NotMappedPattern) {
                FileNameFormatter.NotMappedPattern ex2 = (FileNameFormatter.NotMappedPattern) ex1.getCause();
                return targetFile(ex1.getDirectory(), String.format(getLocaleString("source.no_map_pattern"), ex2.getPattern()));
            }
        }

        return doNext(data);
    }

    private String targetFile(File file, String msg) {
        return String.format(getPublicString("at_file") + " : %s", file.toString(), msg);
    }
}

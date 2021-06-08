package org.vincentyeh.IMG2PDF.commandline.handler.execution;

import org.vincentyeh.IMG2PDF.commandline.ConvertCommand;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ClassHandler;
import org.vincentyeh.IMG2PDF.converter.exception.ConversionException;
import org.vincentyeh.IMG2PDF.converter.exception.OverwriteDenyException;
import org.vincentyeh.IMG2PDF.converter.exception.ReadImageException;
import org.vincentyeh.IMG2PDF.pattern.Handler;

import java.io.FileNotFoundException;
import java.util.ResourceBundle;

public class ConvertHandler extends ClassHandler {
    private final ResourceBundle resourceBundle;

    private String message;
    private int exitCode;

    public ConvertHandler(ResourceBundle resourceBundle) {
        super(ConvertCommand.class);
        this.resourceBundle = resourceBundle;
    }

    @Override
    public void parse(Exception e) {
        Handler<String, Exception> handler=new PDFConversionExceptionHandler(new TaskListExceptionHandler(null));
        message=handler.handle(e);
    }

    @Override
    public String getErrorMessage() {
        return message;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    private String getLocaleResource(String key) {
        return resourceBundle.getString("err.execution.convert.handler." + key);
    }

    private String getFileTypeResource(ConvertCommand.WrongFileTypeException.Type type) {
        return getPublicResource("file.type." + type.toString().toLowerCase());
    }

    private String getPublicResource(String key) {
        return resourceBundle.getString("public." + key);
    }

    private class TaskListExceptionHandler extends Handler<String, Exception> {

        public TaskListExceptionHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data instanceof ConvertCommand.TaskListException) {
                ConvertCommand.TaskListException ex1 = (ConvertCommand.TaskListException) data;
                if (ex1.getCause() instanceof FileNotFoundException) {
                    return String.format(getLocaleResource("tasklist.not_found"), ex1.getTasklist());
                } else if (ex1.getCause() instanceof ConvertCommand.WrongFileTypeException) {
                    ConvertCommand.WrongFileTypeException ex2 = (ConvertCommand.WrongFileTypeException) ex1.getCause();
                    return String.format(getLocaleResource("tasklist.wrong_type"), getFileTypeResource(ex2.getExpected()), getFileTypeResource(ex2.getValue()), ex1.getTasklist());
                } else if (ex1.getCause() instanceof ConvertCommand.NoTaskException) {
                    return String.format(getLocaleResource("tasklist.no_task"), ex1.getTasklist());
                }
            }
            return doNext(data,data.getMessage());
        }
    }

    private class PDFConversionExceptionHandler extends Handler<String, Exception> {

        public PDFConversionExceptionHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data instanceof ConvertCommand.PDFConversionException) {
                ConvertCommand.PDFConversionException ex1 = (ConvertCommand.PDFConversionException) data;
                if (ex1.getCause() instanceof ReadImageException) {
                    ReadImageException ex2 = (ReadImageException) data.getCause();
                    return String.format(getLocaleResource("conversion.read_image"), ex2.getFile());
                } else if (ex1.getCause() instanceof OverwriteDenyException) {
                    return String.format(getLocaleResource("conversion.overwrite"), ex1.getTask().getPdfDestination());
                } else if (ex1.getCause() instanceof ConversionException) {
                    return String.format(getLocaleResource("conversion.conversion"), ex1.getCause().getCause().getMessage());
                }
            }
            return doNext(data, data.getMessage());
        }
    }
}
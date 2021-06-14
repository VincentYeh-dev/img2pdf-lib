package org.vincentyeh.IMG2PDF.commandline.handler;

import org.vincentyeh.IMG2PDF.commandline.exception.*;
import org.vincentyeh.IMG2PDF.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.util.file.exception.WrongFileTypeException;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.task.factory.DirlistTaskFactory;
import picocli.CommandLine;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

public class ResourceBundleExecutionHandler implements CommandLine.IExecutionExceptionHandler {

    private final ResourceBundle resourceBundle;

    public ResourceBundleExecutionHandler(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    public int handleExecutionException(Exception ex, CommandLine cmd, CommandLine.ParseResult parseResult) {
        Handler<String, Exception> handler=new DirlistExceptionHandler(new PDFConversionExceptionHandler(new SourceFileExceptionHandler(null)));
        String msg;
        try {
            msg = handler.handle(ex);
        }catch (Handler.CantHandleException e){
            printErrorText(cmd,"Can't handle");
            msg=ex.getMessage();
            ex.printStackTrace();
        }

        printErrorText(cmd, msg);
        return CommandLine.ExitCode.SOFTWARE;
    }

    private void printErrorText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(cmd.getColorScheme().errorText(message)); // bold red
    }

    private void printText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(message); // bold red
    }

    private String getLocaleResource(String key) {
        return resourceBundle.getString("execution.convert.handler." + key);
    }

    private String getFileTypeResource(WrongFileTypeException.Type type) {
        return getPublicResource("file.type." + type.toString().toLowerCase());
    }

    private String getPublicResource(String key) {
        return resourceBundle.getString("public." + key);
    }

    private class SourceFileExceptionHandler extends Handler<String, Exception> {

        public SourceFileExceptionHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data instanceof DirlistTaskFactory.SourceFileException) {
                DirlistTaskFactory.SourceFileException ex1 = (DirlistTaskFactory.SourceFileException) data;
                if (ex1.getCause() instanceof FileNotFoundException) {
                    return String.format(getLocaleResource("source.not_found"), ex1.getSource());
                } else if (ex1.getCause() instanceof DirlistTaskFactory.EmptyImagesException) {
                    return String.format(getLocaleResource("source.empty_image"), ex1.getSource());
                } else if (ex1.getCause() instanceof WrongFileTypeException) {
                    WrongFileTypeException ex2 = (WrongFileTypeException) ex1.getCause();
                    return String.format(getLocaleResource("source.wrong_type"), getFileTypeResource(ex2.getExpected()), getFileTypeResource(ex2.getValue()), ex1.getSource());
                }
            }

            return doNext(data);
        }
    }

    private class DirlistExceptionHandler extends Handler<String, Exception> {

        public DirlistExceptionHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data instanceof DirlistTaskFactory.DirListException) {
                DirlistTaskFactory.DirListException ex1 = (DirlistTaskFactory.DirListException) data;
                if (ex1.getCause() instanceof FileNotFoundException) {
                    return String.format(getLocaleResource("dirlist.not_found"), ex1.getDirlist());
                } else if (ex1.getCause() instanceof WrongFileTypeException) {
                    WrongFileTypeException ex2 = (WrongFileTypeException) ex1.getCause();
                    return String.format(getLocaleResource("dirlist.wrong_type"), getFileTypeResource(ex2.getExpected()), getFileTypeResource(ex2.getValue()), ex1.getDirlist());
                }
            }

            return doNext(data);
        }
    }

    private class PDFConversionExceptionHandler extends Handler<String, Exception> {

        public PDFConversionExceptionHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data instanceof PDFConversionException) {
                PDFConversionException ex1 = (PDFConversionException) data;
                if (ex1.getCause() instanceof PDFConverter.ReadImageException) {
                    PDFConverter.ReadImageException ex2 = (PDFConverter.ReadImageException) data.getCause();
                    return String.format(getLocaleResource("conversion.read_image"), ex2.getFile());
                } else if (ex1.getCause() instanceof PDFConverter.OverwriteDenyException) {
                    return String.format(getLocaleResource("conversion.overwrite"), ex1.getTask().getPdfDestination());
                } else if (ex1.getCause() instanceof PDFConverter.ConversionException) {
                    return String.format(getLocaleResource("conversion.conversion"), ex1.getCause().getCause().getMessage());
                }
            }
            return doNext(data);
        }
    }
}

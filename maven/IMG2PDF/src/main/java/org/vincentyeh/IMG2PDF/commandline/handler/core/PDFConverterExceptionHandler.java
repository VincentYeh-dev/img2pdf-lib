package org.vincentyeh.IMG2PDF.commandline.handler.core;


import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.pdf.converter.exception.PDFConversionException;
import org.vincentyeh.IMG2PDF.pdf.converter.exception.PDFConverterException;
import org.vincentyeh.IMG2PDF.pdf.converter.exception.ReadImageException;
import org.vincentyeh.IMG2PDF.pdf.converter.exception.SaveException;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.util.file.exception.InvalidFileException;
import org.vincentyeh.IMG2PDF.util.file.exception.OverwriteException;

public class PDFConverterExceptionHandler extends ExceptionHandler {

    public PDFConverterExceptionHandler(Handler<String, Exception> next) {
        super(next, "conversion");
    }

    @Override
    public String handle(Exception data) throws CantHandleException {
        if (data instanceof PDFConverterException) {
            PDFConverterException ex1 = (PDFConverterException) data;
            Throwable cause = ex1.getCause();
            Task task = ex1.getTask();
            if (cause instanceof ReadImageException) {
                return String.format(getLocaleString("read_image"), ((ReadImageException) cause).getErrorImageFile(), cause.getCause().getMessage());
            } else if (cause instanceof PDFConversionException) {
                return String.format(getLocaleString("conversion"), cause.getCause().getMessage());
            } else if (cause instanceof SaveException) {
                SaveException ex2=(SaveException) cause;
                if (ex2.getCause() instanceof OverwriteException) {
                    return String.format(getLocaleString("overwrite"), task.getPdfDestination());
                } else if (ex2.getCause() instanceof InvalidFileException) {
                    return String.format(getLocaleString("invalid_file"), ex2.getCause().getMessage());
                }
            }
        }
        return doNext(data);
    }
}

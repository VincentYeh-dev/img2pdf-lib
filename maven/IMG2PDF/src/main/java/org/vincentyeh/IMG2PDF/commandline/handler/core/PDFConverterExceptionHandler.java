package org.vincentyeh.IMG2PDF.commandline.handler.core;


import org.vincentyeh.IMG2PDF.pdf.converter.exception.PDFConversionException;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.pdf.converter.exception.PDFConverterException;
import org.vincentyeh.IMG2PDF.pdf.converter.exception.ReadImageException;
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
            Task task = ex1.getTask();
            if (ex1.getCause() instanceof ReadImageException) {
                ReadImageException ex2 = (ReadImageException) ex1;
                return String.format(getLocaleString("read_image"), ex2.getErrorImageFile());
            } else if (ex1.getCause() instanceof OverwriteException) {
                return String.format(getLocaleString("overwrite"), task.getPdfDestination());
            } else if (ex1.getCause() instanceof PDFConversionException) {
                return String.format(getLocaleString("conversion"), ex1.getCause().getMessage());
            }
            else if (ex1.getCause() instanceof InvalidFileException) {
                return String.format(getLocaleString("invalid_file"), ex1.getCause().getMessage());
            }
        }
        return doNext(data);
    }
}

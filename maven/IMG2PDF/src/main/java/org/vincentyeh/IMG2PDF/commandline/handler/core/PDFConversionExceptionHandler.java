package org.vincentyeh.IMG2PDF.commandline.handler.core;


import org.vincentyeh.IMG2PDF.commandline.exception.PDFConversionException;
import org.vincentyeh.IMG2PDF.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.pattern.Handler;

public class PDFConversionExceptionHandler extends ResourceBundleHandler<String,Exception>{

    public PDFConversionExceptionHandler(Handler<String, Exception> next) {
        super(next, "conversion");
    }

    @Override
    public String handle(Exception data) {
        if (data instanceof PDFConversionException) {
            PDFConversionException ex1 = (PDFConversionException) data;
            if (ex1.getCause() instanceof PDFConverter.ReadImageException) {
                PDFConverter.ReadImageException ex2 = (PDFConverter.ReadImageException) data.getCause();
                return String.format(getLocaleString("read_image"), ex2.getFile());
            } else if (ex1.getCause() instanceof PDFConverter.OverwriteDenyException) {
                return String.format(getLocaleString("overwrite"), ex1.getTask().getPdfDestination());
            } else if (ex1.getCause() instanceof PDFConverter.ConversionException) {
                return String.format(getLocaleString("conversion"), ex1.getCause().getCause().getMessage());
            }
        }
        return doNext(data);
    }
}

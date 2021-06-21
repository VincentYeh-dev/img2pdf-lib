package org.vincentyeh.IMG2PDF.pdf.converter.exception;

import org.vincentyeh.IMG2PDF.task.Task;

public class PDFConversionException extends PDFConverterException{

    public PDFConversionException(Task task, Throwable cause) {
        super(String.format("Error occur during conversion:%s", cause.getMessage()),task);
    }

}

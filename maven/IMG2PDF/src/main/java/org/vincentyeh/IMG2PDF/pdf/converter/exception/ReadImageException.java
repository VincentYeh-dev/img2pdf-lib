package org.vincentyeh.IMG2PDF.pdf.converter.exception;

import org.vincentyeh.IMG2PDF.task.Task;

import java.io.File;

public class ReadImageException extends PDFConverterException{
    private final File image_file;

    public ReadImageException(File image_file, Throwable cause, Task task) {
        super(String.format("Unable to import image:%s",image_file),cause,task);
        this.image_file = image_file;
    }

    public File getErrorImageFile() {
        return image_file;
    }
}

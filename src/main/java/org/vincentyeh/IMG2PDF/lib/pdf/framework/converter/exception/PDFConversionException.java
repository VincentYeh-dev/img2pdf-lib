package org.vincentyeh.IMG2PDF.lib.pdf.framework.converter.exception;

import org.vincentyeh.IMG2PDF.lib.task.framework.Task;

public class PDFConversionException extends RuntimeException {
    private final Task task;

    public PDFConversionException(Task task, Throwable cause) {
        super(cause);
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}

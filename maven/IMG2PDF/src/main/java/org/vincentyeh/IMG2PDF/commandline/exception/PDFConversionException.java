package org.vincentyeh.IMG2PDF.commandline.exception;


import org.vincentyeh.IMG2PDF.task.Task;

public class PDFConversionException extends Exception {
    private final Task task;

    public PDFConversionException(Throwable cause, Task task) {
        super(cause);
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}

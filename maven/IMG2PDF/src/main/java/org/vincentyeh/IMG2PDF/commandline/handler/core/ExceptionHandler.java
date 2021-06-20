package org.vincentyeh.IMG2PDF.commandline.handler.core;

import org.vincentyeh.IMG2PDF.pattern.Handler;

public abstract class ExceptionHandler extends ResourceBundleHandler<Exception> {
    private final String exception_name;

    public ExceptionHandler(Handler<String, Exception> next, String exception_name) {
        super(next, "exception");
        this.exception_name = exception_name;
    }

    public ExceptionHandler(Handler<String, Exception> next,Class<? extends Exception> exp) {
        super(next, "exception");
        this.exception_name = exp.getSimpleName().toLowerCase();
    }

    @Override
    protected String getLocaleString(String key) {
        return super.getLocaleString(exception_name+"."+key);
    }
}

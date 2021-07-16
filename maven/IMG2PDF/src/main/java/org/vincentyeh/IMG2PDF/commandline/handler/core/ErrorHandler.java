package org.vincentyeh.IMG2PDF.commandline.handler.core;

import org.vincentyeh.IMG2PDF.pattern.Handler;

import static java.lang.String.format;

public class ErrorHandler extends ResourceBundleHandler<Error>{
    public ErrorHandler(Handler<String, Error> next) {
        super(next, "error");
    }

    @Override
    public String handle(Error data) throws CantHandleException {
         if(data instanceof OutOfMemoryError){
            return format(getLocaleString("OutOfMemoryError"),data.getMessage());
         }
        return null;
    }
}

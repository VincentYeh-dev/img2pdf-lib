package org.vincentyeh.IMG2PDF.commandline.handler.core;


import org.vincentyeh.IMG2PDF.pattern.Handler;
import picocli.CommandLine;

import java.util.stream.Collectors;

public class UnmatchedArgumentExceptionHandler extends ExceptionHandler{

    public UnmatchedArgumentExceptionHandler(Handler<String, Exception> next) {
        super(next,"unmatched_argument");
    }

    @Override
    public String handle(Exception data) throws CantHandleException {
        if (data instanceof CommandLine.UnmatchedArgumentException) {
            CommandLine.UnmatchedArgumentException exception = (CommandLine.UnmatchedArgumentException) data;
            String list = exception.getUnmatched().stream().map(str -> '\'' + str + '\'').collect(Collectors.joining(", "));
            return String.format(getLocaleString("unknown_option"), list);
        } else {
            return doNext(data);
        }
    }
}

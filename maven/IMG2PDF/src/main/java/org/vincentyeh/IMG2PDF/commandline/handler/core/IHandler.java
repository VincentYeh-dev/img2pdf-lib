package org.vincentyeh.IMG2PDF.commandline.handler.core;


public interface IHandler<T>{
    void parse(Exception e);
    String getErrorMessage();
    int getExitCode();
    boolean canHandle(T condition);
}

package org.vincentyeh.IMG2PDF.commandline.handler.core;

public interface HandlerRegister<T extends IHandler<?>> {
    void registerHandler(T handler);
}

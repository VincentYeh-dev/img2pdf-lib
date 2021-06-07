package org.vincentyeh.IMG2PDF.commandline.handler.core;

public interface IHandlerRegister<T extends IHandler<?>> {
    void registerHandler(T handler);
}

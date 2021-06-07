package org.vincentyeh.IMG2PDF.commandline.handler.core;

import java.util.ArrayList;
import java.util.List;

public abstract class ListHandlerRegister<T extends IHandler<?>> implements HandlerRegister<T> {
    protected List<T> handlers = new ArrayList<>();

    @Override
    public void registerHandler(T handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler==null");
        }
        handlers.add(handler);
    }
}

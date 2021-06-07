package org.vincentyeh.IMG2PDF.commandline.handler.core;

public abstract class ClassHandler implements IHandler<Class<?>> {

    private final Class<?> clazz;

    public ClassHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean canHandle(Exception exception, Class<?> condition) {
        return condition.equals(clazz);
    }
}

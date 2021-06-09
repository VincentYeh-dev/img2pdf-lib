package org.vincentyeh.IMG2PDF.pattern;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HandlerRegister<H extends Handler<?,?>> {
    private final List<Class<? extends H>> handlerClasses = new ArrayList<>();

    public void registerHandler(Class<? extends H> clazz) {
        handlerClasses.add(clazz);
    }

    public H getHandler() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Constructor<? extends H>> constructors = handlerClasses.stream()
                .map(c -> {
                    try {
                        return c.getConstructor(Handler.class);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList());

        H last = null;
        for (Constructor<? extends H> constructor : constructors) {
            last = constructor.newInstance(last);
        }

        return last;
    }
}

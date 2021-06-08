package org.vincentyeh.IMG2PDF.pattern;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HandlerRegister<T,R> {
    private final List<Class<? extends Handler<T, R>>> handlerClasses = new ArrayList<>();

    public void registerHandler(Class<? extends Handler<T, R>> clazz) {
        handlerClasses.add(clazz);
    }

    public Handler<T,R> getHandler() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Constructor<?>> constructors = handlerClasses.stream()
                .map(c -> {
                    try {
                        return c.getConstructor(Handler.class);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList());

        Handler<T,R> last = null;
        for (Constructor<?> constructor : constructors) {
            last = (Handler<T,R>) constructor.newInstance(last);
        }

        return last;
    }
}

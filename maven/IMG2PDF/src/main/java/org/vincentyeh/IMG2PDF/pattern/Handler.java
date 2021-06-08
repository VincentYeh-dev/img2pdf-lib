package org.vincentyeh.IMG2PDF.pattern;

/**
 * Using Chain of Responsibility.
 * URL:https://openhome.cc/Gossip/DesignPattern/ChainofResponsibility.htm
 * @param <T> T is return type.
 * @param <R> R is value type.
 */
public abstract class Handler<T,R>{
    protected Handler<T,R> next;

    public Handler(Handler<T,R> next) {
        this.next = next;
    }

    public T doNext(R data,T default_value) {
        if (next != null) {
            return next.handle(data);
        }
        return default_value;
    }

    public abstract T handle(R data);
}
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

    public T doNext(R data) {
        if (next != null) {
            return next.handle(data);
        }
        throw new CantHandleException("Can't handle.");
    }

    public abstract T handle(R data);

    public static class CantHandleException extends RuntimeException{
        public CantHandleException(String message) {
            super(message);
        }
    }
}
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

    protected T doNext(R data) throws CantHandleException {
        if (next != null) {
            return next.handle(data);
        }
        throw new CantHandleException("Can't handle.");
    }

    public abstract T handle(R data) throws CantHandleException;

    public static class CantHandleException extends Exception{
        public CantHandleException(String message) {
            super(message);
        }
    }
}
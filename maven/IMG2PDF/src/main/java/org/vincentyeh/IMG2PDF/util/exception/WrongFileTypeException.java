package org.vincentyeh.IMG2PDF.util.exception;


public class WrongFileTypeException extends Exception{

    public enum Type {
        FOLDER, FILE
    }

    private final Type expected;
    private final Type value;

    public Type getExpected() {
        return expected;
    }

    public Type getValue() {
        return value;
    }

    public WrongFileTypeException(Type expected, Type value) {
        super(value + "!=" + expected + "(expected)");
        this.expected = expected;
        this.value = value;
    }
}

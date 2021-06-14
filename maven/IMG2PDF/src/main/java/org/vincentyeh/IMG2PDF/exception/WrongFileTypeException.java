package org.vincentyeh.IMG2PDF.exception;


public class WrongFileTypeException extends RuntimeException {

    public enum Type {
        FOLDER, FILE
    }

    private final WrongFileTypeException.Type expected;
    private final WrongFileTypeException.Type value;

    public WrongFileTypeException.Type getExpected() {
        return expected;
    }

    public WrongFileTypeException.Type getValue() {
        return value;
    }

    public WrongFileTypeException(Type expected, Type value) {
        super(value + "!=" + expected + "(expected)");
        this.expected = expected;
        this.value = value;
    }
}

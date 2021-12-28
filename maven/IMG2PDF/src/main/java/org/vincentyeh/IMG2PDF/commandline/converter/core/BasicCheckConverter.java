package org.vincentyeh.IMG2PDF.commandline.converter.core;

import picocli.CommandLine;

public abstract class BasicCheckConverter<T> implements CommandLine.ITypeConverter<T> {
    protected abstract T doConvert(String s);

    private void checkNull(String string) {
        if (string == null)
            throw new IllegalArgumentException(getClass().getName() + ".s is null.");
    }

    private void checkEmpty(String string) {
        if (string != null && string.isEmpty())
            throw new IllegalArgumentException(getClass().getName() + ".s is empty.");
    }

    @Override
    public final T convert(String s) throws Exception {
        try {
            checkNull(s);
            checkEmpty(s);
            return doConvert(s);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }

}

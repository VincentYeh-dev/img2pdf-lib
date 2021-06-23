package org.vincentyeh.IMG2PDF.util.interfaces;

/**
 * @param <D> Data type
 */
public abstract class NameFormatter<D> {
    protected final String pattern;

    public NameFormatter(String pattern) {
        this.pattern = pattern;
        if (pattern==null)
            throw new IllegalArgumentException("data==null");
    }

    public abstract String format(D data) throws Exception;
}

package org.vincentyeh.IMG2PDF.util.interfaces;

/**
 * @param <D> Data type
 */
public abstract class NameFormatter<D> {
    private final D data;

    public NameFormatter(D data) {
        if (data==null)
            throw new IllegalArgumentException("data==null");
        this.data = data;
    }

    public abstract String format(String pattern) throws Exception;

    public D getData() {
        return data;
    }
}

package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface IDocument {
    void addPage(IPage page);

    void saveAndClose(OutputStream outputStream) throws IOException;
    void saveAndClose(File destination) throws IOException;

    int getPageCount();

    /** @deprecated Use {@link #saveAndClose(OutputStream)} instead. */
    @Deprecated
    default void save(OutputStream outputStream) throws IOException {
        saveAndClose(outputStream);
    }

    /** @deprecated Use {@link #saveAndClose(File)} instead. */
    @Deprecated
    default void save(File destination) throws IOException {
        saveAndClose(destination);
    }

    /**
     * @deprecated Resources are released by saveAndClose(). This is a no-op for backward compatibility.
     */
    @Deprecated
    default void close() {
        // no-op：saveAndClose 已在內部完成資源釋放
    }
}

package org.vincentyeh.img2pdf.lib.pdf.framework.objects;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface IDocument {
    void addPage(IPage page);

    void save(OutputStream outputStream) throws IOException;
    void save(File destination) throws IOException;

    void close() throws IOException;
    int getPageCount();
}

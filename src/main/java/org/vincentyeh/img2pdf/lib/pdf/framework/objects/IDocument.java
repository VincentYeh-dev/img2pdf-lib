package org.vincentyeh.img2pdf.lib.pdf.framework.objects;

import java.io.File;
import java.io.IOException;

public interface IDocument {
    void addPage(IPage page);
    void save(File destination) throws IOException;

    void close() throws IOException;
    int getPageCount();
}

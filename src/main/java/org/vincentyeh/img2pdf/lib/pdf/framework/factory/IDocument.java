package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface IDocument {
    void addPage(IPage page);

    void saveAndClose(OutputStream outputStream) throws IOException;
    void saveAndClose(File destination) throws IOException;

    int getPageCount();
}

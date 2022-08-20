package org.vincentyeh.img2pdf.lib.pdf.framework.objects;

import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public interface PdfDocument<DOCUMENT> extends Closeable {
    void setUserPassword(String userPassword);

    void setOwnerPassword(String ownerPassword);

    void setPermission(Permission permission);

    void setInfo(PDFDocumentInfo info);

    void encrypt() throws IOException;

    void addPage(PdfPage<?> page) throws Exception;

    void save(File file) throws IOException;

    DOCUMENT get();
}

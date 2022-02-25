package org.vincentyeh.IMG2PDF.pdf.framework.listener;


import java.io.File;

public interface ImagePDFCreationListener extends PDFCreationListener {
    void onAppendingImage(int index, int total, File file);
}

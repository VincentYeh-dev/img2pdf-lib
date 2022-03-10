package org.vincentyeh.IMG2PDF.pdf.function.listener;


import org.vincentyeh.IMG2PDF.pdf.framework.listener.PDFCreationListener;

import java.io.File;

public interface ImagePDFCreationListener extends PDFCreationListener {
    void onAppendingImage(int index, int total, File file);
}

package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import java.io.File;

public interface ImagePDFFactoryListener {

    void initializing(int length);


    void onConversionComplete();

    void onAppend(File file, int appendedCount, int length);
}

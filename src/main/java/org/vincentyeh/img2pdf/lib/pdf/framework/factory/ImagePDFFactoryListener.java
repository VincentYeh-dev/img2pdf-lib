package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import java.io.File;

public interface ImagePDFFactoryListener {

    void initializing(int procedure_id, int length);

    void onSaved(int procedure_id, File destination);

    void onConversionComplete(int procedure_id);

    void onAppend(int procedure_id, File file, int i, int length);
}

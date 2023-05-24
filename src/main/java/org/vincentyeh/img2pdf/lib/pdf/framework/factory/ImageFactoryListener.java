package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import java.io.File;

public interface ImageFactoryListener {

    void initializing(long procedure_id);

    void onSaved(long procedure_id, File destination);

    void onConversionComplete(long procedure_id);

    void onAppend(long procedure_id, int index, int total);

}

package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IDocument;

import java.io.File;

public interface ImagePDFFactory {
    IDocument start(File[] imageFiles, ColorType colorType) throws PDFFactoryException;

    IDocument start(File[] imageFiles, ColorType colorType, ImagePDFFactoryListener listener);


//    IDocument start(int procedure_id, File directory, FileFilter filter,
//                    Comparator<File> fileSorter, ImagePDFFactoryListener listener) throws PDFFactoryException;
//
//    IDocument start(int procedure_id, File directory, FileFilter filter,
//                    Comparator<File> fileSorter) throws PDFFactoryException;

    void shutdown();
}

package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IDocument;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;

public interface ImagePDFFactory {
    IDocument start(int procedure_id, File[] imageFiles, ImagePDFFactoryListener listener) throws PDFFactoryException;

    IDocument start(int procedure_id, File[] imageFiles) throws PDFFactoryException;


    IDocument start(int procedure_id, File directory, FileFilter filter,
                    Comparator<File> fileSorter, ImagePDFFactoryListener listener) throws PDFFactoryException;

    IDocument start(int procedure_id, File directory, FileFilter filter,
                    Comparator<File> fileSorter) throws PDFFactoryException;

    void shutdown();
}

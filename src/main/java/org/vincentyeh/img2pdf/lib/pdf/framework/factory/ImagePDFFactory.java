package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;

public interface ImagePDFFactory {
    File start(int procedure_id, File[] imageFiles, File destination, ImageFactoryListener listener) throws PDFFactoryException;

    File start(int procedure_id, File[] imageFiles, File destination) throws PDFFactoryException;


    File start(int procedure_id, File directory, FileFilter filter,
               Comparator<File> fileSorter, File destination, ImageFactoryListener listener) throws PDFFactoryException;

    File start(int procedure_id, File directory, FileFilter filter,
               Comparator<File> fileSorter, File destination) throws PDFFactoryException;
}

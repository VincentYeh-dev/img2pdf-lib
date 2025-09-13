package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.io.File;

public interface ImagePDFFactory {
    IDocument start(File[] imageFiles,
                    ColorType colorType,
                    DocumentArgument documentArgument,
                    PageArgument pageArgument) throws PDFFactoryException;


    IDocument start(File[] imageFiles,
                    ColorType colorType,
                    DocumentArgument documentArgument,
                    PageArgument pageArgument,
                    ImagePDFFactoryListener listener) throws PDFFactoryException;


    void shutdown();
}

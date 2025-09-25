package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.TemplateImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;

public class OpenPDFImagePDFFactory extends TemplateImagePDFFactory {

    public OpenPDFImagePDFFactory(ImageScalingStrategy imageScalingStrategy, ImageReader imageReader, int nThreads) {
        super(imageScalingStrategy, imageReader, nThreads);
    }

    @Override
    protected IDocument createDocument(DocumentArgument argument) {
        return new OpenPDFDocumentAdaptor(argument);
    }

    @Override
    protected IPage createPage(int pageNumber, SizeF pageSize) {
        return new OpenPDFPageAdaptor(pageNumber, pageSize);
    }

    @Override
    protected boolean parallelProcessingSupported() {
        return true;
    }
}

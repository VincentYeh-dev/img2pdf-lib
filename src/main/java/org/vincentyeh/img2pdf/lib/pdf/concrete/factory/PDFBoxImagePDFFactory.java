package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import org.vincentyeh.img2pdf.lib.pdf.concrete.object.PDFBoxDocumentAdaptor;
import org.vincentyeh.img2pdf.lib.pdf.concrete.object.PDFBoxPageAdaptor;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageReadImpl;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

public class PDFBoxImagePDFFactory extends DefaultImagePDFFactory {
    public PDFBoxImagePDFFactory(PageArgument pageArgument, DocumentArgument documentArgument, ImageReadImpl imageReadImpl) {
        super(pageArgument, documentArgument, imageReadImpl);
    }

    public PDFBoxImagePDFFactory(PageArgument pageArgument, DocumentArgument documentArgument, ImageReadImpl imageReadImpl, ImageScalingStrategy imageScalingStrategy) {
        super(pageArgument, documentArgument, imageReadImpl, imageScalingStrategy);
    }

    @Override
    public IDocument createDocument(DocumentArgument argument) {
        return new PDFBoxDocumentAdaptor(argument);
    }
    @Override
    public IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) {
        PDFBoxDocumentAdaptor document = (PDFBoxDocumentAdaptor) pdfDocument;
        return new PDFBoxPageAdaptor(document.getInternalDocument(), pageNumber, pageSize);
    }
}

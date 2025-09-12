package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import com.drew.lang.annotations.NotNull;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.image.ImageUtils;
import org.vincentyeh.img2pdf.lib.pdf.concrete.object.PDFBoxDocumentAdaptor;
import org.vincentyeh.img2pdf.lib.pdf.concrete.object.PDFBoxPageAdaptor;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.awt.image.BufferedImage;
import java.io.File;

public class PDFBoxImagePDFFactory extends TemplateImagePDFFactory {

    public PDFBoxImagePDFFactory(@NotNull PageArgument pageArgument,
                                 @NotNull DocumentArgument documentArgument,
                                 @NotNull ImageScalingStrategy imageScalingStrategy) {
        super(pageArgument, documentArgument, imageScalingStrategy);
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

    @Override
    public BufferedImage readImage(File imageFile, ColorType colorType) {
        return ImageUtils.readImage(imageFile, colorType);
    }

}

package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import com.drew.lang.annotations.NotNull;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.image.ImageUtils;
import org.vincentyeh.img2pdf.lib.pdf.concrete.object.PDFBoxDocumentAdaptor;
import org.vincentyeh.img2pdf.lib.pdf.concrete.object.PDFBoxPageAdaptor;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;

import java.awt.image.BufferedImage;
import java.io.File;

public class PDFBoxImagePDFFactory extends TemplateImagePDFFactory {

    private final MemoryUsageSetting memoryUsageSetting;

    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy) {
        this(imageScalingStrategy, Runtime.getRuntime().availableProcessors(), true);
    }

    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy, int nThreads) {
        this(imageScalingStrategy, nThreads, true);
    }

    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy, int nThreads, boolean useMemoryOnly) {
        super(imageScalingStrategy, nThreads);
        if (useMemoryOnly)
            this.memoryUsageSetting = MemoryUsageSetting.setupMainMemoryOnly();
        else
            this.memoryUsageSetting = MemoryUsageSetting.setupTempFileOnly();
    }

    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy, int nThreads,
                                 long maxMainMemoryBytes,
                                 long maxStorageBytes) {
        super(imageScalingStrategy, nThreads);
        this.memoryUsageSetting = MemoryUsageSetting.setupMixed(maxMainMemoryBytes, maxStorageBytes);
    }

    @Override
    protected IDocument createDocument(DocumentArgument argument) {
        return new PDFBoxDocumentAdaptor(argument, memoryUsageSetting);
    }

    @Override
    protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) {
        PDFBoxDocumentAdaptor document = (PDFBoxDocumentAdaptor) pdfDocument;
        return new PDFBoxPageAdaptor(document.getInternalDocument(), pageNumber, pageSize);
    }

    @Override
    protected BufferedImage readImage(File imageFile, ColorType colorType) {
        return ImageUtils.readImage(imageFile, colorType);
    }

}

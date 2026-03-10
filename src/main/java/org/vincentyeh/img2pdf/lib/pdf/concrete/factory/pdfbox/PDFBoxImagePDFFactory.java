package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.vincentyeh.img2pdf.lib.annotation.NotNull;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.TemplateImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;

public class PDFBoxImagePDFFactory extends TemplateImagePDFFactory {

    private final MemoryUsageSetting memoryUsageSetting;

    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy,
                                 @NotNull ImageReader imageReader) {
        this(imageScalingStrategy, imageReader, Runtime.getRuntime().availableProcessors(), true);
    }

    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy, @NotNull ImageReader imageReader, int nThreads) {
        this(imageScalingStrategy, imageReader, nThreads, true);
    }

    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy, @NotNull ImageReader imageReader, int nThreads, boolean useMemoryOnly) {
        super(imageScalingStrategy, imageReader, nThreads);
        if (useMemoryOnly)
            this.memoryUsageSetting = MemoryUsageSetting.setupMainMemoryOnly();
        else
            this.memoryUsageSetting = MemoryUsageSetting.setupTempFileOnly();
    }

    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy,
                                 @NotNull ImageReader imageReader,
                                 int nThreads,
                                 long maxMainMemoryBytes,
                                 long maxStorageBytes) {
        super(imageScalingStrategy, imageReader, nThreads);
        this.memoryUsageSetting = MemoryUsageSetting.setupMixed(maxMainMemoryBytes, maxStorageBytes);
    }

    @Override
    protected IDocument createDocument(DocumentArgument argument) {
        return new PDFBoxDocumentAdaptor(argument, memoryUsageSetting);
    }

    @Override
    protected IPage createPage(int pageNumber, SizeF pageSize) {
        return new PDFBoxPageAdaptor(pageNumber, pageSize);
    }

    @Override
    protected boolean parallelProcessingSupported() {
        return true;
    }

}

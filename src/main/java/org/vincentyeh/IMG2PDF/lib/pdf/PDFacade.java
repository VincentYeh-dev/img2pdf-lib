package org.vincentyeh.IMG2PDF.lib.pdf;

import org.vincentyeh.IMG2PDF.lib.image.helper.concrete.ColorSpaceImageHelper;
import org.vincentyeh.IMG2PDF.lib.image.helper.concrete.DirectionImageHelper;
import org.vincentyeh.IMG2PDF.lib.pdf.concrete.appender.ExecutorPageAppender;
import org.vincentyeh.IMG2PDF.lib.pdf.concrete.calculation.strategy.StandardImagePageCalculationStrategy;
import org.vincentyeh.IMG2PDF.lib.pdf.concrete.converter.ImageHelperPDFCreatorImpl;
import org.vincentyeh.IMG2PDF.lib.pdf.concrete.converter.PDFBoxCreatorImpl;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.converter.PDFCreator;
import org.vincentyeh.IMG2PDF.lib.pdf.function.converter.ImagePDFCreator;
import org.vincentyeh.IMG2PDF.lib.util.file.exception.MakeDirectoryException;

import java.awt.color.ColorSpace;
import java.io.File;

public class PDFacade {
    private PDFacade() {

    }

    public static PDFCreator createImagePDFConverter(long bytes_count, File tempFolder, boolean overwrite_output, ColorSpace colorSpace, int nThread) throws MakeDirectoryException {
        var pdfBoxCreatorImpl = new PDFBoxCreatorImpl(tempFolder, bytes_count);
        var imagePDFCreatorImpl = new ImageHelperPDFCreatorImpl(new DirectionImageHelper(new ColorSpaceImageHelper(colorSpace)));
        var appender = new ExecutorPageAppender(nThread);
        return new ImagePDFCreator(pdfBoxCreatorImpl,imagePDFCreatorImpl, appender, overwrite_output, new StandardImagePageCalculationStrategy());
    }
}

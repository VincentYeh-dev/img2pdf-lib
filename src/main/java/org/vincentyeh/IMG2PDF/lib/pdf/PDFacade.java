package org.vincentyeh.IMG2PDF.lib.pdf;

import org.vincentyeh.IMG2PDF.lib.image.helper.concrete.ColorSpaceImageHelper;
import org.vincentyeh.IMG2PDF.lib.image.helper.concrete.DirectionImageHelper;
import org.vincentyeh.IMG2PDF.lib.pdf.concrete.calculation.strategy.StandardImagePageCalculationStrategy;
import org.vincentyeh.IMG2PDF.lib.pdf.function.converter.ImagePDFCreatorImpl;
import org.vincentyeh.IMG2PDF.lib.util.file.exception.MakeDirectoryException;
import org.vincentyeh.IMG2PDF.lib.pdf.concrete.appender.ExecutorPageAppender;
import org.vincentyeh.IMG2PDF.lib.pdf.concrete.converter.ImageHelperPDFCreatorImpl;
import org.vincentyeh.IMG2PDF.lib.pdf.concrete.converter.PDFBoxCreatorImpl;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.converter.PDFCreator;
import org.vincentyeh.IMG2PDF.lib.pdf.function.converter.ImagePDFCreator;

import java.awt.color.ColorSpace;
import java.io.File;

public class PDFacade {
    private PDFacade() {

    }

    public static PDFCreator createImagePDFConverter(long bytes_count, File tempFolder, boolean overwrite_output, ColorSpace colorSpace, int nThread) throws MakeDirectoryException {
        PDFBoxCreatorImpl pdfBoxCreatorImpl = new PDFBoxCreatorImpl(tempFolder, bytes_count);
        ImagePDFCreatorImpl imagePDFCreatorImpl = new ImageHelperPDFCreatorImpl(new DirectionImageHelper(new ColorSpaceImageHelper(colorSpace)));
        ExecutorPageAppender appender = new ExecutorPageAppender(nThread);

        PDFCreator converter = new ImagePDFCreator(pdfBoxCreatorImpl,imagePDFCreatorImpl, appender, overwrite_output, new StandardImagePageCalculationStrategy());

        return converter;
    }
}

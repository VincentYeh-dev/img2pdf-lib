package org.vincentyeh.IMG2PDF.pdf;

import org.vincentyeh.IMG2PDF.image.helper.concrete.ColorSpaceImageHelper;
import org.vincentyeh.IMG2PDF.image.helper.concrete.DirectionImageHelper;
import org.vincentyeh.IMG2PDF.pdf.concrete.appender.ExecutorPageAppender;
import org.vincentyeh.IMG2PDF.pdf.concrete.calculation.strategy.StandardImagePageCalculationStrategy;
import org.vincentyeh.IMG2PDF.pdf.concrete.converter.ImageHelperPDFCreatorImpl;
import org.vincentyeh.IMG2PDF.pdf.concrete.converter.PDFBoxCreatorImpl;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.PDFCreator;
import org.vincentyeh.IMG2PDF.pdf.function.converter.ImagePDFCreator;
import org.vincentyeh.IMG2PDF.pdf.function.converter.ImagePDFCreatorImpl;
import org.vincentyeh.IMG2PDF.util.file.exception.MakeDirectoryException;

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

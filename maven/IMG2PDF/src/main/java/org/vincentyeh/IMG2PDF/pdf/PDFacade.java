package org.vincentyeh.IMG2PDF.pdf;

import org.vincentyeh.IMG2PDF.image.helper.concrete.ColorSpaceImageHelper;
import org.vincentyeh.IMG2PDF.image.helper.concrete.DirectionImageHelper;
import org.vincentyeh.IMG2PDF.pdf.concrete.appender.ExecutorPageAppender;
import org.vincentyeh.IMG2PDF.pdf.concrete.calculation.strategy.StandardImagePageCalculationStrategy;
import org.vincentyeh.IMG2PDF.pdf.concrete.converter.PDFBoxCreatorImpl;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.PDFCreator;
import org.vincentyeh.IMG2PDF.pdf.framework.listener.PDFCreationListener;
import org.vincentyeh.IMG2PDF.pdf.function.converter.ImagePDFCreator;
import org.vincentyeh.IMG2PDF.util.file.exception.MakeDirectoryException;

import java.awt.color.ColorSpace;
import java.io.File;

public class PDFacade {
    private PDFacade() {

    }

    public static PDFCreator<?> createImagePDFConverter(long bytes_count, File tempFolder, boolean overwrite_output, PDFCreationListener listener, ColorSpace colorSpace, int nThread) throws MakeDirectoryException {
        PDFBoxCreatorImpl impl = new PDFBoxCreatorImpl(tempFolder, bytes_count);
        ExecutorPageAppender appender = new ExecutorPageAppender(nThread);
        appender.setPageAppendListener((index) -> {
            if (listener != null)
                listener.onPageAppended(index);
        });
        PDFCreator<PDFCreationListener> converter = new ImagePDFCreator(impl, appender, overwrite_output, new StandardImagePageCalculationStrategy(), new DirectionImageHelper(new ColorSpaceImageHelper(colorSpace)));
        converter.setListener(listener);
        return converter;
    }
}

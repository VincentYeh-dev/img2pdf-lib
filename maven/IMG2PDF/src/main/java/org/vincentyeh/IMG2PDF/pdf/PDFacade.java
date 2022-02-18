package org.vincentyeh.IMG2PDF.pdf;

import org.vincentyeh.IMG2PDF.image.helper.concrete.DirectionImageHelper;
import org.vincentyeh.IMG2PDF.image.helper.concrete.ColorSpaceImageHelper;
import org.vincentyeh.IMG2PDF.pdf.concrete.calculation.strategy.StandardImagePageCalculationStrategy;
import org.vincentyeh.IMG2PDF.pdf.concrete.converter.ImagePDFConverter;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.PDFConverter;
import org.vincentyeh.IMG2PDF.pdf.framework.listener.ConversionListener;
import org.vincentyeh.IMG2PDF.util.file.exception.MakeDirectoryException;

import java.awt.color.ColorSpace;
import java.io.File;

public class PDFacade {
    private PDFacade() {

    }

    public static PDFConverter createImagePDFConverter(long bytes_count, File tempFolder, boolean overwrite_output, ConversionListener listener, ColorSpace colorSpace) throws MakeDirectoryException {
        PDFConverter converter = new ImagePDFConverter(bytes_count, tempFolder, overwrite_output, new StandardImagePageCalculationStrategy(),new DirectionImageHelper(new ColorSpaceImageHelper(colorSpace)));
        converter.setListener(listener);
        return converter;
    }
}

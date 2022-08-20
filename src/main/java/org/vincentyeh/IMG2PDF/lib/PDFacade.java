package org.vincentyeh.img2pdf.lib;

import org.vincentyeh.img2pdf.lib.image.reader.concrete.ColorSpaceImageReader;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.DirectionImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.appender.ExecutorPageAppender;
import org.vincentyeh.img2pdf.lib.pdf.concrete.calculation.strategy.StandardImagePageCalculationStrategy;
import org.vincentyeh.img2pdf.lib.pdf.concrete.converter.ImageReaderReadImpl;
import org.vincentyeh.img2pdf.lib.pdf.concrete.converter.PDFBoxImpl;
import org.vincentyeh.img2pdf.lib.pdf.function.converter.ImagePDFCreator;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.awt.color.ColorSpace;
import java.io.File;

public class PDFacade {
    private PDFacade() {

    }

    public static ImagePDFCreator createImagePDFConverter(PageArgument pageArgument, DocumentArgument documentArgument, long bytes_count, File tempFolder, boolean overwrite_output, ColorSpace colorSpace, int nThread){
        var pdfBoxCreatorImpl = new PDFBoxImpl(tempFolder, bytes_count);
        var imageReadImpl = new ImageReaderReadImpl(new DirectionImageReader(new ColorSpaceImageReader(colorSpace)));
        var appender = new ExecutorPageAppender(nThread);
        return new ImagePDFCreator(pageArgument,documentArgument,pdfBoxCreatorImpl,imageReadImpl, appender, overwrite_output, new StandardImagePageCalculationStrategy());
    }
}

package org.vincentyeh.img2pdf.lib;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.ColorSpaceImageReader;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.DirectionImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.builder.PDFBoxBuilder;
import org.vincentyeh.img2pdf.lib.pdf.concrete.calculation.strategy.StandardImagePageCalculationStrategy;
import org.vincentyeh.img2pdf.lib.pdf.concrete.converter.ImageReaderReadImpl;
import org.vincentyeh.img2pdf.lib.pdf.function.converter.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.awt.color.ColorSpace;
import java.io.File;

public class PDFacade {
    private PDFacade() {

    }

    public static ImagePDFFactory createImagePDFConverter(PageArgument pageArgument, DocumentArgument documentArgument,
                                                          long bytes_count, File tempFolder, boolean overwrite_output,
                                                          ColorSpace colorSpace, int nThread) {
        var setting = MemoryUsageSetting.setupMixed(bytes_count).setTempDir(tempFolder);

        var imageReadImpl = new ImageReaderReadImpl(new DirectionImageReader(new ColorSpaceImageReader(colorSpace)));
        return new ImagePDFFactory(pageArgument, documentArgument, new PDFBoxBuilder(setting), imageReadImpl,
                overwrite_output, new StandardImagePageCalculationStrategy());
    }
}

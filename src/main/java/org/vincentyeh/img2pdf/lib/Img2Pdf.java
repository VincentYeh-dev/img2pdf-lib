package org.vincentyeh.img2pdf.lib;

import org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.DefaultImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf.OpenPDFImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox.PDFBoxImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;

public class Img2Pdf {

    private Img2Pdf() {

    }

    public static ImagePDFFactory createPDFBoxMaxPerformanceFactory() {
        ImageReader reader = ImageIOReader.getInstance();
        return new PDFBoxImagePDFFactory(new DefaultImageScalingStrategy(), reader);
    }

    public static ImagePDFFactory createPDFBoxFactory(int nThreads, long maxMainMemoryBytes, long maxStorageBytes) {
        ImageReader reader = ImageIOReader.getInstance();
        return new PDFBoxImagePDFFactory(new DefaultImageScalingStrategy(), reader,
                nThreads, maxMainMemoryBytes, maxStorageBytes);
    }

    public static ImagePDFFactory createOpenPDFFactory(int nThreads) {
        ImageReader reader = ImageIOReader.getInstance();
        return new OpenPDFImagePDFFactory(new DefaultImageScalingStrategy(), reader, nThreads);
    }


}

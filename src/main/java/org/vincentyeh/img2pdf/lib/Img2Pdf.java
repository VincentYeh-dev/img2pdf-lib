package org.vincentyeh.img2pdf.lib;

import org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.DefaultImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.PDFBoxImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;

public class Img2Pdf {

    private Img2Pdf() {

    }

    public static ImagePDFFactory createMaxPerformanceFactory() {
        ImageReader reader = ImageIOReader.getInstance();
        return new PDFBoxImagePDFFactory(new DefaultImageScalingStrategy(), reader);
    }

    public static ImagePDFFactory createFactory(int nThreads, long maxMainMemoryBytes, long maxStorageBytes) {
        ImageReader reader = ImageIOReader.getInstance();
        return new PDFBoxImagePDFFactory(new DefaultImageScalingStrategy(), reader,
                nThreads, maxMainMemoryBytes, maxStorageBytes);
    }


}

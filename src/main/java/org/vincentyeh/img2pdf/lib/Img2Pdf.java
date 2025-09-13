package org.vincentyeh.img2pdf.lib;

import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.DefaultImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.PDFBoxImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;

public class Img2Pdf {

    private Img2Pdf() {

    }

    public static ImagePDFFactory createMaxPerformanceFactory() {
        return new PDFBoxImagePDFFactory(new DefaultImageScalingStrategy());
    }

    public static ImagePDFFactory createFactory(int nThreads, long maxMainMemoryBytes, long maxStorageBytes) {
        return new PDFBoxImagePDFFactory(new DefaultImageScalingStrategy(), nThreads, maxMainMemoryBytes, maxStorageBytes);
    }


}

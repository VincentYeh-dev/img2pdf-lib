package org.vincentyeh.img2pdf;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.vincentyeh.img2pdf.image.ImageUtils;
import org.vincentyeh.img2pdf.image.ColorType;
import org.vincentyeh.img2pdf.pdf.concrete.builder.PDFBoxBuilder;
import org.vincentyeh.img2pdf.pdf.concrete.factory.DefaultImagePDFFactory;
import org.vincentyeh.img2pdf.pdf.framework.factory.ImageReadImpl;
import org.vincentyeh.img2pdf.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.pdf.parameter.PageArgument;

import java.io.File;

public class Img2Pdf {
    private static MemoryUsageSetting setting = MemoryUsageSetting.setupMainMemoryOnly();

    public static void setMainMemoryOnly() {
        setting = MemoryUsageSetting.setupMainMemoryOnly();
    }

    public static void setMainMemoryOnly(long buffer_limit_in_bytes) {
        setting = MemoryUsageSetting.setupMainMemoryOnly(buffer_limit_in_bytes);
    }

    public static void setTempFileOnly(File bufferFolder) {
        setting = MemoryUsageSetting.setupTempFileOnly().setTempDir(bufferFolder);
    }

    public static void setTempFileOnly(File bufferFolder, long buffer_limit_in_bytes) {
        setting = MemoryUsageSetting.setupTempFileOnly(buffer_limit_in_bytes).setTempDir(bufferFolder);
    }

    public static void setMixed(File bufferFolder, long main_memory_limit_in_bytes, long storage_limit_in_bytes) {
        setting = MemoryUsageSetting.setupMixed(main_memory_limit_in_bytes, storage_limit_in_bytes).setTempDir(bufferFolder);
    }


    private Img2Pdf() {

    }

    public static ImagePDFFactory createFactory(PageArgument pageArgument, DocumentArgument documentArgument,
                                                ColorType colorType, boolean overwrite_output) {

        final ImageReadImpl imageReadImpl = (file) -> ImageUtils.readImage(file, colorType);

        return new DefaultImagePDFFactory(pageArgument, documentArgument, imageReadImpl, new PDFBoxBuilder(setting), overwrite_output);
    }

}

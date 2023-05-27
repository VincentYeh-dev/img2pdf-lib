package org.vincentyeh.img2pdf.lib;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.vincentyeh.img2pdf.lib.image.ImageUtils;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.concrete.builder.PDFBoxBuilder;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.DefaultImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.FactoryImpl;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

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

        final FactoryImpl factoryImpl = (file) -> ImageUtils.readImage(file, colorType);

        return new DefaultImagePDFFactory(pageArgument, documentArgument, factoryImpl, new PDFBoxBuilder(setting), overwrite_output);
    }

}

package org.vincentyeh.img2pdf.lib;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.vincentyeh.img2pdf.lib.image.decorator.concrete.ColorConvertDecorator;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.MetaImageReader;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ColorType;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.builder.PDFBoxBuilder;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.StandardImagePageCalculationStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.FactoryImpl;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.io.File;

public class Img2Pdf {
    private static MemoryUsageSetting setting=MemoryUsageSetting.setupMainMemoryOnly();

    public static void setMainMemoryOnly() {
        setting = MemoryUsageSetting.setupMainMemoryOnly();
    }

    public static void setMainMemoryOnly(long buffer_limit_in_bytes) {
        setting = MemoryUsageSetting.setupMainMemoryOnly(buffer_limit_in_bytes);
    }

    public static void setTempFileOnly(File bufferFolder) {
        setting = MemoryUsageSetting.setupTempFileOnly().setTempDir(bufferFolder);
    }

    public static void setTempFileOnly(File bufferFolder,long buffer_limit_in_bytes) {
        setting = MemoryUsageSetting.setupTempFileOnly(buffer_limit_in_bytes).setTempDir(bufferFolder);
    }

    public static void setMixed(File bufferFolder,long main_memory_limit_in_bytes,long storage_limit_in_bytes) {
        setting = MemoryUsageSetting.setupMixed(main_memory_limit_in_bytes,storage_limit_in_bytes).setTempDir(bufferFolder);
    }


    private Img2Pdf() {

    }

    public static ImagePDFFactory createFactory(PageArgument pageArgument, DocumentArgument documentArgument,
                                                ColorType colorType, boolean overwrite_output) {
        ImageReader reader = new MetaImageReader();
        ColorConvertDecorator decorator = new ColorConvertDecorator(colorType, null);

        final FactoryImpl factoryImpl = (file) -> decorator.decorate(reader.read(file));

        return new ImagePDFFactory(pageArgument, documentArgument, factoryImpl, new PDFBoxBuilder(setting),
                new StandardImagePageCalculationStrategy(), overwrite_output);
    }

}

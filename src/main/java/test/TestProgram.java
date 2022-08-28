package test;


import org.apache.pdfbox.io.MemoryUsageSetting;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.ColorSpaceImageReader;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.DirectionImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.builder.PDFBoxBuilder;
import org.vincentyeh.img2pdf.lib.pdf.concrete.builder.TextPDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.StandardImagePageCalculationStrategy;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;

import java.awt.color.ColorSpace;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class TestProgram {

    public static void main(String[] args) throws IOException {
        var tempFolder= Files.createTempDirectory("org.vincentyeh.img2pdf-lib.tmp.").toFile();
        tempFolder.deleteOnExit();
//        var factory =
//                createImagePDFFactory(
//                        new PageArgument(PageSize.A4),
//                        new DocumentArgument("1234","5678")
//                        ,3*1024*1024,tempFolder, true,
//                        ColorSpace.getInstance(ColorSpace.CS_sRGB));

        var factory =
                createTestImagePDFFactory(
                        new PageArgument(PageSize.A4),
                        new DocumentArgument("1234","5678"),
                        true,ColorSpace.getInstance(ColorSpace.CS_sRGB));

        var files=new File("test").listFiles();
        factory.start(files,new File("ssss\\output.txt"),listener);
    }

    private static final ImagePDFFactory.Listener listener=new ImagePDFFactory.Listener() {
        @Override
        public void initializing(long procedure_id) {
            System.out.println("initializing:"+procedure_id);
        }

        @Override
        public void onSaved(long procedure_id, File destination) {
            System.out.println("onSaved:"+procedure_id+"\tDest:"+destination);
        }

        @Override
        public void onConversionComplete(long procedure_id) {
            System.out.println("onConversionComplete:"+procedure_id);
        }

        @Override
        public void onAppend(long procedure_id, int index) {
            System.out.println("onAppend:"+procedure_id+"\t Page:"+index);
        }
    };

    public static ImagePDFFactory createImagePDFFactory(PageArgument pageArgument, DocumentArgument documentArgument,
                                                        long bytes_count, File tempFolder, boolean overwrite_output,
                                                        ColorSpace colorSpace) {
        var setting = MemoryUsageSetting.setupMixed(bytes_count).setTempDir(tempFolder);

        var imageReader = new DirectionImageReader(new ColorSpaceImageReader(colorSpace));
        return new ImagePDFFactory(pageArgument, documentArgument, new PDFBoxBuilder(setting),
                imageReader,new StandardImagePageCalculationStrategy(),
                overwrite_output);
    }

    public static ImagePDFFactory createTestImagePDFFactory(PageArgument pageArgument, DocumentArgument documentArgument,
                                                        boolean overwrite_output,
                                                        ColorSpace colorSpace) throws FileNotFoundException {

        var imageReader = new DirectionImageReader(new ColorSpaceImageReader(colorSpace));
        return new ImagePDFFactory(pageArgument, documentArgument, new TextPDFBuilder(),
                imageReader,new StandardImagePageCalculationStrategy(),
                overwrite_output);
    }
}

package test;


import org.vincentyeh.img2pdf.lib.PDFacade;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;

import java.awt.color.ColorSpace;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestProgram {

    public static void main(String[] args) throws IOException {
        var tempFolder= Files.createTempDirectory("org.vincentyeh.img2pdf-lib.tmp.").toFile();
        tempFolder.deleteOnExit();
        var factory =
                PDFacade.createImagePDFFactory(
                        new PageArgument(PageSize.A4),
                        new DocumentArgument("1234","5678")
                        ,3*1024*1024,tempFolder, true,
                        ColorSpace.getInstance(ColorSpace.CS_GRAY));

        var files=new File("test").listFiles();
        factory.start(files,new File("ssss\\output.pdf"),listener);
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

}

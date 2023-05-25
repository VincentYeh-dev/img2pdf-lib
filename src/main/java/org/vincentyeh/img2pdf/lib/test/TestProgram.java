package org.vincentyeh.img2pdf.lib.test;


import org.vincentyeh.img2pdf.lib.Img2Pdf;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageFactoryListener;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;

import java.io.File;

public class TestProgram {

    public static void main(String[] args) {
        ImagePDFFactory factory = Img2Pdf.createFactory(
                new PageArgument(PageSize.A4),
                new DocumentArgument("1234", "5678")
                , ColorType.GRAY, true);

//        File[] files = new File("test").listFiles();
        factory.start(-1, new File("test"), null, null, new File("output2.pdf"), listener);
    }

    private static final ImageFactoryListener listener = new ImageFactoryListener() {
        @Override
        public void initializing(long procedure_id) {
            System.out.println("initializing:" + procedure_id);
        }

        @Override
        public void onSaved(long procedure_id, File destination) {
            System.out.println("onSaved:" + procedure_id + "\tDest:" + destination);
        }

        @Override
        public void onConversionComplete(long procedure_id) {
            System.out.println("onConversionComplete:" + procedure_id);
        }

        @Override
        public void onAppend(int procedure_id, File file, int i, int length) {
            System.out.println("onAppend:" + procedure_id +"\t image:"+file.getName());
        }

    };
}

package org.vincentyeh.img2pdf.lib.test;


import org.vincentyeh.img2pdf.lib.Img2Pdf;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactoryListener;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.parameter.*;

import java.io.File;

public class TestProgram {

    public static void main(String[] args) {
        PageArgument pageArgument=new PageArgument(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER,
                PageSize.A4, PageDirection.Portrait,true);

        ImagePDFFactory factory = Img2Pdf.createFactory(pageArgument
                ,
                new DocumentArgument("1234", "5678")
                , ColorType.GRAY, true);

//        File[] files = new File("test").listFiles();
        factory.start(-1, new File("test2"), null, null, new File("output2.pdf"), listener);
    }

    private static final ImagePDFFactoryListener listener = new ImagePDFFactoryListener() {

        @Override
        public void initializing(int procedure_id, int length) {
            System.out.println("initializing:" + procedure_id);
        }

        @Override
        public void onSaved(int procedure_id, File destination) {
            System.out.println("onSaved:" + procedure_id + "\tDest:" + destination);
        }

        @Override
        public void onConversionComplete(int procedure_id) {
            System.out.println("onConversionComplete:" + procedure_id);
        }

        @Override
        public void onAppend(int procedure_id, File file, int i, int length) {
            System.out.println("onAppend:" + procedure_id +"\t image:"+file.getName());
        }

    };
}

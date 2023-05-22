package org.vincentyeh.img2pdf.lib.test;


import org.vincentyeh.img2pdf.lib.Img2Pdf;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;

import java.io.File;
import java.io.IOException;

public class TestProgram {

    public static void main(String[] args) throws IOException {
        ImagePDFFactory factory = Img2Pdf.createFactory(
                new PageArgument(PageSize.A4),
                new DocumentArgument("1234", "5678")
                , ColorType.GRAY, true);

        File[] files = new File("test").listFiles();
        factory.start(-1, files, new File("output2.pdf"), listener);
    }

    private static final ImagePDFFactory.Listener listener = new ImagePDFFactory.Listener() {
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
        public void onAppend(long procedure_id, int index, int total) {
            System.out.println("onAppend:" + procedure_id + "\t Page:" + index);
        }
    };
}

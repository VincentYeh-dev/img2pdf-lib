package org.vincentyeh.img2pdf.lib.test;


import org.vincentyeh.img2pdf.lib.Img2Pdf;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactoryListener;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.*;

import java.io.File;
import java.io.IOException;

public class TestProgram {

    public static void main(String[] args) throws IOException {
        PageArgument pageArgument = new PageArgument(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER,
                PageSize.A4, PageDirection.Portrait, true);

        ImagePDFFactory factory = Img2Pdf.createFactory(pageArgument,
                new DocumentArgument("1234", "5678"));

        File destination = new File("output.pdf");
        boolean allowOverwriteFile = true;

        if (!allowOverwriteFile && destination.exists()) {
            throw new IOException("Overwrite deny");
        }


        File sourceDirectory = new File("test");
        File[] images = sourceDirectory.listFiles();

        if (images == null) {
            throw new RuntimeException("abstract pathname does not denote a directory");
        }

        if (images.length == 0) {
            throw new RuntimeException("No image files is found");
        }

        IDocument pdf = factory.start(1, images, ColorType.GRAY, listener);
        pdf.save(destination);
        factory.shutdown();
    }

    private static final ImagePDFFactoryListener listener = new ImagePDFFactoryListener() {

        @Override
        public void initializing(int procedure_id, int length) {
            System.out.println("initializing:" + procedure_id);
        }

        @Override
        public void onConversionComplete(int procedure_id) {
            System.out.println("onConversionComplete:" + procedure_id);
        }

        @Override
        public void onAppend(int procedure_id, File file, int appendedCount, int length) {
            System.out.println("onAppend:" + procedure_id + "\t image:" + file.getName());
        }

    };
}

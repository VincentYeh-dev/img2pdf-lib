package org.vincentyeh.img2pdf.lib.test;


import org.vincentyeh.img2pdf.lib.Img2Pdf;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactoryListener;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.*;

import java.io.File;
import java.io.IOException;

/**
 * Manual integration test program that reads all images from the {@code test/} directory
 * and converts them to a single {@code output.pdf} using PDFBox with AES encryption.
 */
public class TestProgram {

    /**
     * Entry point: loads images from {@code test/}, converts to PDF, and writes {@code output.pdf}.
     *
     * @param args command-line arguments (unused)
     * @throws IOException if the output file cannot be written or the source directory is invalid
     */
    public static void main(String[] args) throws IOException {
        PageArgument pageArgument = new PageArgument(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER,
                PageSize.A4, PageDirection.Portrait, true);
        DocumentArgument documentArgument = new DocumentArgument();
        documentArgument.setEncryption("1234", "5678", new Permission());

//        ImagePDFFactory factory = Img2Pdf.createOpenPDFFactory(Runtime.getRuntime().availableProcessors());
        ImagePDFFactory factory=Img2Pdf.createPDFBoxMaxPerformanceFactory();

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

        IDocument pdf = factory.start(images, ColorType.sRGB, documentArgument, pageArgument, listener);
        pdf.saveAndClose(destination);
        factory.shutdown();
    }

    private static final ImagePDFFactoryListener listener = new ImagePDFFactoryListener() {

        @Override
        public void initializing(int length) {
            System.out.println("initializing: length=" + length);
        }

        @Override
        public void onConversionComplete() {
            System.out.println("onConversionComplete");
        }

        @Override
        public void onAppend(File file, int appendedCount, int length) {
            System.out.println("onAppend:" + "\t image:" + file.getName());
        }

    };
}

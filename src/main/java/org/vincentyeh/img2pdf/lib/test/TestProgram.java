package org.vincentyeh.img2pdf.lib.test;


import org.vincentyeh.img2pdf.lib.Img2Pdf;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactoryListener;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Manual integration test program that generates synthetic images in a temporary directory
 * and converts them to a single {@code output.pdf} using OpenPDF with AES encryption.
 *
 * <p>Ten 200×200 JPEG images (1.jpg–10.jpg) are generated at runtime via {@link Graphics2D},
 * each displaying its page number in red text. The temporary directory is deleted on exit.</p>
 */
public class TestProgram {

    private static final int IMAGE_COUNT = 10;
    private static final int IMAGE_SIZE = 200;

    /**
     * Entry point: generates synthetic images, converts to PDF, and writes {@code output.pdf}.
     *
     * @param args command-line arguments (unused)
     * @throws IOException if image generation, PDF conversion, or file I/O fails
     */
    public static void main(String[] args) throws IOException {
        File tempDir = Files.createTempDirectory("img2pdf-test-").toFile();
        try {
            File[] images = generateImages(tempDir);

            PageArgument pageArgument = new PageArgument(
                    PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER,
                    PageSize.A4, PageDirection.Portrait, true);
            DocumentArgument documentArgument = new DocumentArgument();
            documentArgument.setEncryption("1234", "5678", new Permission());

            ImagePDFFactory factory = Img2Pdf.createOpenPDFFactory(Runtime.getRuntime().availableProcessors());

            File destination = new File("output.pdf");
            try (IDocument pdf = factory.start(images, ColorType.sRGB, documentArgument, pageArgument, listener)) {
                pdf.save(destination);
            }
            factory.shutdown();
        } finally {
            deleteDirectory(tempDir);
        }
    }

    /**
     * Generates {@value IMAGE_COUNT} synthetic 200×200 JPEG images numbered 1.jpg–10.jpg
     * into the given directory. Each image has a solid background colour and displays
     * its page number in large red text.
     *
     * @param dir the directory in which images are written
     * @return an array of {@link File} objects pointing to the generated images, in order
     * @throws IOException if any image cannot be written
     */
    private static File[] generateImages(File dir) throws IOException {
        File[] files = new File[IMAGE_COUNT];
        for (int i = 1; i <= IMAGE_COUNT; i++) {
            BufferedImage img = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            try {
                g.setColor(new Color(220, 235, 255));
                g.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Color.RED);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 72));

                String text = String.valueOf(i);
                FontMetrics fm = g.getFontMetrics();
                int x = (IMAGE_SIZE - fm.stringWidth(text)) / 2;
                int y = (IMAGE_SIZE - fm.getHeight()) / 2 + fm.getAscent();
                g.drawString(text, x, y);
            } finally {
                g.dispose();
            }

            File file = new File(dir, i + ".jpg");
            ImageIO.write(img, "jpg", file);
            files[i - 1] = file;
        }
        return files;
    }

    /**
     * Recursively deletes the given directory and all its contents.
     *
     * @param dir the directory to delete
     */
    private static void deleteDirectory(File dir) {
        File[] contents = dir.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirectory(f);
            }
        }
        dir.delete();
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

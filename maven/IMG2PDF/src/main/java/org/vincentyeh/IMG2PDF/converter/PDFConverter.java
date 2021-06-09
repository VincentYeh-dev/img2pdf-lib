package org.vincentyeh.IMG2PDF.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.vincentyeh.IMG2PDF.converter.listener.ConversionInfoListener;
import org.vincentyeh.IMG2PDF.pdf.page.core.ImagePageFactory;
import org.vincentyeh.IMG2PDF.task.Task;

import javax.imageio.ImageIO;

/**
 * The core of this program. At first,this class will be initialized by task.
 * When call() being called by other program,this program will start conversion
 * and finally return ImagesPDFDocument.
 *
 * @author VincentYeh
 */
public class PDFConverter implements ConversionInfoListener {

    private final Task task;
    private final boolean overwrite;
    private ConversionInfoListener info_listener;

    private final MemoryUsageSetting memoryUsageSetting;

    public PDFConverter(Task task, long maxMainMemoryBytes, File tempFolder, boolean overwrite) {
        this.overwrite = overwrite;

        if (task == null)
            throw new IllegalArgumentException("task is null.");
        this.task = task;

        if (tempFolder == null)
            throw new IllegalArgumentException("tempFolder is null");

        tempFolder.mkdirs();
        memoryUsageSetting = MemoryUsageSetting.setupMixed(maxMainMemoryBytes).setTempDir(tempFolder);
    }

    public File start() throws OverwriteDenyException, IOException {
        try (PDDocument document = new PDDocument(memoryUsageSetting)) {

            document.protect(task.getDocumentArgument().getSpp());

            onConversionPreparing(task);
            checkOverwrite();

            File[] images = task.getImages();
            appendAllPageToDocument(images, document);

            File pdf = savePDF(document);
            onConversionComplete(pdf);
            return pdf;
        }
    }

    private File savePDF(PDDocument document) throws IOException {
        task.getPdfDestination().getParentFile().mkdirs();

        document.save(task.getPdfDestination());
        return task.getPdfDestination();
    }

    private void appendAllPageToDocument(File[] images, PDDocument document) throws ConversionException, ReadImageException {
        for (int i = 0; i < images.length; i++) {
            onConverting(i, images[i]);
            appendPageToDocument(images[i],document);
        }
    }

    private void appendPageToDocument(File file, PDDocument document) throws ConversionException, ReadImageException {
        BufferedImage image = readImage(file);
        try {
            document.addPage(getImagePage(image,document));
        } catch (Exception e) {
            throw new ConversionException(file, e);
        }

    }

    private BufferedImage readImage(File image) throws ReadImageException {
        try {
            return ImageIO.read(image);
        } catch (Exception e) {
            throw new ReadImageException(image, e);
        }
    }

    private void checkOverwrite() throws OverwriteDenyException {
        if (!overwrite && task.getPdfDestination().exists()) {
            throw new OverwriteDenyException(task.getPdfDestination());
        }
    }

    private PDPage getImagePage(BufferedImage img, PDDocument document) throws Exception {
        return ImagePageFactory.getImagePage(document, task.getPageArgument(), img);
    }

    public void setInfoListener(ConversionInfoListener listener) {
        this.info_listener = listener;
    }

    @Override
    public void onConversionPreparing(Task task) {
        if (info_listener != null)
            info_listener.onConversionPreparing(task);
    }

    @Override
    public void onConverting(int index, File file) {
        if (info_listener != null)
            info_listener.onConverting(index, file);
    }

    @Override
    public void onConversionComplete(File dst) {
        if (info_listener != null)
            info_listener.onConversionComplete(dst);
    }


    public static class ConversionException extends RuntimeException {
        private final File file;
        private final Throwable cause;

        public ConversionException(File file, Throwable cause) {
            super(String.format("Error occur during conversion:%s", cause.getMessage()));
            this.file = file;
            this.cause = cause;
        }

        public File getFile() {
            return file;
        }

        @Override
        public Throwable getCause() {
            return cause;
        }
    }

    public static class OverwriteDenyException extends RuntimeException {
        private final File file;

        public OverwriteDenyException(File file) {
            super(String.format("Overwrite DENY:%s", file.getPath()));
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }

    public static class ReadImageException extends RuntimeException {
        private final File file;
        private final Throwable cause;

        public ReadImageException(File file, Throwable cause) {
            super(String.format("Unable to import image:%s", cause.getMessage()));
            this.file = file;
            this.cause = cause;
        }

        public File getFile() {
            return file;
        }

        @Override
        public Throwable getCause() {
            return cause;
        }
    }

}

package org.vincentyeh.IMG2PDF.converter;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.vincentyeh.IMG2PDF.converter.listener.ConversionInfoListener;
import org.vincentyeh.IMG2PDF.pdf.page.core.ImagePageFactory;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.util.file.FileChecker;

import javax.imageio.ImageIO;

/**
 * The core of this program. At first,this class will be initialized by task.
 * When call() being called by other program,this program will start conversion
 * and finally return ImagesPDFDocument.
 *
 * @author VincentYeh
 */
public class PDFConverter implements  ConversionInfoListener {

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

    private final PDDocument document;
    private final Task task;
    private final boolean overwrite;
    private ConversionInfoListener info_listener;

    public PDFConverter(Task task, long maxMainMemoryBytes, File tempFolder, boolean overwrite) throws ReadImageException, ConversionException, IOException {

        this.overwrite = overwrite;

        if (task == null)
            throw new NullPointerException("task is null.");
        this.task = task;

        if (tempFolder == null)
            throw new IllegalArgumentException("tempFolder is null");

        try {
            FileChecker.checkWritableFolder(tempFolder);

            MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMixed(maxMainMemoryBytes).setTempDir(tempFolder);

            document = new PDDocument(memoryUsageSetting);
            document.protect(task.getDocumentArgument().getSpp());
        } catch (IOException e) {
            closeDocument();
            throw e;
        }
    }

    /**
     * Start conversion process.
     * If listener isn't null,the return value will be null and
     * call onImageReadFail or onConversionFail.
     * <p>
     * If listener is null,call() will throw the exception.
     */

    public File convert() throws OverwriteDenyException, ImagingOpException, ConversionException, IOException {
        try {
            checkOverwrite();
//            if(1==1)
//                throw new OverwriteDenyException(new File("aaaa.bb"));
        } catch (OverwriteDenyException e) {
            closeDocument();
            throw e;
        }

//        closeDocument();
        File[] images = task.getImages();
        onConversionPreparing(task);
        try {
            appendAllPageToDocument(images);
        } catch (ReadImageException | ConversionException e) {
            closeDocument();
            throw e;
        }

        try {
            File pdf = savePDF();
            onConversionComplete(pdf);
            return pdf;
        } finally {
            closeDocument();
        }
    }

    private File savePDF() throws IOException {
        FileChecker.makeParentDirsIfNotExists(task.getDestination());
        FileChecker.checkWritableFile(task.getDestination());
        document.save(task.getDestination());
        return task.getDestination();
    }

    private void appendAllPageToDocument(File[] images) throws ConversionException, ReadImageException {
        for (int i = 0; i < images.length; i++) {
            onConverting(i, images[i]);
            appendPageToDocument(images[i]);
        }
    }

    private void appendPageToDocument(File file) throws ConversionException, ReadImageException {
        BufferedImage image = readImage(file);
        try {
            document.addPage(getImagePage(image));
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
        if (!overwrite && task.getDestination().exists()) {
            throw new OverwriteDenyException(task.getDestination());
        }
    }

    private PDPage getImagePage(BufferedImage img) throws Exception {
        return ImagePageFactory.getImagePage(document, task.getPageArgument(), img);
    }

    private void closeDocument() {
        try {
            document.close();
        } catch (Exception ignored) {
        }
    }

    /**
     * Setup the listener.
     *
     * @param listener ConversionListener
     */
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
}

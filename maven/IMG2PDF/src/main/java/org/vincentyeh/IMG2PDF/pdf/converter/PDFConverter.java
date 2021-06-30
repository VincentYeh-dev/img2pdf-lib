package org.vincentyeh.IMG2PDF.pdf.converter;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.vincentyeh.IMG2PDF.pdf.converter.core.ImagePageFactory;
import org.vincentyeh.IMG2PDF.pdf.converter.exception.PDFConversionException;
import org.vincentyeh.IMG2PDF.pdf.converter.exception.PDFConverterException;
import org.vincentyeh.IMG2PDF.pdf.converter.exception.ReadImageException;
import org.vincentyeh.IMG2PDF.pdf.converter.exception.SaveException;
import org.vincentyeh.IMG2PDF.pdf.converter.listener.ConversionInfoListener;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;
import org.vincentyeh.IMG2PDF.util.file.exception.OverwriteException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    public PDFConverter(Task task, long maxMainMemoryBytes, File tempFolder, boolean overwrite) throws IOException {
        this.overwrite = overwrite;

        if (task == null)
            throw new IllegalArgumentException("task is null.");
        this.task = task;

        if (tempFolder == null)
            throw new IllegalArgumentException("tempFolder is null");

        FileUtils.makeDirectories(tempFolder);

        memoryUsageSetting = MemoryUsageSetting.setupMixed(maxMainMemoryBytes).setTempDir(tempFolder);
    }

    public File start() throws PDFConverterException {
        checkOverwrite();
        try (PDDocument document = new PDDocument(memoryUsageSetting)) {
            document.protect(task.getDocumentArgument().getSpp());
            onConversionPreparing(task);

            File[] images = task.getImages();
            appendAllPageToDocument(images, document);
            File pdf = savePDF(document);
            onConversionComplete(pdf);
            return pdf;
        } catch (Exception e) {
            throw new PDFConverterException(e, task);
        }
    }

    private File savePDF(PDDocument document) throws SaveException {
        try {
            File parent = FileUtils.getParentFile(task.getPdfDestination());
            FileUtils.makeDirectories(parent);
            document.save(task.getPdfDestination());
            return task.getPdfDestination();
        } catch (Exception e) {
            throw new SaveException(e);
        }
    }

    private void appendAllPageToDocument(File[] images, PDDocument document) throws PDFConversionException, ReadImageException {
        for (int i = 0; i < images.length; i++) {
            onConverting(i, images[i]);
            appendPageToDocument(images[i], document);
        }
    }

    private void appendPageToDocument(File file, PDDocument document) throws PDFConversionException, ReadImageException {
        BufferedImage image = readImage(file);
        try {
            document.addPage(getImagePage(image, document));
        } catch (Exception e) {
            throw new PDFConversionException(e);
        }

    }

    private BufferedImage readImage(File image) throws ReadImageException {
        try {
            return ImageIO.read(image);
        } catch (Exception e) {
            throw new ReadImageException(e, image);
        }
    }

    private void checkOverwrite() throws SaveException {
        if (!overwrite) {
            try {
                FileUtils.checkOverwrite(task.getPdfDestination(), "PDF overwrite deny,File is already exists:" + task.getPdfDestination().getAbsoluteFile());
            } catch (OverwriteException e) {
                throw new SaveException(e);
            }
        }
    }

    private PDPage getImagePage(BufferedImage image, PDDocument document) throws Exception {
        ImagePageFactory.Builder builder = new ImagePageFactory.Builder();
        builder.setAutoRotate(task.getPageArgument().getAutoRotate());
        builder.setDirection(task.getPageArgument().getDirection());
        builder.setSize(task.getPageArgument().getSize());
        builder.setAlign(task.getPageArgument().getAlign());

        return builder.build().getImagePage(document, image);
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


}

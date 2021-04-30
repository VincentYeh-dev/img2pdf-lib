package org.vincentyeh.IMG2PDF.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.concurrent.*;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.pdf.page.ImagePageFactory;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.util.FileChecker;

import javax.imageio.ImageIO;

/**
 * The core of this program. At first,this class will be initialized by task.
 * When call() being called by other program,this program will start conversion
 * and finally return ImagesPDFDocument.
 *
 * @author VincentYeh
 */
public class PDFConverter implements Callable<File> {

    private final PDDocument document;
    private final Task task;
    private final boolean overwrite;
    //    private final ExecutorService page_executor = Executors.newCachedThreadPool();
    private ConversionListener listener;

    public PDFConverter(Task task, long maxMainMemoryBytes, File tempFolder, boolean overwrite) throws IOException {

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
    @Override
    public File call() throws Exception {
        try {
            checkOverwrite();

            File[] images = task.getImages();
            if (listener != null)
                listener.onConversionPreparing(task);

            for (int i = 0; i < images.length; i++) {
                if (listener != null)
                    listener.onConverting(i, images[i]);
                BufferedImage image = readImage(i, images[i]);
                appendPageToDocument(i, image);
            }

            FileChecker.makeParentDirsIfNotExists(task.getDestination());
            FileChecker.checkWritableFile(task.getDestination());

            document.save(task.getDestination());

            if (listener != null)
                listener.onConversionComplete(task.getDestination());

            return task.getDestination();

        } finally {
            closeDocument();
        }
    }

    private void appendPageToDocument(int index, BufferedImage image) throws Exception {
        try {
            document.addPage(getImagePage(image));
        } catch (Exception e) {
            closeDocument();
            if (listener != null) {
                listener.onConversionFail(index, e);
            }
            throw e;
        }
    }

    private BufferedImage readImage(int index, File image) throws IOException {
        try {
            return ImageIO.read(image);
        } catch (IOException e) {
            closeDocument();
            if (listener != null) {
                listener.onImageReadFail(index, image, e);
            }
            throw e;
        }
    }

    private void checkOverwrite() throws HandledException {
        if (!overwrite && task.getDestination().exists()) {
            if (listener != null) {
                listener.onFileAlreadyExists(task.getDestination());
            }
            throw new HandledException(new FileAlreadyExistsException(task.getDestination().getAbsolutePath()), getClass());
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
    public void setListener(ConversionListener listener) {
        this.listener = listener;
    }

}

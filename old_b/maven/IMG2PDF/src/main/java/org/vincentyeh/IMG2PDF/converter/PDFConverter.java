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
import org.vincentyeh.IMG2PDF.pdf.page.ImagePageConverter;
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
    private ConversionListener listener;
    private final Task task;
    private final boolean overwrite;


    public PDFConverter(Task task, long maxMainMemoryBytes, File tempFolder, boolean overwrite) throws IOException {
        if (task == null)
            throw new NullPointerException("task is null.");
        if (tempFolder == null)
            throw new IllegalArgumentException("tempFolder is null");
        FileChecker.checkWritableFolder(tempFolder);

        this.task = task;
        MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMixed(maxMainMemoryBytes).setTempDir(tempFolder);

        document = new PDDocument(memoryUsageSetting);
        document.protect(task.getDocumentArgument().getSpp());
        this.overwrite = overwrite;

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


        final ExecutorService page_executor = Executors.newCachedThreadPool();
        try {
            if (!overwrite && task.getDestination().exists()) {
                if (listener != null) {
                    listener.onFileAlreadyExists(task.getDestination());
                }
                throw new HandledException(new FileAlreadyExistsException(task.getDestination().getAbsolutePath()), getClass());
            }

            File[] imgs = task.getImages();
            if (listener != null)
                listener.onConversionPreparing(task);

            for (int i = 0; i < imgs.length; i++) {
                if (listener != null)
                    listener.onConverting(i, imgs[i]);

                BufferedImage image;
                try {
                    image = ImageIO.read(imgs[i]);
                } catch (IOException e) {
                    closeDocument();

                    if (listener != null) {
                        listener.onImageReadFail(i, imgs[i], e);
                    }
                    throw e;
                }

                try {
                    document.addPage(getImagePage(image, page_executor));
                } catch (Exception e) {
                    closeDocument();
                    if (listener != null) {
                        listener.onConversionFail(i, e);
                    }
                    throw e;
                }

            }

            FileChecker.makeParentDirsIfNotExists(task.getDestination());
            FileChecker.checkWritableFile(task.getDestination());

            document.save(task.getDestination());
            document.close();
            if (listener != null)
                listener.onConversionComplete(task.getDestination());

            return task.getDestination();

        } finally {
            page_executor.shutdown();
            closeDocument();
        }
    }

    /**
     * Draw Image to Page
     *
     * @param img The image written to the page
     * @return The page contain image
     * @throws Exception
     */
    private PDPage getImagePage(BufferedImage img, ExecutorService page_executor) throws Exception {
        ImagePageConverter converter = new ImagePageConverter(document, task.getPageArgument(), img);
        Future<PDPage> future = page_executor.submit(converter);
        return future.get();
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

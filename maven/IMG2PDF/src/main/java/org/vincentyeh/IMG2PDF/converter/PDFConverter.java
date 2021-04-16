package org.vincentyeh.IMG2PDF.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
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

    /**
     * Setup the pdf converter.
     *
     * @param task
     * @throws IOException
     */
    public PDFConverter(Task task, long maxMainMemoryBytes, File tempFolder) throws IOException {
        if (task == null)
            throw new NullPointerException("task is null.");
        if (tempFolder == null)
            throw new IllegalArgumentException("tempFolder is null");
        FileChecker.checkWritableFolder(tempFolder);

        this.task = task;
        MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMixed(maxMainMemoryBytes).setTempDir(tempFolder);

        document = new PDDocument(memoryUsageSetting);
        document.protect(task.getDocumentArgument().getSpp());
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
        File[] imgs = task.getImgs();
        if (listener != null)
            listener.onConversionPreparing(task);

        for (int i = 0; i < imgs.length; i++) {
            if (listener != null)
                listener.onConverting(i, imgs[i]);

            BufferedImage image;
            try {
                image = ImageIO.read(imgs[i]);
            } catch (IOException e) {
                try {
                    document.close();
                } catch (Exception ignored) {
                }

                if (listener != null) {
                    listener.onImageReadFail(i, e);
                    return null;
                } else {
                    throw e;
                }
            }

            try {
                document.addPage(getImagePage(image, page_executor));
            } catch (Exception e) {
                try {
                    document.close();
                } catch (Exception ignored) {
                }

                if (listener != null) {
                    listener.onConversionFail(i, e);
                    return null;
                } else {
                    throw e;
                }
            }

        }


        FileChecker.makeParentDirsIfNotExists(task.getDocumentArgument().getDestination());
        FileChecker.checkWritableFile(task.getDocumentArgument().getDestination());
        document.save(task.getDocumentArgument().getDestination());
        document.close();

        page_executor.shutdown();
        if (listener != null)
            listener.onConversionComplete(task.getDocumentArgument().getDestination());
        return task.getDocumentArgument().getDestination();
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


    /**
     * Setup the listener.
     *
     * @param listener ConversionListener
     */
    public void setListener(ConversionListener listener) {
        this.listener = listener;
    }

}

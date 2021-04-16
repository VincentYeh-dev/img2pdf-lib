package org.vincentyeh.IMG2PDF.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
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
        File[] imgs = task.getImgs();
        if (listener != null)
            listener.onConversionPreparing(task);

        for (int i = 0; i < imgs.length; i++) {
            if (listener != null)
                listener.onConverting(i);

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
                document.addPage(getImagePage(image));
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

        if (listener != null)
            listener.onConversionComplete();

        FileChecker.makeParentDirsIfNotExists(task.getDocumentArgument().getDestination());
        FileChecker.checkWritableFile(task.getDocumentArgument().getDestination());
        document.save(task.getDocumentArgument().getDestination());
        document.close();
        executor.shutdown();
        return task.getDocumentArgument().getDestination();
    }

    /**
     * Draw Image to Page
     *
     * @param img The image written to the page
     * @return The page contain image
     * @throws Exception
     */
    private PDPage getImagePage(BufferedImage img) throws Exception {
        ImagePageConverter converter = new ImagePageConverter(document,task.getPageArgument(), img);
        Future<PDPage> future=executor.submit(converter);
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

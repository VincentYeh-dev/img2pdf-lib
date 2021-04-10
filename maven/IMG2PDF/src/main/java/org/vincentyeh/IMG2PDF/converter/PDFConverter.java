package org.vincentyeh.IMG2PDF.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDPage;
import org.vincentyeh.IMG2PDF.pdf.doc.ImagesDocumentAdaptor;
import org.vincentyeh.IMG2PDF.pdf.page.ImagePageAdaptor;
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
public class PDFConverter implements Callable<ImagesDocumentAdaptor> {

    private final ImagesDocumentAdaptor documentAdaptor;
    private ConversionListener listener;
    private final Task task;

    /**
     * Setup the pdf converter.
     *
     * @param task
     * @throws IOException
     */
    public PDFConverter(Task task,long maxMainMemoryBytes,File tempFolder) throws IOException {
        if (task == null)
            throw new NullPointerException("task is null.");
        if(tempFolder==null)
            throw new IllegalArgumentException("tempFolder is null");
        FileChecker.checkWritableFolder(tempFolder);

        this.task = task;
        MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMixed(maxMainMemoryBytes).setTempDir(tempFolder);

        documentAdaptor = new ImagesDocumentAdaptor(task.getDocumentArgument(), memoryUsageSetting);
//        documentAdaptor.protect(task.getDocumentArgument().getSpp());
    }

    /**
     * Start conversion process.
     * If listener isn't null,the return value will be null and
     * call onImageReadFail or onConversionFail.
     * <p>
     * If listener is null,call() will throw the exception.
     */
    @Override
    public ImagesDocumentAdaptor call() throws Exception {
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
                documentAdaptor.closeDocument();
                if (listener != null) {
                    listener.onImageReadFail(i, e);
                    return null;
                } else {
                    throw e;
                }
            }

            try {
                documentAdaptor.addPage(createImgPage(image));

            } catch (Exception e) {
                documentAdaptor.closeDocument();
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

        return documentAdaptor;
    }

    /**
     * Draw Image to Page
     *
     * @param img The image written to the page
     * @return The page contain image
     * @throws Exception
     */
    private PDPage createImgPage(BufferedImage img) throws Exception {
        ImagePageAdaptor imgpage = new ImagePageAdaptor(task.getPageArgument(), img);
        imgpage.drawImageToPage(documentAdaptor.getDocument());
        return imgpage.getPage();
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

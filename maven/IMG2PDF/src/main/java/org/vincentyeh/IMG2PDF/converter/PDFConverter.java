package org.vincentyeh.IMG2PDF.converter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDPage;
import org.vincentyeh.IMG2PDF.pdf.doc.ImagesDocumentAdaptor;
import org.vincentyeh.IMG2PDF.pdf.doc.ImgFile;
import org.vincentyeh.IMG2PDF.pdf.page.ImagePageAdaptor;
import org.vincentyeh.IMG2PDF.pdf.page.PageArgument;
import org.vincentyeh.IMG2PDF.task.Task;

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
    public PDFConverter(Task task) throws IOException {
        if (task == null)
            throw new NullPointerException("task is null.");
        this.task = task;
        documentAdaptor = new ImagesDocumentAdaptor(task.getDocumentArgument());
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
        ImgFile[] imgs = task.getImgs();
        if (listener != null)
            listener.onConversionPreparing(task);

        for (int i = 0; i < imgs.length; i++) {
            if (listener != null)
                listener.onConverting(i);

            BufferedImage image;
            try {
                image = imgs[i].getImage();

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

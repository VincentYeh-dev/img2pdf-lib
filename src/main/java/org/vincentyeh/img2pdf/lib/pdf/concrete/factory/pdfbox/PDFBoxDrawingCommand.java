package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import java.awt.image.BufferedImage;

/**
 * Encapsulates a single image-drawing operation to be performed on a PDFBox page.
 *
 * <p>This class implements the <em>Command</em> pattern: the drawing parameters
 * (image, position, size) are captured at construction time and the actual PDFBox
 * API call is deferred to {@link #execute(PDDocument, PDPage)}. This separation
 * allows {@link PDFBoxPageAdaptor} to queue drawing requests from worker threads
 * and flush them later when the document context is available.</p>
 *
 * <p>The image is embedded as a lossless XObject using
 * {@link LosslessFactory#createFromImage}, which preserves full pixel fidelity at
 * the cost of larger file size.</p>
 *
 * <p>Instances of this class are package-private and are only created by
 * {@link PDFBoxPageAdaptor}.</p>
 */
public class PDFBoxDrawingCommand {
    private final BufferedImage image;
    private final PointF imagePosition;
    private final SizeF pageSize;

    /**
     * Creates a new drawing command with the given image data and layout metrics.
     *
     * @param image         the source image to embed; must not be {@code null}
     * @param imagePosition the bottom-left origin of the image in PDF user-space units
     *                      (points); must not be {@code null}
     * @param pageSize      the target width and height of the rendered image in PDF
     *                      user-space units (points); must not be {@code null}
     */
    PDFBoxDrawingCommand(BufferedImage image, PointF imagePosition, SizeF pageSize) {
        this.image = image;
        this.imagePosition = imagePosition;
        this.pageSize = pageSize;
    }

    /**
     * Executes this drawing command by embedding the image into the given page.
     *
     * <p>A new {@link PDPageContentStream} is opened for {@code page}, the image
     * XObject is drawn at the stored position and size, and the stream is closed
     * before returning. The content stream uses the default
     * {@link PDPageContentStream.AppendMode#OVERWRITE} mode, so it replaces any
     * previously written content on the page.</p>
     *
     * @param document the owning {@link PDDocument}; must not be {@code null}
     * @param page     the target page within {@code document}; must not be {@code null}
     * @throws IllegalStateException    if {@code document} is {@code null}
     * @throws IllegalArgumentException if {@code imagePosition}, {@code image}, or
     *                                  {@code pageSize} stored in this command is
     *                                  {@code null}
     * @throws RuntimeException         wrapping any {@link Exception} thrown by the
     *                                  PDFBox API during image encoding or stream writing
     */
    void execute(PDDocument document, PDPage page){
        if (document == null)
            throw new IllegalStateException("document has not been created");

        if (imagePosition == null)
            throw new IllegalArgumentException("position==null");

        if (image == null) throw new IllegalArgumentException("image==null");

        if (pageSize == null) throw new IllegalArgumentException("size==null");

        try {
            PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, image);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(pdImageXObject, imagePosition.x, imagePosition.y, pageSize.width, pageSize.height);
            contentStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

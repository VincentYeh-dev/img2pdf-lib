package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.vincentyeh.img2pdf.lib.annotation.NotNull;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

/**
 * PDFBox-backed implementation of {@link IPage}.
 *
 * <p>This class acts as an <em>Adapter</em> between the library's page abstraction
 * ({@link IPage}) and the PDFBox {@link PDPage} API. Drawing requests issued by
 * {@link #drawImage} are not immediately written to the PDF stream; instead they
 * are queued as {@link PDFBoxDrawingCommand} objects and executed lazily during
 * {@link #render(IDocument)}, at which point a live {@link PDDocument} reference
 * is available.</p>
 *
 * <p>This deferred execution model allows multiple pages to be prepared concurrently
 * on worker threads (draw phase) while serialisation to the document happens
 * sequentially (render phase), matching the threading constraints of PDFBox.</p>
 *
 * <p>This class is package-private and is only instantiated by
 * {@link PDFBoxImagePDFFactory}.</p>
 */
public class PDFBoxPageAdaptor implements IPage {
    private final PDPage page;
    private final int pageNumber;
    private final List<PDFBoxDrawingCommand> PDFBoxDrawingCommands = new LinkedList<>();

    /**
     * Creates a new page adaptor with the given page number and media-box size.
     *
     * @param pageNumber the one-based page number within the document; must be
     *                   greater than zero
     * @param pageSize   the width and height of the page in PDF user-space units
     *                   (points); must not be {@code null}
     * @throws IllegalArgumentException if {@code pageSize} is {@code null}
     */
    public PDFBoxPageAdaptor(int pageNumber, SizeF pageSize) {
        this.pageNumber = pageNumber;
        page = new PDPage();
        if (pageSize == null)
            throw new IllegalArgumentException("size==null");
        page.setMediaBox(new PDRectangle(pageSize.width, pageSize.height));
    }


    /**
     * Queues an image-drawing command for this page.
     *
     * <p>The actual PDFBox API call is deferred until {@link #render(IDocument)} is
     * invoked. This method may be called from a worker thread.</p>
     *
     * @param image         the source image to embed; must not be {@code null}
     * @param imagePosition the bottom-left position of the image in PDF user-space
     *                      units (points); must not be {@code null}
     * @param imageSize     the target width and height of the rendered image in PDF
     *                      user-space units (points); must not be {@code null}
     * @throws IllegalArgumentException if any argument is {@code null}
     * @throws RuntimeException         if a backend-level error occurs during command
     *                                  construction
     */
    @Override
    public void drawImage(@NotNull BufferedImage image, @NotNull PointF imagePosition, @NotNull SizeF imageSize) throws RuntimeException {

        if (imagePosition == null)
            throw new IllegalArgumentException("position==null");

        if (image == null) throw new IllegalArgumentException("image==null");

        if (imageSize == null) throw new IllegalArgumentException("size==null");

        PDFBoxDrawingCommands.add(new PDFBoxDrawingCommand(image, imagePosition, imageSize));
    }


    /**
     * Returns the one-based page number assigned to this page.
     *
     * @return the page number; always greater than zero
     */
    public int getPageNumber() {
        return pageNumber;
    }


    /**
     * Executes all queued drawing commands against the given document and page.
     *
     * <p>Each queued {@link PDFBoxDrawingCommand} is executed in insertion order,
     * writing image content directly into the underlying {@link PDPage} via a
     * {@link org.apache.pdfbox.pdmodel.PDPageContentStream}. The {@code document}
     * parameter must be a {@link PDFBoxDocumentAdaptor}; a {@link ClassCastException}
     * will be thrown otherwise.</p>
     *
     * @param document the target document; must be a {@link PDFBoxDocumentAdaptor}
     *                 and must not be {@code null}
     * @throws ClassCastException if {@code document} is not a
     *                            {@link PDFBoxDocumentAdaptor}
     * @throws RuntimeException   if any drawing command fails during execution
     */
    @Override
    public void render(IDocument document) {
        for (PDFBoxDrawingCommand command : PDFBoxDrawingCommands) {
            PDDocument doc = ((PDFBoxDocumentAdaptor) document).getInternalDocument();
            command.execute(doc, page);
        }
    }

    /**
     * Returns the underlying PDFBox {@link PDPage} instance.
     *
     * <p>This method is intended for use by {@link PDFBoxDocumentAdaptor} when
     * appending the page to the {@link PDDocument}. External callers should use the
     * {@link IPage} abstraction instead.</p>
     *
     * @return the internal {@link PDPage}; never {@code null}
     */
    public PDPage getInternalPage() {
        return page;
    }

}

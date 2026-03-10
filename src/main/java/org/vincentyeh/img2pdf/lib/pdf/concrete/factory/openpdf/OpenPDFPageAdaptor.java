package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Adapter that wraps an OpenPDF (librepdf) {@link Document} to implement the {@link IPage}
 * contract for a single PDF page.
 *
 * <p>Each instance represents exactly one page and follows a strict two-step lifecycle:</p>
 * <ol>
 *   <li>{@link #drawImage(BufferedImage, PointF, SizeF)} — records the image and its layout
 *       parameters. This step may be called at most once and must occur before
 *       {@link #render(IDocument)}.</li>
 *   <li>{@link #render(IDocument)} — commits the image to the internal OpenPDF
 *       {@link Document}. After this call, no further drawing is permitted.</li>
 * </ol>
 *
 * <p>After {@link #render(IDocument)} has been called, {@link #getPDFBytesContent()} closes
 * the internal document and returns the raw single-page PDF bytes. These bytes are then
 * consumed by {@link OpenPDFDocumentAdaptor#addPage(IPage)} to merge the page into the
 * parent document.</p>
 *
 * <p>The {@code IDocument} parameter of {@link #render(IDocument)} is ignored because the
 * page manages its own in-memory OpenPDF document independently, enabling thread-safe
 * parallel rendering.</p>
 *
 * <p>Instances of this class are not thread-safe individually; concurrent calls to any
 * method on the same instance produce undefined behaviour.</p>
 */
public class OpenPDFPageAdaptor implements IPage {
    private final int pageNumber;
    private final SizeF pageSize;
    private final ByteArrayOutputStream buffer;
    private final Document document;
    private BufferedImage bufferedImage;
    private PointF imagePosition;
    private SizeF imageSize;
    private boolean isDrawn = false;
    private boolean isRendered = false;


    /**
     * Constructs a new page adaptor with a fresh in-memory OpenPDF document of the
     * specified dimensions.
     *
     * @param pageNumber the 1-based page index within the final PDF document; must be
     *                   {@code > 0}
     * @param pageSize   the page dimensions in PDF user-space units (points); must not be
     *                   {@code null}, and both width and height must be {@code > 0}
     * @throws IllegalArgumentException if {@code pageNumber <= 0}, {@code pageSize} is
     *                                  {@code null}, or either page dimension is {@code <= 0}
     */
    public OpenPDFPageAdaptor(int pageNumber, SizeF pageSize) {
        checkArgument(pageNumber, pageSize);
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        buffer = new ByteArrayOutputStream();
        document = new Document(new RectangleReadOnly(pageSize.width, pageSize.height), 0, 0, 0, 0);
        PdfWriter.getInstance(document, buffer);
        document.open();
    }


    /**
     * Records the image and its layout parameters to be rendered on this page.
     *
     * <p>This method only stores the parameters; actual rendering is deferred to
     * {@link #render(IDocument)}. It may be called at most once per page instance.</p>
     *
     * @param image         the decoded image to place on the page; must not be {@code null}
     * @param imagePosition the absolute position of the image's origin in user-space units;
     *                      must not be {@code null}
     * @param imageSize     the target rendering dimensions; both width and height must be
     *                      {@code > 0}; must not be {@code null}
     * @throws IllegalStateException    if this page has already been rendered or if an image
     *                                  has already been drawn
     * @throws IllegalArgumentException if {@code imageSize} has a non-positive dimension
     * @throws NullPointerException     if any argument is {@code null}
     */
    @Override
    public void drawImage(BufferedImage image, PointF imagePosition, SizeF imageSize) throws RuntimeException {
        if(isRendered)
            throw new IllegalStateException("page has already been rendered, can not draw image anymore");

        Objects.requireNonNull(image, "image==null");
        Objects.requireNonNull(imagePosition, "imagePosition==null");
        Objects.requireNonNull(imageSize, "imageSize==null");

        if(isDrawn)
            throw new IllegalStateException("image has already been drawn on this page");

        if (imageSize.width <= 0 || imageSize.height <= 0)
            throw new IllegalArgumentException("imageSize is invalid");

        this.imagePosition = imagePosition;
        this.imageSize = imageSize;
        this.bufferedImage = image;
        isDrawn = true;
    }

    /**
     * Returns the 1-based page number of this page within the final PDF document.
     *
     * @return the page number; always {@code >= 1}
     */
    @Override
    public int getPageNumber() {
        return pageNumber;
    }


    /**
     * Renders the previously drawn image into the internal OpenPDF document.
     *
     * <p>If no image has been drawn (i.e., {@link #drawImage} was never called), this
     * method still marks the page as rendered but writes nothing. This allows empty pages
     * to be represented without error.</p>
     *
     * <p>The {@code ignored} parameter is not used; this page maintains its own internal
     * OpenPDF document so that rendering can proceed on any thread without shared state.</p>
     *
     * @param ignored not used; may be {@code null}
     * @throws IllegalStateException if this page has already been rendered
     * @throws RuntimeException      if the OpenPDF image conversion fails internally
     */
    @Override
    public void render(IDocument ignored) {
        if (isRendered)
            throw new IllegalStateException("page has already been rendered");
        isRendered = true;

        if(!isDrawn)
            return;

        document.newPage();
        try {
            Image image1 = Image.getInstance(bufferedImage, null);
            image1.setAbsolutePosition(imagePosition.x, imagePosition.y);
            image1.scaleToFit(imageSize.width, imageSize.height);
            document.add(image1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Returns the page dimensions in PDF user-space units (points).
     *
     * @return the page size; never {@code null}
     */
    public SizeF getPageSize() {
        return pageSize;
    }

    /**
     * Closes the internal OpenPDF document and returns its raw byte representation.
     *
     * <p>This method should be called only after {@link #render(IDocument)} has been
     * invoked. The returned bytes represent a valid single-page PDF document, ready to
     * be merged into the parent document by {@link OpenPDFDocumentAdaptor}.</p>
     *
     * <p><strong>Note:</strong> after this method is called the internal document is
     * permanently closed; calling this method more than once returns the same bytes
     * (already captured in the buffer) but does not re-close the document.</p>
     *
     * @return a byte array containing the single-page PDF; never {@code null}
     */
    public byte[] getPDFBytesContent() {
        document.close();
        return buffer.toByteArray();
    }

    /**
     * Validates the constructor arguments for page number and page size.
     *
     * @param pageNumber the 1-based page index to validate
     * @param pageSize   the page dimensions to validate
     * @throws IllegalArgumentException if {@code pageNumber <= 0}, {@code pageSize} is
     *                                  {@code null}, or either dimension is {@code <= 0}
     */
    private static void checkArgument(int pageNumber, SizeF pageSize) {
        if (pageNumber <= 0)
            throw new IllegalArgumentException("pageNumber must be greater than zero");
        if (pageSize == null)
            throw new IllegalArgumentException("size==null");
        if (pageSize.width <= 0 || pageSize.height <= 0)
            throw new IllegalArgumentException("size can not be zero or negative");

    }
}

package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
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
 * <p>Each instance represents exactly one page. {@link #drawImage(BufferedImage, PointF, SizeF)}
 * records the image and its layout parameters. {@link #getPDFBytesContent()} performs the
 * actual rendering into the internal OpenPDF document and returns the resulting raw PDF bytes.
 * Rendering is guaranteed to occur exactly once; calling {@link #getPDFBytesContent()} a second
 * time throws {@link IllegalStateException}.</p>
 *
 * <p>The raw bytes returned by {@link #getPDFBytesContent()} are then consumed by
 * {@link OpenPDFDocumentAdaptor#addPage(IPage)} to merge the page into the parent document.</p>
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
     * {@link #getPDFBytesContent()}. It may be called at most once per page instance.</p>
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
        if (isRendered)
            throw new IllegalStateException("page has already been rendered, can not draw image anymore");

        Objects.requireNonNull(image, "image==null");
        Objects.requireNonNull(imagePosition, "imagePosition==null");
        Objects.requireNonNull(imageSize, "imageSize==null");

        if (isDrawn)
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
     * Returns the page dimensions in PDF user-space units (points).
     *
     * @return the page size; never {@code null}
     */
    public SizeF getPageSize() {
        return pageSize;
    }

    /**
     * Renders any previously drawn image into the internal OpenPDF document, closes it,
     * and returns the raw single-page PDF bytes.
     *
     * <p>This method may be called exactly once. A second call throws
     * {@link IllegalStateException}. If no image has been drawn, the page is closed
     * without any image content.</p>
     *
     * @return a byte array containing the single-page PDF; never {@code null}
     * @throws IllegalStateException if this method has already been called
     * @throws RuntimeException      if the OpenPDF image conversion fails internally
     */
    public byte[] getPDFBytesContent() {
        if (isRendered) {
            throw new IllegalStateException("getPDFBytesContent() can only be called once");
        }
        isRendered = true;

        try {
            if (isDrawn) {
                document.newPage();
                Image image1 = Image.getInstance(bufferedImage, null);
                image1.setAbsolutePosition(imagePosition.x, imagePosition.y);
                image1.scaleToFit(imageSize.width, imageSize.height);
                document.add(image1);
            } else {
                // OpenPDF 需要至少一個頁面才能正常 close；加入空白 Chunk 確保頁面被建立
                document.add(new Chunk(""));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

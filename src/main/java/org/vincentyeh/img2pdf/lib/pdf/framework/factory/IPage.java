package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import org.vincentyeh.img2pdf.lib.annotation.NotNull;

import java.awt.image.BufferedImage;

/**
 * Represents a single page within a PDF document being constructed.
 *
 * <p>The life cycle of a page follows a two-phase model:</p>
 * <ol>
 *   <li><strong>Draw phase</strong> — image data and layout metrics are supplied via
 *       {@link #drawImage(BufferedImage, PointF, SizeF)}. This phase may run on a worker
 *       thread when parallel rendering is enabled.</li>
 *   <li><strong>Render phase</strong> — the page is committed to the document via
 *       {@link #render(IDocument)}. Depending on the PDF backend, this phase may need to
 *       run on the document-owner thread.</li>
 * </ol>
 *
 * <p>Implementations are not required to be thread-safe across both phases simultaneously.</p>
 */
public interface IPage {

    /**
     * Draws a scaled image onto this page at the specified position and size.
     *
     * <p>Both {@code imagePosition} and {@code imageSize} are expressed in PDF user-space
     * units (points), as computed by an {@link ImageScalingStrategy}.</p>
     *
     * @param image         the source image to draw; must not be {@code null}
     * @param imagePosition the top-left position of the image on the page, in points;
     *                      must not be {@code null}
     * @param imageSize     the target width and height of the image on the page, in points;
     *                      must not be {@code null}
     * @throws RuntimeException if the image cannot be embedded (e.g., unsupported format or
     *                          backend-specific encoding failure)
     */
    void drawImage(@NotNull BufferedImage image, @NotNull PointF imagePosition, @NotNull SizeF imageSize) throws RuntimeException;

    /**
     * Returns the one-based page number assigned to this page within the document.
     *
     * @return the page number; always greater than zero
     */
    int getPageNumber();

    /**
     * Commits this page to the given document.
     *
     * <p>After this method returns the page has been appended to {@code document} and must
     * not be used again. Calling {@link IDocument#addPage(IPage)} delegates to this
     * method.</p>
     *
     * @param document the document to which this page will be added; must not be
     *                 {@code null}
     */
    void render(IDocument document);
}

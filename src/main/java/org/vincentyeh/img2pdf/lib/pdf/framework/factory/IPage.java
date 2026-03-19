package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import org.vincentyeh.img2pdf.lib.annotation.NotNull;

import java.awt.image.BufferedImage;

/**
 * Represents a single page within a PDF document being constructed.
 *
 * <p>{@link #drawImage(BufferedImage, PointF, SizeF)} is the sole operation on a page;
 * it records image data and layout metrics for the page. Integration into the document
 * is handled exclusively by {@link IDocument#addPage(IPage)}, which acts as the single
 * point of responsibility for committing page content into the parent document.</p>
 *
 * <p>Implementations are not required to be thread-safe.</p>
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
}

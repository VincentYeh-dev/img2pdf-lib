package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

/**
 * Immutable value object that holds the output of an {@link ImageScalingStrategy} computation.
 *
 * <p>All measurements are in PDF user-space units (points, 1/72 inch). An instance of this
 * class is produced once per page and consumed by the page-rendering layer to set up the
 * PDF page canvas and to position the scaled image on that canvas.</p>
 */
public class ImageScalingResult {
    private final PointF imagePosition;
    private final SizeF pageSize;
    private final SizeF imageSize;

    /**
     * Constructs a new scaling result.
     *
     * @param pageSize      the final dimensions of the PDF page; must not be {@code null}
     * @param imageSize     the scaled dimensions at which the image will be drawn on the
     *                      page; must not be {@code null}
     * @param imagePosition the top-left position of the image on the page, in PDF
     *                      user-space units; must not be {@code null}
     */
    public ImageScalingResult(SizeF pageSize, SizeF imageSize, PointF imagePosition) {
        this.imagePosition = imagePosition;
        this.pageSize = pageSize;
        this.imageSize = imageSize;
    }

    /**
     * Returns the top-left position of the image on the page.
     *
     * @return the image position in PDF user-space units; never {@code null}
     */
    public PointF getImagePosition() {
        return imagePosition;
    }

    /**
     * Returns the final dimensions of the PDF page.
     *
     * @return the page size in PDF user-space units; never {@code null}
     */
    public SizeF getPageSize() {
        return pageSize;
    }

    /**
     * Returns the scaled dimensions at which the image will be drawn.
     *
     * @return the image size in PDF user-space units; never {@code null}
     */
    public SizeF getImageSize() {
        return imageSize;
    }

}

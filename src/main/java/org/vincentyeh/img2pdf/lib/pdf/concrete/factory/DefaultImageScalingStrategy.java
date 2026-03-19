package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingResult;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageAlign;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageDirection;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;

import static org.vincentyeh.img2pdf.lib.pdf.parameter.PageDirection.Landscape;
import static org.vincentyeh.img2pdf.lib.pdf.parameter.PageDirection.Portrait;


/**
 * Default concrete implementation of {@link ImageScalingStrategy} that scales an image
 * to fill the page as large as possible while preserving its aspect ratio, then positions
 * it according to the configured {@link PageAlign}.
 *
 * <p>The scaling algorithm works as follows:</p>
 * <ol>
 *   <li><strong>Page size resolution</strong> — if {@link PageSize#DEPEND_ON_IMG} is set,
 *       the page dimensions match the image exactly; otherwise the canonical size for the
 *       chosen {@link PageSize} is used. When {@code autoRotate} is {@code true}, the
 *       orientation (Portrait/Landscape) is derived from the image's own aspect ratio
 *       instead of the explicit {@link PageDirection}.</li>
 *   <li><strong>Image scaling</strong> — the image is scaled uniformly (aspect-ratio
 *       preserving) to the largest size that still fits within the page bounds, applying
 *       either width-fill or height-fill depending on which axis becomes the binding
 *       constraint first.</li>
 *   <li><strong>Image positioning</strong> — the scaled image is placed on the page
 *       according to the horizontal ({@link PageAlign.HorizontalAlign}) and vertical
 *       ({@link PageAlign.VerticalAlign}) alignment settings, distributing any remaining
 *       whitespace to the appropriate side(s).</li>
 * </ol>
 *
 * <p>All coordinates and dimensions are expressed in PDF user-space units (points,
 * 1/72 inch). This class is stateless and therefore thread-safe.</p>
 */
public final class DefaultImageScalingStrategy implements ImageScalingStrategy {

    /**
     * Computes the page size, scaled image dimensions, and image position for a single page.
     *
     * @param argument  page configuration specifying size, direction, alignment, and
     *                  auto-rotation; must not be {@code null}
     * @param imageSize the intrinsic size of the source image in user-space units;
     *                  must not be {@code null}
     * @return an {@link ImageScalingResult} containing the resolved page dimensions,
     *         scaled image dimensions, and image position; never {@code null}
     */
    public ImageScalingResult execute(PageArgument argument, SizeF imageSize) {
        SizeF pageSize = getSuitablePageSize(argument.direction, argument.size, imageSize, argument.autoRotate);
        SizeF newImageSize = getImageMaxSize(imageSize, pageSize);
        PointF imagePosition = calculateImagePosition(argument.align, newImageSize, pageSize);
        return new ImageScalingResult(pageSize, newImageSize, imagePosition);
    }

    /**
     * Resolves the final page dimensions based on the page size setting, orientation, and
     * auto-rotation preference.
     *
     * <p>If {@code pageSize} is {@link PageSize#DEPEND_ON_IMG}, the image dimensions are
     * returned directly. Otherwise, the canonical size for the given {@link PageSize} is
     * swapped between width and height when the resolved direction is Landscape.</p>
     *
     * @param default_direction the page direction to use when {@code autoRotate} is {@code false}
     * @param pageSize          the target page size preset
     * @param imageSize         the intrinsic image dimensions used to detect orientation
     *                          when {@code autoRotate} is {@code true}
     * @param autoRotate        if {@code true}, derives orientation from the image aspect ratio
     *                          instead of {@code default_direction}
     * @return the resolved page dimensions in user-space units
     * @throws IllegalArgumentException if the resolved direction is neither Portrait nor Landscape
     */
    private SizeF getSuitablePageSize(PageDirection default_direction, PageSize pageSize, SizeF imageSize, boolean autoRotate) {

        final PageDirection direction;
        if (pageSize == PageSize.DEPEND_ON_IMG)
            return imageSize;

        if (autoRotate) {
            direction = PageDirection.detectDirection(imageSize);
        } else {
            direction = default_direction;
        }

        SizeF size = pageSize.getSizeInPixels();
        if (direction == Portrait) {
            return new SizeF(size.width, size.height);
        } else if (direction == Landscape) {
            return new SizeF(size.height, size.width);
        } else throw new IllegalArgumentException();
    }

    /**
     * Scales the image uniformly (aspect-ratio preserving) to the maximum size that fits
     * within the given page dimensions.
     *
     * <p>The algorithm tries width-fill first (scale so that width equals page width). If
     * the resulting height fits within the page height, that size is returned. Otherwise,
     * height-fill is used (scale so that height equals page height).</p>
     *
     * @param img_size  the intrinsic image dimensions; must not be {@code null}
     * @param page_size the page dimensions that bound the scaled image; must not be {@code null}
     * @return the scaled image dimensions; width or height equals the corresponding page
     *         dimension, the other axis is at most the page dimension
     * @throws IllegalArgumentException if either argument is {@code null}
     * @throws IllegalStateException    if neither width-fill nor height-fill produces a
     *                                  valid fit (should not occur under normal conditions)
     */
    private SizeF getImageMaxSize(SizeF img_size, SizeF page_size) {
        if (img_size == null) throw new IllegalArgumentException("img_size==null");
        if (page_size == null) throw new IllegalArgumentException("page_size==null");

        float scaled_width = (img_size.width / img_size.height) * page_size.height;
        float scaled_height = (img_size.height / img_size.width) * page_size.width;

//			width fill
        if (scaled_height <= page_size.height)
            return new SizeF(page_size.width, scaled_height);
//			height fill
        if (scaled_width <= page_size.width)
            return new SizeF(scaled_width, page_size.height);

        throw new IllegalStateException();
    }

    /**
     * Calculates the absolute position of the (already scaled) image within the page,
     * applying both horizontal and vertical alignment.
     *
     * @param align      the alignment settings for both axes; must not be {@code null}
     * @param objectSize the scaled image dimensions; must not be {@code null}
     * @param pageSize   the page dimensions; must not be {@code null}
     * @return the top-left position of the image in user-space units
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    private PointF calculateImagePosition(PageAlign align, SizeF objectSize, SizeF pageSize) {
        if (objectSize == null) throw new IllegalArgumentException("objectSize==null");
        if (pageSize == null) throw new IllegalArgumentException("pageSize==null");
        float positionX = calculatePositionX(align.horizontal_align, objectSize, pageSize);
        float positionY = calculatePositionY(align.vertical_align, objectSize, pageSize);

        return new PointF(positionX, positionY);
    }

    /**
     * Calculates the X-axis (horizontal) offset of the image within the page.
     *
     * <ul>
     *   <li>{@link PageAlign.HorizontalAlign#LEFT} — offset is {@code 0}.</li>
     *   <li>{@link PageAlign.HorizontalAlign#RIGHT} — offset is the full remaining space.</li>
     *   <li>{@code CENTER} (default) — offset is half the remaining space.</li>
     * </ul>
     *
     * @param horizontalAlign the horizontal alignment setting
     * @param objectSize      the scaled image dimensions
     * @param pageSize        the page dimensions
     * @return the X position of the image's left edge in user-space units
     * @throws IllegalArgumentException if the horizontal space is negative (image wider than page)
     */
    private float calculatePositionX(PageAlign.HorizontalAlign horizontalAlign, SizeF objectSize, SizeF pageSize) {
        float xSpace = pageSize.width - objectSize.width;
        if (xSpace < 0) throw new IllegalArgumentException("The space of x axis is negative.");

        switch (horizontalAlign) {
            case LEFT:
                return 0;
            case RIGHT:
                return xSpace;
            default:
                return xSpace / 2;
        }
    }

    /**
     * Calculates the Y-axis (vertical) offset of the image within the page.
     *
     * <ul>
     *   <li>{@link PageAlign.VerticalAlign#BOTTOM} — offset is {@code 0}.</li>
     *   <li>{@link PageAlign.VerticalAlign#TOP} — offset is the full remaining space.</li>
     *   <li>{@code CENTER} (default) — offset is half the remaining space.</li>
     * </ul>
     *
     * @param verticalAlign the vertical alignment setting
     * @param objectSize    the scaled image dimensions
     * @param pageSize      the page dimensions
     * @return the Y position of the image's bottom edge in user-space units
     * @throws IllegalArgumentException if the vertical space is negative (image taller than page)
     */
    private float calculatePositionY(PageAlign.VerticalAlign verticalAlign, SizeF objectSize, SizeF pageSize) {
        float ySpace = pageSize.height - objectSize.height;
        if (ySpace < 0) throw new IllegalArgumentException("The space of y axis is negative.");
        switch (verticalAlign) {
            case BOTTOM:
                return 0;
            case TOP:
                return ySpace;
            default:
                return ySpace / 2;
        }
    }
}

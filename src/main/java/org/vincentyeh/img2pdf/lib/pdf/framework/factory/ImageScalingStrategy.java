package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

/**
 * Strategy interface for computing how an image should be scaled and positioned within a
 * PDF page.
 *
 * <p>Given the desired page configuration ({@link PageArgument}) and the intrinsic size of
 * an image, an implementation calculates three things:</p>
 * <ul>
 *   <li>The final page dimensions ({@link ImageScalingResult#getPageSize()}).</li>
 *   <li>The scaled image dimensions ({@link ImageScalingResult#getImageSize()}).</li>
 *   <li>The top-left position of the image on the page
 *       ({@link ImageScalingResult#getImagePosition()}).</li>
 * </ul>
 *
 * <p>All measurements are in PDF user-space units (points, 1/72 inch).</p>
 *
 * <p>This interface follows the <em>Strategy</em> design pattern: the concrete algorithm
 * (e.g., {@code DefaultImageScalingStrategy}) is injected into the factory at construction
 * time and invoked once per page during the conversion pipeline.</p>
 */
public interface ImageScalingStrategy {

    /**
     * Computes the page size, scaled image size, and image position for a single page.
     *
     * @param pageArgument the page configuration specifying the target page size, alignment,
     *                     direction, and auto-rotation preference; must not be {@code null}
     * @param imageSize    the intrinsic size of the source image in PDF user-space units;
     *                     must not be {@code null}
     * @return an {@link ImageScalingResult} containing the resolved page dimensions, scaled
     *         image dimensions, and image position; never {@code null}
     */
    ImageScalingResult execute(PageArgument pageArgument, SizeF imageSize);
}

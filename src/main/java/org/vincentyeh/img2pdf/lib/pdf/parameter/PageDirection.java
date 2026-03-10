package org.vincentyeh.img2pdf.lib.pdf.parameter;


import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

/**
 * Represents the orientation of a PDF page — either portrait (taller than wide) or
 * landscape (wider than tall).
 *
 * <p>{@code PageDirection} is stored in {@link PageArgument} and consumed by
 * {@code DefaultImageScalingStrategy}.  When {@link PageArgument#autoRotate} is enabled,
 * the strategy calls {@link #detectDirection(SizeF)} to override the user-supplied direction
 * with one that matches the source image's aspect ratio, avoiding unnecessary white space.</p>
 *
 * @author VincentYeh
 */
public enum PageDirection {

    /** Landscape orientation: the page width is greater than or equal to the page height. */
    Landscape,

    /** Portrait orientation: the page height is greater than the page width. */
    Portrait;

    /**
     * Detects the natural orientation of the given size, returning {@link #Portrait} when
     * {@code height / width > 1} and {@link #Landscape} otherwise.
     *
     * @param size the dimensions to evaluate; must not be {@code null}
     * @return {@link #Portrait} if the height exceeds the width, otherwise {@link #Landscape}
     */
    public static PageDirection detectDirection(SizeF size) {
        return detectDirection(size.width, size.height);
    }

    /**
     * Detects the natural orientation from explicit width and height values, returning
     * {@link #Portrait} when {@code height / width > 1} and {@link #Landscape} otherwise.
     *
     * @param width  the width component of the dimensions
     * @param height the height component of the dimensions
     * @return {@link #Portrait} if the height exceeds the width, otherwise {@link #Landscape}
     */
    public static PageDirection detectDirection(float width, float height) {
        return height / width > 1 ? Portrait : Landscape;
    }
}

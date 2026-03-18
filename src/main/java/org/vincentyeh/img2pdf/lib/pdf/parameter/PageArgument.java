package org.vincentyeh.img2pdf.lib.pdf.parameter;


/**
 * Aggregates all page-layout parameters used when converting an image to a PDF page.
 *
 * <p>An instance of this class is passed to {@code ImagePDFFactory.start()} and consumed by
 * {@code DefaultImageScalingStrategy} to determine how each image is positioned and scaled
 * on the resulting PDF page.  The most common configuration is available through the
 * convenience constructors; fine-grained control is possible by supplying explicit values
 * for every field.</p>
 *
 * <p>Example — create a centred A4 portrait page with auto-rotation enabled:</p>
 * <pre>{@code
 * PageArgument arg = new PageArgument(
 *         PageAlign.VerticalAlign.CENTER,
 *         PageAlign.HorizontalAlign.CENTER,
 *         PageSize.A4,
 *         PageDirection.Portrait,
 *         true);
 * }</pre>
 */
public class PageArgument {

    /**
     * The alignment (vertical + horizontal) of the image within the page.
     */
    public PageAlign align;

    /**
     * The target page size.  Use {@link PageSize#DEPEND_ON_IMG} to make the page
     * dimensions match the source image dimensions exactly.
     */
    public PageSize size;

    /**
     * The orientation (portrait or landscape) of the page.
     */
    public PageDirection direction;

    /**
     * When {@code true}, the page direction is automatically adjusted so that the
     * longer image edge is mapped to the longer page edge, preventing unnecessary
     * blank space.
     */
    public boolean autoRotate;

    /**
     * Creates a {@code PageArgument} with all parameters specified explicitly.
     *
     * @param align      the image alignment within the page
     * @param size       the target page size
     * @param direction  the page orientation
     * @param autoRotate {@code true} to automatically rotate the page to match the image aspect ratio
     */
    public PageArgument(PageAlign align, PageSize size, PageDirection direction, boolean autoRotate) {
        this.align = align;
        this.size = size;
        this.direction = direction;
        this.autoRotate = autoRotate;
    }

    /**
     * Creates a {@code PageArgument} with individual vertical and horizontal alignment values.
     *
     * @param verticalAlign   the vertical component of the image alignment
     * @param horizontalAlign the horizontal component of the image alignment
     * @param size            the target page size
     * @param direction       the page orientation
     * @param autoRotate      {@code true} to automatically rotate the page to match the image aspect ratio
     */
    public PageArgument(PageAlign.VerticalAlign verticalAlign,
                        PageAlign.HorizontalAlign horizontalAlign,
                        PageSize size, PageDirection direction,
                        boolean autoRotate) {
        this(new PageAlign(verticalAlign, horizontalAlign), size, direction, autoRotate);
    }

    /**
     * Creates a {@code PageArgument} with individual alignment values, defaulting to
     * {@link PageDirection#Portrait} orientation and {@code autoRotate = true}.
     *
     * @param verticalAlign   the vertical component of the image alignment
     * @param horizontalAlign the horizontal component of the image alignment
     * @param size            the target page size
     */
    public PageArgument(PageAlign.VerticalAlign verticalAlign,
                        PageAlign.HorizontalAlign horizontalAlign,
                        PageSize size) {
        this(new PageAlign(verticalAlign, horizontalAlign), size, PageDirection.Portrait, true);
    }

    /**
     * Creates a {@code PageArgument} with individual alignment values and an explicit direction,
     * defaulting to {@code autoRotate = false}.
     *
     * @param verticalAlign   the vertical component of the image alignment
     * @param horizontalAlign the horizontal component of the image alignment
     * @param size            the target page size
     * @param direction       the page orientation
     */
    public PageArgument(PageAlign.VerticalAlign verticalAlign,
                        PageAlign.HorizontalAlign horizontalAlign,
                        PageSize size, PageDirection direction) {
        this(verticalAlign, horizontalAlign, size, direction, false);
    }

    /**
     * Creates a {@code PageArgument} with the given page size, centred alignment,
     * portrait orientation, and auto-rotation enabled.
     *
     * @param size the target page size
     */
    public PageArgument(PageSize size) {
        this(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER, size);
    }

    /**
     * Creates a {@code PageArgument} with default settings: centred alignment,
     * {@link PageSize#DEPEND_ON_IMG} size, portrait orientation, and auto-rotation enabled.
     */
    public PageArgument() {
        this(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER, PageSize.DEPEND_ON_IMG);
    }

    /**
     * Returns the image alignment within the page.
     *
     * @return the {@link PageAlign} instance
     */
    public PageAlign getAlign() {
        return align;
    }

    /**
     * Returns the target page size.
     *
     * @return the {@link PageSize} constant
     */
    public PageSize getSize() {
        return size;
    }

    /**
     * Returns the page orientation.
     *
     * @return the {@link PageDirection} constant
     */
    public PageDirection getDirection() {
        return direction;
    }

    /**
     * Returns whether the page should be automatically rotated to match the image aspect ratio.
     *
     * @return {@code true} if auto-rotation is enabled
     */
    public boolean isAutoRotate() {
        return autoRotate;
    }

    /**
     * Sets the image alignment within the page.
     *
     * @param align the new {@link PageAlign} value
     */
    public void setAlign(PageAlign align) {
        this.align = align;
    }

    /**
     * Sets the target page size.
     *
     * @param size the new {@link PageSize} value
     */
    public void setSize(PageSize size) {
        this.size = size;
    }

    /**
     * Sets the page orientation.
     *
     * @param direction the new {@link PageDirection} value
     */
    public void setDirection(PageDirection direction) {
        this.direction = direction;
    }

    /**
     * Sets whether the page should be automatically rotated to match the image aspect ratio.
     *
     * @param autoRotate {@code true} to enable auto-rotation
     */
    public void setAutoRotate(boolean autoRotate) {
        this.autoRotate = autoRotate;
    }

}

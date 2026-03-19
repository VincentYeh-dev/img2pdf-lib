package org.vincentyeh.img2pdf.lib.pdf.parameter;


/**
 * Defines the 2-D alignment of an image within its PDF page, combining a
 * {@link VerticalAlign} and a {@link HorizontalAlign} component.
 *
 * <p>A {@code PageAlign} is stored inside {@link PageArgument} and is consumed by
 * {@code DefaultImageScalingStrategy} to determine the position of the scaled image
 * on the page.</p>
 *
 * <p>The string representation uses the format {@code "VERTICAL-HORIZONTAL"}
 * (e.g. {@code "TOP-LEFT"}, {@code "CENTER-CENTER"}), which is also the format
 * accepted by {@link #valueOf(String)}.</p>
 *
 * @author vincent
 */
public class PageAlign {

    /**
     * The vertical component of the alignment (top, centre, or bottom).
     */
    public final VerticalAlign vertical_align;

    /**
     * The horizontal component of the alignment (left, centre, or right).
     */
    public final HorizontalAlign horizontal_align;

    /**
     * Constructs a {@code PageAlign} from explicit vertical and horizontal alignment values.
     *
     * @param vertical_align   the vertical alignment component
     * @param horizontal_align the horizontal alignment component
     */
    public PageAlign(VerticalAlign vertical_align, HorizontalAlign horizontal_align) {
        this.vertical_align = vertical_align;
        this.horizontal_align = horizontal_align;
    }

    /**
     * Parses a {@code PageAlign} from a string in the format {@code "VERTICAL-HORIZONTAL"},
     * where each part is a valid name of the corresponding enum constant
     * (e.g. {@code "TOP-LEFT"}, {@code "CENTER-CENTER"}).
     *
     * @param value the string to parse; must contain exactly one {@code '-'} separator
     * @return a new {@code PageAlign} instance corresponding to the parsed values
     * @throws IllegalArgumentException if the string does not match the expected format or
     *                                  contains unknown enum constant names
     */
    public static PageAlign valueOf(String value) {
        String[] verti_hori_align = value.split("-");
        return new PageAlign(VerticalAlign.valueOf(verti_hori_align[0]), HorizontalAlign.valueOf(verti_hori_align[1]));
    }

    /**
     * Returns the string representation in the format {@code "VERTICAL-HORIZONTAL"},
     * for example {@code "CENTER-LEFT"}.
     *
     * @return the formatted alignment string
     */
    @Override
    public String toString() {
        return String.format("%s-%s", vertical_align, horizontal_align);
    }

    /**
     * Defines the horizontal position of the image within the page.
     */
    public enum HorizontalAlign {
        /** Align the image to the right edge of the page. */
        RIGHT,
        /** Align the image to the left edge of the page. */
        LEFT,
        /** Centre the image horizontally on the page. */
        CENTER
    }

    /**
     * Defines the vertical position of the image within the page.
     */
    public enum VerticalAlign {
        /** Align the image to the top edge of the page. */
        TOP,
        /** Align the image to the bottom edge of the page. */
        BOTTOM,
        /** Centre the image vertically on the page. */
        CENTER
    }

}

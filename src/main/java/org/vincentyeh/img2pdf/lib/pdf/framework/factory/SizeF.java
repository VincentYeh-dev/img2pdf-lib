package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import java.util.Objects;

/**
 * Immutable two-dimensional size expressed as single-precision floating-point values.
 *
 * <p>Used throughout the PDF layout pipeline to represent dimensions — such as page size
 * and image size — in PDF user-space units (points, 1/72 inch). Both {@code width} and
 * {@code height} must be non-negative.</p>
 *
 * <p>The natural ordering defined by {@link Comparable} compares sizes by their area
 * ({@code width * height}), not by individual dimensions.</p>
 */
public class SizeF implements Comparable<SizeF> {

    /** The horizontal extent in PDF user-space units; always non-negative. */
    public final float width;

    /** The vertical extent in PDF user-space units; always non-negative. */
    public final float height;

    /**
     * Constructs a size with the specified dimensions.
     *
     * @param width  the horizontal extent; must be {@code >= 0}
     * @param height the vertical extent; must be {@code >= 0}
     * @throws IllegalArgumentException if {@code width} or {@code height} is negative
     */
    public SizeF(float width, float height) {
        if (height < 0)
            throw new IllegalArgumentException("height<0");
        if (width < 0)
            throw new IllegalArgumentException("width<0");
        this.width = width;
        this.height = height;
    }

    /**
     * Constructs a zero size {@code (0, 0)}.
     */
    public SizeF() {
        this(0f, 0f);
    }

    /**
     * Indicates whether another object is equal to this size.
     *
     * <p>Two {@code SizeF} instances are considered equal when they have the same area
     * (i.e., {@link #compareTo(SizeF)} returns zero).</p>
     *
     * @param o the reference object to compare
     * @return {@code true} if {@code o} is a {@code SizeF} with the same area
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SizeF size = (SizeF) o;
        return compareTo(size) == 0;
    }

    /**
     * Returns a string representation of this size in the format {@code (width,height)},
     * with each value rounded to two decimal places.
     *
     * @return a formatted string such as {@code "(595.00,842.00)"}
     */
    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", width, height);
    }

    /**
     * Compares this size to another by area.
     *
     * @param o the size to compare to; must not be {@code null}
     * @return a negative integer, zero, or a positive integer if this size's area is less
     *         than, equal to, or greater than {@code o}'s area
     * @throws IllegalArgumentException if {@code o} is {@code null}
     */
    @Override
    public int compareTo(SizeF o) {
        try {
            Objects.requireNonNull(o);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
        return Float.compare(getArea(), o.getArea());
    }

    /**
     * Returns the area of this size ({@code width * height}).
     *
     * @return the area; always non-negative
     */
    public float getArea() {
        return height * width;
    }
}

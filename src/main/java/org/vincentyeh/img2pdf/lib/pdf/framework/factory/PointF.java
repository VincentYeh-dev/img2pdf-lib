package org.vincentyeh.img2pdf.lib.pdf.framework.factory;


/**
 * Immutable two-dimensional coordinate expressed as single-precision floating-point values.
 *
 * <p>Used throughout the PDF layout pipeline to represent positions in PDF user-space units
 * (points, 1/72 inch). The origin {@code (0, 0)} corresponds to the bottom-left corner of
 * the page in standard PDF coordinate space.</p>
 *
 * <p>The natural ordering defined by {@link Comparable} compares points by their distance
 * from the origin (hypotenuse), not by individual axis values.</p>
 */
public class PointF implements Comparable<PointF> {

    /** The horizontal coordinate in PDF user-space units. */
    public final float x;

    /** The vertical coordinate in PDF user-space units. */
    public final float y;

    /**
     * Constructs a point with the specified coordinates.
     *
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     */
    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a point at the origin {@code (0, 0)}.
     */
    public PointF() {
        this(0, 0);
    }

    /**
     * Returns a string representation of this point in the format {@code (x,y)}, with each
     * value rounded to two decimal places.
     *
     * @return a formatted string such as {@code "(12.50,34.00)"}
     */
    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", x, y);
    }

    /**
     * Indicates whether another object is equal to this point.
     *
     * <p>Two {@code PointF} instances are considered equal when their distances from the
     * origin are equal (i.e., {@link #compareTo(PointF)} returns zero).</p>
     *
     * @param o the reference object to compare
     * @return {@code true} if {@code o} is a {@code PointF} with the same hypotenuse value
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointF position = (PointF) o;
        return compareTo(position) == 0;
    }

    /**
     * Compares this point to another by their distances from the origin.
     *
     * @param o the point to compare to; must not be {@code null}
     * @return a negative integer, zero, or a positive integer if this point's distance from
     *         the origin is less than, equal to, or greater than {@code o}'s distance
     */
    @Override
    public int compareTo(PointF o) {
        return Double.compare(hypotenuse(), o.hypotenuse());
    }

    /**
     * Returns the Euclidean distance from the origin to this point (i.e., {@code sqrt(x²+y²)}).
     *
     * @return the distance from the origin; always non-negative
     */
    public float hypotenuse() {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}

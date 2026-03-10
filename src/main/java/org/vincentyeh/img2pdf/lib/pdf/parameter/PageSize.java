package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

/**
 * Defines the physical page size used when generating PDF pages.
 *
 * <p>Each constant (except {@link #DEPEND_ON_IMG}) stores its dimensions in PDF user units
 * (points, where 1 pt = 1/72 inch), in portrait orientation (width &lt; height).
 * Convenience methods are provided to retrieve the dimensions in millimetres or inches.</p>
 *
 * <p>The special constant {@link #DEPEND_ON_IMG} instructs the scaling strategy to derive
 * the page size directly from the source image dimensions rather than using a fixed standard
 * size.</p>
 *
 * @author VincetYeh
 */
public enum PageSize {

    /** ISO A0 paper size (841 × 1189 mm). */
    A0(new SizeF(2383.937F, 3370.3938F)),

    /** ISO A1 paper size (594 × 841 mm). */
    A1(new SizeF(1683.7795F, 2383.937F)),

    /** ISO A2 paper size (420 × 594 mm). */
    A2(new SizeF(1190.5513F, 1683.7795F)),

    /** ISO A3 paper size (297 × 420 mm). */
    A3(new SizeF(841.8898F, 1190.5513F)),

    /** ISO A4 paper size (210 × 297 mm). */
    A4(new SizeF(595.27563F, 841.8898F)),

    /** ISO A5 paper size (148 × 210 mm). */
    A5(new SizeF(419.52756F, 595.27563F)),

    /** ISO A6 paper size (105 × 148 mm). */
    A6(new SizeF(297.63782F, 419.52756F)),

    /** US Legal paper size (8.5 × 14 inches). */
    LEGAL(new SizeF(612.0F, 1008.0F)),

    /** US Letter paper size (8.5 × 11 inches). */
    LETTER(new SizeF(612.0F, 792.0F)),

    /**
     * Special sentinel value indicating that the page size should be determined by the
     * dimensions of the source image rather than a fixed standard size.
     * The stored {@link SizeF} is {@code (0, 0)} and should not be used for calculations.
     */
    DEPEND_ON_IMG(new SizeF(0, 0));

    /** Conversion factor from PDF points to inches (72 points per inch). */
    private static final float POINTS_PER_INCH = 72.0F;

    /** Conversion factor from PDF points to millimetres (approximately 2.835 points per mm). */
    private static final float POINTS_PER_MM = 2.8346457F;

    /** The page dimensions stored in PDF user units (points). */
    private final SizeF size;

    /**
     * Constructs a {@code PageSize} constant with the given point-unit dimensions.
     *
     * @param size the width and height in PDF points
     */
    PageSize(SizeF size) {
        this.size = size;
    }

    /**
     * Returns the page dimensions in PDF user units (points, 1/72 inch).
     *
     * @return a {@link SizeF} with width and height expressed in points
     */
    public SizeF getSizeInPixels() {
        return size;
    }

    /**
     * Returns the page dimensions converted to millimetres.
     *
     * @return a {@link SizeF} with width and height expressed in millimetres
     */
    public SizeF getSizeInMillimeters() {
        return new SizeF(size.width / POINTS_PER_MM, size.height / POINTS_PER_MM);
    }

    /**
     * Returns the page dimensions converted to inches.
     *
     * @return a {@link SizeF} with width and height expressed in inches
     */
    public SizeF getSizeInInches() {
        return new SizeF(size.width / POINTS_PER_INCH, size.height / POINTS_PER_INCH);
    }
}

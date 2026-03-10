package org.vincentyeh.img2pdf.lib.image;

import java.awt.color.ColorSpace;

/**
 * Defines the color space used when decoding a source image into a {@link java.awt.image.BufferedImage}.
 *
 * <p>The selected {@code ColorType} is passed to {@code ImageReader#readImage(File, ColorType)},
 * which converts the raw pixel data into the corresponding {@link ColorSpace} before the image is
 * rendered onto a PDF page.  Choosing the appropriate color type can significantly affect both the
 * visual fidelity and the size of the resulting PDF.</p>
 */
public enum ColorType {

    /**
     * Standard RGB color space ({@link ColorSpace#CS_sRGB}).
     * Use this for full-color photographs and illustrations.
     */
    sRGB(ColorSpace.CS_sRGB),

    /**
     * Grayscale color space ({@link ColorSpace#CS_GRAY}).
     * Use this for black-and-white or grayscale images to reduce PDF file size.
     */
    GRAY(ColorSpace.CS_GRAY);

    /** The {@link ColorSpace} constant from {@code java.awt.color.ColorSpace} for this color type. */
    private final int colorSpace;

    /**
     * Constructs a {@code ColorType} enum constant mapped to the given AWT color-space identifier.
     *
     * @param colorSpace one of the {@code CS_*} constants defined in {@link ColorSpace}
     */
    ColorType(int colorSpace) {
        this.colorSpace = colorSpace;
    }

    /**
     * Returns the AWT {@link ColorSpace} identifier associated with this color type.
     *
     * @return one of the {@code CS_*} constants defined in {@link ColorSpace}
     *         (e.g. {@link ColorSpace#CS_sRGB} or {@link ColorSpace#CS_GRAY})
     */
    public int getColorSpace() {
        return colorSpace;
    }
}

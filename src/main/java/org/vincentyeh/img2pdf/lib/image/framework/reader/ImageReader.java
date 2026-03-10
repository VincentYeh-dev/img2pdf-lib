package org.vincentyeh.img2pdf.lib.image.framework.reader;

import org.vincentyeh.img2pdf.lib.image.ColorType;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Defines the contract for reading image data from various sources into {@link BufferedImage} instances.
 *
 * <p>Implementations are expected to support format auto-detection and optional color space conversion.
 * When a {@link ColorType} is provided, the returned image will be converted to the specified color space.</p>
 */
public interface ImageReader {

    /**
     * Reads an image from the given input stream without color space conversion.
     *
     * @param inputStream the input stream containing raw image data; must not be {@code null}
     * @return the decoded {@link BufferedImage}
     * @throws IOException if an I/O error occurs while reading the stream
     */
    BufferedImage readImage(InputStream inputStream) throws IOException;

    /**
     * Reads an image from the given input stream and converts it to the specified color space.
     *
     * @param inputStream the input stream containing raw image data; must not be {@code null}
     * @param colorType   the target color space to convert to; if {@code null}, no conversion is applied
     * @return the decoded and color-converted {@link BufferedImage}
     * @throws IOException if an I/O error occurs while reading the stream
     */
    BufferedImage readImage(InputStream inputStream, ColorType colorType) throws IOException;

    /**
     * Reads an image from the given byte array without color space conversion.
     *
     * @param imageData the byte array containing raw image data; must not be {@code null}
     * @return the decoded {@link BufferedImage}
     * @throws IOException if an I/O error occurs while decoding the image data
     */
    BufferedImage readImage(byte[] imageData) throws IOException;

    /**
     * Reads an image from the given byte array and converts it to the specified color space.
     *
     * @param imageData the byte array containing raw image data; must not be {@code null}
     * @param colorType the target color space to convert to; if {@code null}, no conversion is applied
     * @return the decoded and color-converted {@link BufferedImage}
     * @throws IOException if an I/O error occurs while decoding the image data
     */
    BufferedImage readImage(byte[] imageData, ColorType colorType) throws IOException;

    /**
     * Reads an image from the given file without color space conversion.
     *
     * <p>Implementations may apply EXIF-based auto-rotation or other metadata-driven adjustments.</p>
     *
     * @param imagePath the image file to read; must not be {@code null}
     * @return the decoded {@link BufferedImage}
     * @throws IOException if an I/O error occurs while reading the file
     */
    BufferedImage readImage(File imagePath) throws IOException;

    /**
     * Reads an image from the given file and converts it to the specified color space.
     *
     * <p>Implementations may apply EXIF-based auto-rotation or other metadata-driven adjustments.</p>
     *
     * @param imagePath the image file to read; must not be {@code null}
     * @param colorType the target color space to convert to; if {@code null}, no conversion is applied
     * @return the decoded and color-converted {@link BufferedImage}
     * @throws IOException if an I/O error occurs while reading the file
     */
    BufferedImage readImage(File imagePath, ColorType colorType) throws IOException;
}

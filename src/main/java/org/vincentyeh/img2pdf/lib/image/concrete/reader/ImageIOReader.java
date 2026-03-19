package org.vincentyeh.img2pdf.lib.image.concrete.reader;

import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.image.ImageReadingException;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageOrientationReader;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.nio.file.Files;
import java.util.OptionalInt;

/**
 * A singleton {@link ImageReader} implementation backed by {@code javax.imageio.ImageIO}
 * and TwelveMonkeys ImageIO plugins (e.g., WebP support).
 *
 * <p>When reading from a {@link File}, this implementation automatically applies EXIF
 * orientation correction by delegating to an {@link ImageOrientationReader} strategy
 * to read the TIFF Orientation tag, then rotating the image accordingly.</p>
 *
 * <p>Instantiation is controlled via the {@link InstanceHolder} pattern to ensure
 * thread-safe lazy initialization without synchronization overhead.</p>
 */
public final class ImageIOReader implements ImageReader {

    /** Strategy for reading EXIF orientation from image files. */
    private final ImageOrientationReader orientationReader;

    /**
     * Lazy-initialization holder for the singleton instance.
     * The JVM guarantees that the class initializer runs exactly once.
     */
    private static final class InstanceHolder {
        static final ImageReader instance = new ImageIOReader(new ExifOrientationReader());
    }

    /**
     * Returns the singleton instance of {@code ImageIOReader}.
     *
     * @return the shared {@link ImageReader} instance
     */
    public static ImageReader getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * Constructor for dependency injection.
     * Production code should use {@link #getInstance()} which injects {@link ExifOrientationReader}.
     * This constructor is also used in tests to inject a mock {@link ImageOrientationReader}.
     *
     * @param orientationReader the strategy to use for reading EXIF orientation
     */
    // package-private visibility sufficient for same-package access; tests in other packages use public
    public ImageIOReader(ImageOrientationReader orientationReader) {
        this.orientationReader = orientationReader;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates directly to {@link ImageIO#read(InputStream)}.</p>
     *
     * @throws ImageReadingException if {@code inputStream} is {@code null}
     */
    @Override
    public BufferedImage readImage(InputStream inputStream) throws IOException {
        if (inputStream == null)
            throw new ImageReadingException("inputStream must not be null", null);
        return ImageIO.read(inputStream);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Reads the image from the stream, then converts its color space via
     * {@link #convertColorSpace(BufferedImage, ColorType)}.</p>
     *
     * @throws ImageReadingException if {@code inputStream} is {@code null}, or if the image
     *                               format is unsupported and {@link ImageIO#read} returns {@code null}
     */
    @Override
    public BufferedImage readImage(InputStream inputStream, ColorType colorType) throws IOException {
        if (inputStream == null)
            throw new ImageReadingException("inputStream must not be null", null);
        BufferedImage img = readImage(inputStream);
        if (img == null)
            throw new ImageReadingException("Unable to decode image from InputStream: unsupported format", null);
        return convertColorSpace(img, colorType);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Wraps the byte array in a {@link ByteArrayInputStream} and delegates to
     * {@link ImageIO#read(InputStream)}.</p>
     *
     * @throws ImageReadingException if {@code imageData} is {@code null}
     */
    @Override
    public BufferedImage readImage(byte[] imageData) throws IOException {
        if (imageData == null)
            throw new ImageReadingException("imageData must not be null", null);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        return ImageIO.read(inputStream);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Reads the image from the byte array, then converts its color space via
     * {@link #convertColorSpace(BufferedImage, ColorType)}.</p>
     *
     * @throws ImageReadingException if {@code imageData} is {@code null}, or if the image
     *                               format is unsupported and {@link ImageIO#read} returns {@code null}
     */
    @Override
    public BufferedImage readImage(byte[] imageData, ColorType colorType) throws IOException {
        if (imageData == null)
            throw new ImageReadingException("imageData must not be null", null);
        BufferedImage img = readImage(imageData);
        if (img == null)
            throw new ImageReadingException("Unable to decode image from byte[]: unsupported format", null);
        return convertColorSpace(img, colorType);
    }

    /**
     * {@inheritDoc}
     *
     * <p>In addition to decoding the image, this implementation delegates to
     * {@link ImageOrientationReader#readOrientation(File)} to read the EXIF Orientation
     * tag from the file and rotates the image to match the intended display orientation.</p>
     *
     * @throws IllegalArgumentException if {@code file} is {@code null}
     * @throws ImageReadingException    if an I/O error occurs while reading the file or its metadata
     */
    @Override
    public BufferedImage readImage(File file) throws ImageReadingException {
        if (file == null)
            throw new IllegalArgumentException("image file equals null");

        try {
            final BufferedImage rawImage;
            try (InputStream is = Files.newInputStream(file.toPath())) {
                rawImage = readImage(is);
            }
            if (rawImage == null)
                throw new ImageReadingException("Unable to read image file: " + file.toPath(), null);
            OptionalInt orientationOpt = orientationReader.readOrientation(file);
            if (orientationOpt.isPresent()) {
                return applyOrientation(rawImage, orientationOpt.getAsInt());
            }
            return rawImage;
        } catch (IOException e) {
            throw new ImageReadingException("Unable to handle metadata", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Reads the image from the file (with EXIF orientation correction), then converts
     * its color space via {@link #convertColorSpace(BufferedImage, ColorType)}.</p>
     *
     * @throws ImageReadingException if {@link #readImage(File)} returns {@code null},
     *                               indicating an unsupported image format
     */
    @Override
    public BufferedImage readImage(File imagePath, ColorType colorType) throws IOException {
        BufferedImage img = readImage(imagePath);
        if (img == null)
            throw new ImageReadingException("Unable to decode image from file: unsupported format", null);
        return convertColorSpace(img, colorType);
    }

    /**
     * Converts the color space of the given image to the color space indicated by {@code colorType}.
     *
     * <p>If {@code colorType} is {@code null}, the image's existing color space is used (no conversion).
     * If the image already matches the target color space type, the original image is returned unchanged.</p>
     *
     * @param image     the source image to convert; must not be {@code null}
     * @param colorType the desired target color space; may be {@code null} to skip conversion
     * @return the color-converted image, or the original image if no conversion is needed
     */
    public BufferedImage convertColorSpace(BufferedImage image, ColorType colorType) {
        final ColorSpace targetColorSpace;
        if (colorType != null)
            targetColorSpace = ColorSpace.getInstance(colorType.getColorSpace());
        else
            targetColorSpace = image.getColorModel().getColorSpace();

        if (image.getColorModel().getColorSpace().getType() == targetColorSpace.getType()) return image;

        ColorConvertOp op = new ColorConvertOp(targetColorSpace, null);
        return op.filter(image, null);
    }

    /**
     * Returns a safe image type for creating a new {@link BufferedImage}.
     * If the source image type is {@link BufferedImage#TYPE_CUSTOM} (0),
     * falls back to {@link BufferedImage#TYPE_INT_ARGB} to avoid
     * {@link IllegalArgumentException} from the {@code BufferedImage} constructor.
     *
     * @param img the source image; must not be {@code null}
     * @return a valid {@code BufferedImage} type constant
     */
    private static int safeImageType(BufferedImage img) {
        int type = img.getType();
        return type == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_ARGB : type;
    }

    /**
     * Rotates the given image by the specified number of degrees clockwise.
     *
     * <p>The resulting image is sized to fully contain the rotated content.
     * If {@code degrees} is 0 or 360, the original image is returned unchanged.</p>
     *
     * @param img     the source image to rotate; must not be {@code null}
     * @param degrees the clockwise rotation angle in degrees
     * @return the rotated image, or the original if no rotation is needed
     */
    private static BufferedImage rotateImage(BufferedImage img, double degrees) {
        if (degrees == 0 || degrees == 360)
            return img;
        double rads = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();

        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, safeImageType(img));
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2., (newHeight - h) / 2.);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return rotated;
    }

    /**
     * Flips the given image either horizontally or vertically using {@link AffineTransform}.
     *
     * @param img        the source image to flip; must not be {@code null}
     * @param horizontal {@code true} to flip left-right (mirror horizontal);
     *                   {@code false} to flip top-bottom (mirror vertical)
     * @return the flipped image
     */
    private static BufferedImage flipImage(BufferedImage img, boolean horizontal) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage flipped = new BufferedImage(w, h, safeImageType(img));
        Graphics2D g2d = flipped.createGraphics();
        AffineTransform at = new AffineTransform();
        if (horizontal) {
            // mirror horizontal: scale x by -1, then translate back
            at.translate(w, 0);
            at.scale(-1, 1);
        } else {
            // mirror vertical: scale y by -1, then translate back
            at.translate(0, h);
            at.scale(1, -1);
        }
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return flipped;
    }

    /**
     * Applies EXIF orientation correction to the given image by handling all 8 standard
     * EXIF Orientation values via rotation and/or mirroring.
     *
     * <p>Reference: <a href="https://exiftool.org/TagNames/EXIF.html">ExifTool EXIF Tag Names</a></p>
     * <pre>
     *   1 = Horizontal (normal)            → no-op
     *   2 = Mirror horizontal              → flipImage(img, true)
     *   3 = Rotate 180                     → rotateImage(img, 180)
     *   4 = Mirror vertical                → flipImage(img, false)
     *   5 = Mirror horizontal + Rotate 270 → rotateImage(flipImage(img, true), 270)
     *   6 = Rotate 90 CW                   → rotateImage(img, 90)
     *   7 = Mirror horizontal + Rotate 90  → rotateImage(flipImage(img, true), 90)
     *   8 = Rotate 270 CW                  → rotateImage(img, 270)
     *   default (including 0)              → no-op (return original)
     * </pre>
     *
     * @param img         the source image to correct; must not be {@code null}
     * @param orientation the EXIF orientation value (0–8)
     * @return the corrected image, or the original image for orientation 1 / unknown values
     */
    private static BufferedImage applyOrientation(BufferedImage img, int orientation) {
        switch (orientation) {
            case 1: // Horizontal (normal) — no-op
                return img;
            case 2: // Mirror horizontal
                return flipImage(img, true);
            case 3: // Rotate 180
                return rotateImage(img, 180);
            case 4: // Mirror vertical
                return flipImage(img, false);
            case 5: // Mirror horizontal + Rotate 270 CW
                return rotateImage(flipImage(img, true), 270);
            case 6: // Rotate 90 CW
                return rotateImage(img, 90);
            case 7: // Mirror horizontal + Rotate 90 CW
                return rotateImage(flipImage(img, true), 90);
            case 8: // Rotate 270 CW
                return rotateImage(img, 270);
            default: // unknown or out-of-range orientation — log warning and return as-is
                System.err.println("[ImageIOReader] WARNING: Unrecognized EXIF orientation value: "
                        + orientation + "; image returned without transformation.");
                return img;
        }
    }

}

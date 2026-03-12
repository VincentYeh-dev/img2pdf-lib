package org.vincentyeh.img2pdf.lib.image.concrete.reader;

import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFReader;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.image.ImageReadingException;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
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
 * orientation correction by reading the TIFF Orientation tag from JPEG or TIFF files
 * and rotating the image accordingly.</p>
 *
 * <p>Instantiation is controlled via the {@link InstanceHolder} pattern to ensure
 * thread-safe lazy initialization without synchronization overhead.</p>
 */
public final class ImageIOReader implements ImageReader {

    /**
     * Lazy-initialization holder for the singleton instance.
     * The JVM guarantees that the class initializer runs exactly once.
     */
    private static final class InstanceHolder {
        static final ImageReader instance = new ImageIOReader();
    }

    /**
     * Returns the singleton instance of {@code ImageIOReader}.
     *
     * @return the shared {@link ImageReader} instance
     */
    public static ImageReader getInstance() {
        return InstanceHolder.instance;
    }

    /** Prevents external instantiation; use {@link #getInstance()} instead. */
    private ImageIOReader() {

    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates directly to {@link ImageIO#read(InputStream)}.</p>
     */
    @Override
    public BufferedImage readImage(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Reads the image from the stream, then converts its color space via
     * {@link #convertColorSpace(BufferedImage, ColorType)}.</p>
     */
    @Override
    public BufferedImage readImage(InputStream inputStream, ColorType colorType) throws IOException {
        return convertColorSpace(readImage(inputStream), colorType);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Wraps the byte array in a {@link ByteArrayInputStream} and delegates to
     * {@link ImageIO#read(InputStream)}.</p>
     */
    @Override
    public BufferedImage readImage(byte[] imageData) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        return ImageIO.read(inputStream);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Reads the image from the byte array, then converts its color space via
     * {@link #convertColorSpace(BufferedImage, ColorType)}.</p>
     */
    @Override
    public BufferedImage readImage(byte[] imageData, ColorType colorType) throws IOException {
        return convertColorSpace(readImage(imageData), colorType);
    }

    /**
     * {@inheritDoc}
     *
     * <p>In addition to decoding the image, this implementation reads the EXIF Orientation
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
            BufferedImage rawImage = readImage(Files.newInputStream(file.toPath()));
            OptionalInt orientationOpt = readExifOrientation(file);
            if (orientationOpt.isPresent()) {
                double angle = orientationToAngle(orientationOpt.getAsInt());
                return rotateImage(rawImage, angle);
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
     */
    @Override
    public BufferedImage readImage(File imagePath, ColorType colorType) throws IOException {
        return convertColorSpace(readImage(imagePath), colorType);
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

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, img.getType());
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
     * Reads the EXIF Orientation tag from a file.
     * Supports JPEG and TIFF formats via magic byte detection.
     *
     * @param file the image file to inspect; must not be {@code null}
     * @return an {@link OptionalInt} containing the orientation value (1–8),
     *         or {@link OptionalInt#empty()} if no orientation tag is found
     * @throws IOException if an I/O error occurs while reading the file
     */
    private static OptionalInt readExifOrientation(File file) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            if (iis == null) return OptionalInt.empty();
            if (!positionAtExifTiff(iis)) return OptionalInt.empty();

            Directory dir = new TIFFReader().read(iis);
            Directory ifd0 = (dir instanceof CompoundDirectory)
                    ? ((CompoundDirectory) dir).getDirectory(0) : dir;

            Entry entry = ifd0.getEntryById(TIFF.TAG_ORIENTATION);
            if (entry == null) return OptionalInt.empty();
            return OptionalInt.of(((Number) entry.getValue()).intValue());
        }
    }

    /**
     * Positions the stream at the start of the TIFF header containing EXIF data.
     * Detects format by magic bytes: 0xFFD8=JPEG, 0x4949/0x4D4D=TIFF.
     *
     * @param iis the image input stream to inspect; must not be {@code null}
     * @return {@code true} if the stream is successfully positioned at a TIFF header,
     *         {@code false} if the format is not recognized or no EXIF data is found
     * @throws IOException if an I/O error occurs while reading the stream
     */
    private static boolean positionAtExifTiff(ImageInputStream iis) throws IOException {
        long startPos = iis.getStreamPosition();
        int magic = iis.readUnsignedShort();

        if (magic == 0xFFD8) {
            return seekJpegExifTiff(iis);
        } else if (magic == 0x4949 || magic == 0x4D4D) {
            iis.seek(startPos);
            return true;
        }
        return false;
    }

    /**
     * Scans JPEG markers to find the APP1 EXIF segment,
     * positioning the stream at the embedded TIFF header.
     *
     * @param iis the image input stream positioned immediately after the SOI marker; must not be {@code null}
     * @return {@code true} if the stream is positioned at the TIFF header within the APP1 EXIF segment,
     *         {@code false} if the EXIF segment is not found before the end-of-image marker
     * @throws IOException if an I/O error occurs while scanning the JPEG markers
     */
    private static boolean seekJpegExifTiff(ImageInputStream iis) throws IOException {
        try {
            while (true) {
                int marker = iis.readUnsignedShort();
                int length = iis.readUnsignedShort();
//                APP1:EXIF
                if (marker == 0xFFE1) {
                    byte[] header = new byte[6];
                    iis.readFully(header);
                    if (header[0] == 'E' && header[1] == 'x' && header[2] == 'i'
                            && header[3] == 'f' && header[4] == 0 && header[5] == 0) {
                        return true;
                    }
                    iis.skipBytes(length - 2 - 6);

//                APP0:JIFF
                }else if(marker==0xFFE0){
                    return false;
                }else if (marker == 0xFFD9 || marker == 0xFFDA) {
                    // EOI or SOS — EXIF is always in the header before SOS; stop scanning
                    return false;
                } else {
                    iis.skipBytes(length - 2);
                }
            }
        } catch (java.io.EOFException e) {
            // JPEG has no EXIF segment; treat as no orientation
            return false;
        }
    }

    /**
     * Converts an EXIF Orientation value to a clockwise rotation angle in degrees.
     *
     * <p>Reference: <a href="https://exiftool.org/TagNames/EXIF.html">ExifTool EXIF Tag Names</a></p>
     * <pre>
     *   1 = Horizontal (normal)
     *   2 = Mirror horizontal
     *   3 = Rotate 180
     *   4 = Mirror vertical
     *   5 = Mirror horizontal and rotate 270 CW
     *   6 = Rotate 90 CW
     *   7 = Mirror horizontal and rotate 90 CW
     *   8 = Rotate 270 CW
     * </pre>
     *
     * <p>TODO: Add support for mirror-based orientation values (2, 4, 5, 7) in IFD0.</p>
     *
     * @param orientation the EXIF orientation value (0–8)
     * @return the corresponding clockwise rotation angle in degrees
     * @throws IllegalStateException if the orientation value is not currently supported
     */
    private static double orientationToAngle(int orientation) {
        switch (orientation) {
            case 0:
            case 1: // [Exif IFD0] Orientation - Top, left side (Horizontal / normal)
                return 0;
            case 6: // [Exif IFD0] Orientation - Right side, top (Rotate 90 CW)
                return 90;
            case 3: // [Exif IFD0] Orientation - Bottom, right side (Rotate 180)
                return 180;
            case 8: // [Exif IFD0] Orientation - Left side, bottom (Rotate 270 CW)
                return 270;
            default:
                throw new IllegalStateException("orientation==" + orientation);
        }
    }

}

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
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.nio.file.Files;
import java.util.OptionalInt;

public final class ImageIOReader implements ImageReader {

    private static final class InstanceHolder {
        static final ImageReader instance = new ImageIOReader();
    }

    public static ImageReader getInstance() {
        return InstanceHolder.instance;
    }

    private ImageIOReader() {

    }

    @Override
    public BufferedImage readImage(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }

    @Override
    public BufferedImage readImage(InputStream inputStream, ColorType colorType) throws IOException {
        return convertColorSpace(readImage(inputStream), colorType);
    }

    @Override
    public BufferedImage readImage(byte[] imageData) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        return ImageIO.read(inputStream);
    }

    @Override
    public BufferedImage readImage(byte[] imageData, ColorType colorType) throws IOException {
        return convertColorSpace(readImage(imageData), colorType);
    }

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

    @Override
    public BufferedImage readImage(File imagePath, ColorType colorType) throws IOException {
        return convertColorSpace(readImage(imagePath), colorType);
    }

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
        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        g2d.dispose();
        return rotated;
    }

    /**
     * Reads the EXIF Orientation tag from a file.
     * Supports JPEG and TIFF formats via magic byte detection.
     *
     * @return orientation value (1-8), or empty if not present
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
     * @return true if successfully positioned, false if no EXIF found
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
     * Scans JPEG markers to find APP1 EXIF segment,
     * positioning the stream at the embedded TIFF header.
     */
    private static boolean seekJpegExifTiff(ImageInputStream iis) throws IOException {
        while (true) {
            int marker = iis.readUnsignedShort();
            int length = iis.readUnsignedShort();

            if (marker == 0xFFE1) {
                byte[] header = new byte[6];
                iis.readFully(header);
                if (header[0] == 'E' && header[1] == 'x' && header[2] == 'i'
                        && header[3] == 'f' && header[4] == 0 && header[5] == 0) {
                    return true;
                }
                iis.skipBytes(length - 2 - 6);
            } else if (marker == 0xFFD9) {
                return false;
            } else {
                iis.skipBytes(length - 2);
            }
        }
    }

    private static double orientationToAngle(int orientation) {
        switch (orientation) {
//          https://exiftool.org/TagNames/EXIF.html
//            1 = Horizontal (normal)
//            2 = Mirror horizontal
//            3 = Rotate 180
//            4 = Mirror vertical
//            5 = Mirror horizontal and rotate 270 CW
//            6 = Rotate 90 CW
//            7 = Mirror horizontal and rotate 90 CW
//            8 = Rotate 270 CW

//          TODO:更改IFDO的旋轉角度
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

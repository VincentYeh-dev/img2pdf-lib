package org.vincentyeh.img2pdf.lib.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

public final class ImageUtils {
//    private static final ImageReader reader = new MetaImageReader();

    private ImageUtils() {

    }

    public static BufferedImage readImage(File file, ColorType colorType) {
        BufferedImage image;
        try {
            if (canHandleMetaData(file)) {
                double rotate_angle = handleMetaData(file);
                image = rotateImage(ImageIO.read(file), rotate_angle);
            } else {
                image = ImageIO.read(file);
            }

            final ColorSpace targetColorSpace = ColorSpace.getInstance(colorType.getColorSpace());
            ColorSpace image_cs = image.getColorModel().getColorSpace();

            if (image_cs.getType() == targetColorSpace.getType()) return image;

            if (image_cs.getType() == ColorSpace.TYPE_GRAY) return image;

            ColorConvertOp op = new ColorConvertOp(targetColorSpace, null);
            return op.filter(image, null);

        } catch (ImageProcessingException | IOException | MetadataException e) {
            throw new ImageReadException(e);
        }

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


    private static double handleMetaData(File file) throws ImageProcessingException, IOException, MetadataException {
        Metadata metadata = ImageMetadataReader.readMetadata(file);
        ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        int orientation = exifIFD0.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        switch (orientation) {
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

    private static boolean canHandleMetaData(File file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            if (metadata.containsDirectoryOfType(ExifIFD0Directory.class)) {
                ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                return exifIFD0.containsTag(ExifIFD0Directory.TAG_ORIENTATION);
            }
            return false;
        } catch (ImageProcessingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}

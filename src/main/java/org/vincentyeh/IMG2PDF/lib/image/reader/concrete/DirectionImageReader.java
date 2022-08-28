package org.vincentyeh.img2pdf.lib.image.reader.concrete;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

public class DirectionImageReader implements ImageReader {


    private final ImageReader imageReader;

    public DirectionImageReader() {
        this(ImageReader.getDefault());
    }
    public DirectionImageReader(@NotNull ImageReader imageReader) {
        try {
            this.imageReader = Objects.requireNonNull(imageReader, "imageReader==null");
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public final BufferedImage read(File file) throws ImageReadException{
        try {
            var metadata = ImageMetadataReader.readMetadata(file);
            var image= imageReader.read(file);
            if (metadata.containsDirectoryOfType(ExifIFD0Directory.class))
                return handleExifIFD0(image, metadata);
            return image;
        } catch (Exception e) {
            throw new ImageReadException(e);
        }
    }

    private BufferedImage handleExifIFD0(BufferedImage image, Metadata metadata) throws MetadataException {
        ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (!exifIFD0.containsTag(ExifIFD0Directory.TAG_ORIENTATION))
            return image;

        int orientation = exifIFD0.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        return switch (orientation) {
            case 1 -> // [Exif IFD0] Orientation - Top, left side (Horizontal / normal)
                    image;
            case 6 -> // [Exif IFD0] Orientation - Right side, top (Rotate 90 CW)
                    rotateImage(image, 90);
            case 3 -> // [Exif IFD0] Orientation - Bottom, right side (Rotate 180)
                    rotateImage(image, 180);
            case 8 -> // [Exif IFD0] Orientation - Left side, bottom (Rotate 270 CW)
                    rotateImage(image, 270);
            default -> throw new RuntimeException("image==null");
        };
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
}
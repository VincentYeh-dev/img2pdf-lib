package org.vincentyeh.img2pdf.lib.image.reader.concrete;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageFileChainReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ExifImageChainReader extends ImageFileChainReader {
    @Override
    protected BufferedImage handleImpl(File file) throws Exception {

        var metadata = ImageMetadataReader.readMetadata(file);
        var image = ImageIO.read(file);

        ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

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

    @Override
    protected boolean canHandle(File file) {
        try {
            var metadata = ImageMetadataReader.readMetadata(file);
            if (metadata.containsDirectoryOfType(ExifIFD0Directory.class)) {
                var exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                return exifIFD0.containsTag(ExifIFD0Directory.TAG_ORIENTATION);
            }
            return false;
        } catch (ImageProcessingException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}

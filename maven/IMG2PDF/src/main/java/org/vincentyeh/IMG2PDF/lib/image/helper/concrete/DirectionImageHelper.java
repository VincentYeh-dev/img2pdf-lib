package org.vincentyeh.IMG2PDF.lib.image.helper.concrete;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.vincentyeh.IMG2PDF.lib.image.helper.framework.ImageHelper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DirectionImageHelper extends ImageHelper {

    public DirectionImageHelper() {

    }

    public DirectionImageHelper(ImageHelper imageHelper) {
        super(imageHelper);
    }

    @Override
    protected final BufferedImage read(File file, BufferedImage image) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(file);
        if (metadata.containsDirectoryOfType(ExifIFD0Directory.class))
            return handleExifIFD0(image, metadata);
        return image;
    }

    private BufferedImage handleExifIFD0(BufferedImage image, Metadata metadata) throws MetadataException, IOException {
        ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (!exifIFD0.containsTag(ExifIFD0Directory.TAG_ORIENTATION))
            return image;

        int orientation = exifIFD0.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        switch (orientation) {
            case 1: // [Exif IFD0] Orientation - Top, left side (Horizontal / normal)
                return image;
            case 6: // [Exif IFD0] Orientation - Right side, top (Rotate 90 CW)
                return rotateImage(image, 90);
            case 3: // [Exif IFD0] Orientation - Bottom, right side (Rotate 180)
                return rotateImage(image, 180);
            case 8: // [Exif IFD0] Orientation - Left side, bottom (Rotate 270 CW)
                return rotateImage(image, 270);
            default:
                throw new RuntimeException("image==null");
        }
    }
}
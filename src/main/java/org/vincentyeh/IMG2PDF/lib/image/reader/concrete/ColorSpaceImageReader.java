package org.vincentyeh.img2pdf.lib.image.reader.concrete;

import com.drew.lang.annotations.NotNull;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReader;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.util.Objects;

public class ColorSpaceImageReader implements ImageReader {

    private final ImageReader imageReader;
    private final ColorSpace cs;

    public ColorSpaceImageReader(@NotNull ColorSpace cs) {
        this(ImageReader.getDefault(), cs);
    }

    public ColorSpaceImageReader(@NotNull ImageReader imageReader, ColorSpace cs) {
        try {
            this.imageReader = Objects.requireNonNull(imageReader, "imageReader==null");
            this.cs = Objects.requireNonNull(cs, "cs==null");
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public final BufferedImage read(File file) throws ImageReadException {
        try {
            var image = imageReader.read(file);
            ColorSpace image_cs = image.getColorModel().getColorSpace();
            if (image_cs.getType() == cs.getType())
                return image;

            if (image_cs.getType() == ColorSpace.TYPE_GRAY)
                return image;

            ColorConvertOp op = new ColorConvertOp(cs, null);
            return op.filter(image, null);
        } catch (Exception e) {
            throw new ImageReadException(e);
        }
    }
}

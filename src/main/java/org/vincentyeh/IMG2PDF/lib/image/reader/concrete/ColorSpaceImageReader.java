package org.vincentyeh.img2pdf.lib.image.reader.concrete;

import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReader;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;

public class ColorSpaceImageReader implements ImageReader {

    private final ImageReader imageReader;
    private final ColorSpace cs;

    public ColorSpaceImageReader (ColorSpace cs) {
        this(ImageReader.getDefault(),cs);
    }
    public ColorSpaceImageReader (ImageReader imageReader,ColorSpace cs) {
        this.imageReader = imageReader;
        this.cs = cs;
    }

    @Override
    public final BufferedImage read(File file) throws Exception {
        var image=imageReader.read(file);
        ColorSpace image_cs = image.getColorModel().getColorSpace();
        if (image_cs.getType() == cs.getType())
            return image;

        if (image_cs.getType() == ColorSpace.TYPE_GRAY)
            return image;

        ColorConvertOp op = new ColorConvertOp(cs, null);
        return op.filter(image, null);
    }
}

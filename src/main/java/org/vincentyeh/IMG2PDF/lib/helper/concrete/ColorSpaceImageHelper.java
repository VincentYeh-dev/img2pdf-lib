package org.vincentyeh.img2pdf.lib.image.helper.concrete;

import org.vincentyeh.img2pdf.lib.image.helper.framework.ImageHelper;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;

public class ColorSpaceImageHelper extends ImageHelper {

    private final ColorSpace cs;

    public ColorSpaceImageHelper(ColorSpace cs) {
        this.cs = cs;
    }

    @Override
    protected final BufferedImage read(File file, BufferedImage image) {
        ColorSpace image_cs = image.getColorModel().getColorSpace();
        if (image_cs.getType() == cs.getType())
            return image;

        if (image_cs.getType() == ColorSpace.TYPE_GRAY)
            return image;

        ColorConvertOp op = new ColorConvertOp(cs, null);
        return op.filter(image, null);
    }
}
package org.vincentyeh.IMG2PDF.image.helper.concrete;

import org.vincentyeh.IMG2PDF.image.helper.framework.ImageHelper;

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
        ColorConvertOp op = new ColorConvertOp(cs, null);
        return op.filter(image, null);
    }
}
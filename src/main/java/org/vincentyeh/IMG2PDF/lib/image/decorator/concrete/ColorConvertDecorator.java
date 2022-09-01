package org.vincentyeh.img2pdf.lib.image.decorator.concrete;

import org.vincentyeh.img2pdf.lib.image.reader.framework.ColorType;
import org.vincentyeh.img2pdf.lib.image.decorator.framework.ImageDecorator;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class ColorConvertDecorator extends ImageDecorator {

    private final ColorSpace targetColorSpace;

    public ColorConvertDecorator(ColorType colorType, ImageDecorator decorator) {
        super(decorator);
        targetColorSpace = ColorSpace.getInstance(colorType.getColorSpace());
    }

    @Override
    protected BufferedImage decorateImpl(BufferedImage image) {
        ColorSpace image_cs = image.getColorModel().getColorSpace();
        if (image_cs.getType() == targetColorSpace.getType())
            return image;

        if (image_cs.getType() == ColorSpace.TYPE_GRAY)
            return image;

        var op = new ColorConvertOp(targetColorSpace, null);
        return op.filter(image, null);
    }
}

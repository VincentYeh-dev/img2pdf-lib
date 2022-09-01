package org.vincentyeh.img2pdf.lib.image.decorator.framework;

import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReadException;

import java.awt.image.BufferedImage;

public abstract class ImageDecorator {


    private final ImageDecorator imageDecorator;

    public ImageDecorator(ImageDecorator imageDecorator) {
        this.imageDecorator = imageDecorator;
    }

    protected abstract BufferedImage decorateImpl(BufferedImage image) throws Exception;

    public final BufferedImage decorate(BufferedImage image) throws ImageReadException {
        try {
            if (imageDecorator != null)
                return decorateImpl(imageDecorator.decorate(image));
            else
                return decorateImpl(image);

        } catch (Exception e) {
            throw new ImageReadException(e);
        }
    }
}

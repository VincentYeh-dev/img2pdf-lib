package org.vincentyeh.img2pdf.lib.image;

import org.vincentyeh.img2pdf.lib.image.decorator.concrete.ColorConvertDecorator;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.DefaultImageChainReader;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.ExifImageChainReader;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ColorType;
import org.vincentyeh.img2pdf.lib.image.decorator.framework.ImageDecorator;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageFileChainReader;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReadException;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReader;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageReaderFacade {
    private ImageReaderFacade() {

    }

    public static ImageReader getImageReader(ColorType colorType) {
        ImageFileChainReader reader = new ExifImageChainReader()
                .setNextChain(new DefaultImageChainReader());
        ColorConvertDecorator decorator = new ColorConvertDecorator(colorType, null);

        return new ImageDecorateReader(reader, decorator);

    }

    private static class ImageDecorateReader implements ImageReader {
        private final ImageReader imageReader;
        private final ImageDecorator imageDecorator;

        public ImageDecorateReader(ImageReader imageReader,
                                   ImageDecorator imageDecorator) {
            this.imageReader = imageReader;
            this.imageDecorator = imageDecorator;
        }

        @Override
        public BufferedImage read(File file) throws ImageReadException {
            return imageDecorator.decorate(imageReader.read(file));
        }
    }
}

package org.vincentyeh.img2pdf.lib.image;

import org.vincentyeh.img2pdf.lib.image.decorator.concrete.ColorConvertDecorator;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.DefaultImageChainReader;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.ExifImageChainReader;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ColorType;
import org.vincentyeh.img2pdf.lib.image.decorator.framework.ImageDecorator;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReadException;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReader;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageReaderFacade {
    private ImageReaderFacade() {

    }

    public static ImageReader getImageReader(ColorType colorType) {
        var reader = new ExifImageChainReader()
                .setNextChain(new DefaultImageChainReader());
        var decorator=new ColorConvertDecorator(colorType,null);

        return new ImageDecorateReader(reader,decorator);

    }

    private record ImageDecorateReader(ImageReader imageReader, ImageDecorator imageDecorator) implements ImageReader {
        @Override
            public BufferedImage read(File file) throws ImageReadException {
                return imageDecorator.decorate(imageReader.read(file));
            }
        }
}

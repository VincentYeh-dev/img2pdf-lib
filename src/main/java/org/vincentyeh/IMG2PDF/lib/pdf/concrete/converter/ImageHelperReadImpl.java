package org.vincentyeh.img2pdf.lib.pdf.concrete.converter;

import org.vincentyeh.img2pdf.lib.image.helper.framework.ImageHelper;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.exception.ReadImageException;
import org.vincentyeh.img2pdf.lib.pdf.function.converter.ImageReadImpl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

public class ImageHelperReadImpl implements ImageReadImpl {
    private final ImageHelper helper;

    public ImageHelperReadImpl(ImageHelper helper) {
        this.helper = helper;
    }

    @Override
    public BufferedImage readImage(File file) throws ReadImageException {
        try {
            if(!file.exists())
                throw new FileNotFoundException(file.getAbsolutePath()+" not found");
            BufferedImage image = helper.read(file);
            if (image == null)
                throw new RuntimeException("image==null");
            return image;
        } catch (Exception e) {
            throw new ReadImageException(e, file);
        }
    }
}

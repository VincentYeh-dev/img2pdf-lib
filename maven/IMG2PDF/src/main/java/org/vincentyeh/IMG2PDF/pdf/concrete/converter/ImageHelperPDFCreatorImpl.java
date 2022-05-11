package org.vincentyeh.IMG2PDF.pdf.concrete.converter;

import org.vincentyeh.IMG2PDF.image.helper.framework.ImageHelper;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.exception.ReadImageException;
import org.vincentyeh.IMG2PDF.pdf.function.converter.ImagePDFCreatorImpl;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageHelperPDFCreatorImpl implements ImagePDFCreatorImpl {
    private final ImageHelper helper;

    public ImageHelperPDFCreatorImpl(ImageHelper helper) {
        this.helper = helper;
    }

    @Override
    public BufferedImage readImage(File file) throws ReadImageException {
        try {
            FileUtils.checkExists(file);
            BufferedImage image = helper.read(file);
            if (image == null)
                throw new RuntimeException("image==null");
            return image;
        } catch (Exception e) {
            throw new ReadImageException(e, file);
        }
    }
}

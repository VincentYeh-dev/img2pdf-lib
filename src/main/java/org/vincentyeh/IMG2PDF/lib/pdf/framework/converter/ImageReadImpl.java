package org.vincentyeh.img2pdf.lib.pdf.framework.converter;

import org.vincentyeh.img2pdf.lib.pdf.framework.converter.exception.ReadImageException;

import java.awt.image.BufferedImage;
import java.io.File;

public interface ImageReadImpl {
    BufferedImage readImage(File file) throws ReadImageException;
}

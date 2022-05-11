package org.vincentyeh.IMG2PDF.pdf.function.converter;

import org.vincentyeh.IMG2PDF.pdf.framework.converter.exception.ReadImageException;

import java.awt.image.BufferedImage;
import java.io.File;

public interface ImagePDFCreatorImpl {
    BufferedImage readImage(File file) throws ReadImageException;
}

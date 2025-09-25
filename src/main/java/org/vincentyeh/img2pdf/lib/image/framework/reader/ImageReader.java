package org.vincentyeh.img2pdf.lib.image.framework.reader;

import org.vincentyeh.img2pdf.lib.image.ColorType;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ImageReader {
    BufferedImage readImage(InputStream inputStream) throws IOException;

    BufferedImage readImage(InputStream inputStream, ColorType colorType) throws IOException;

    BufferedImage readImage(byte[] imageData) throws IOException;

    BufferedImage readImage(byte[] imageData, ColorType colorType) throws IOException;

    BufferedImage readImage(File imagePath) throws IOException;

    BufferedImage readImage(File imagePath, ColorType colorType) throws IOException;
}

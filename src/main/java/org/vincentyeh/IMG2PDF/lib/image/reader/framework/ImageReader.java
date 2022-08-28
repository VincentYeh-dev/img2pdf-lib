package org.vincentyeh.img2pdf.lib.image.reader.framework;

import org.vincentyeh.img2pdf.lib.image.reader.concrete.ImageReadException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface ImageReader {

    BufferedImage read(File file) throws ImageReadException;

    static ImageReader getDefault() {
        return file -> {
            try {
                return ImageIO.read(file);
            } catch (IOException e) {
                throw new ImageReadException(e);
            }
        };
    }
}

package org.vincentyeh.img2pdf.lib.image.reader.framework;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public interface ImageReader {

    BufferedImage read(File file) throws Exception;

    static ImageReader getDefault() {
        return ImageIO::read;
    }
}

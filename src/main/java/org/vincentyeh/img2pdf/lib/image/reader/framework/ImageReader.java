package org.vincentyeh.img2pdf.lib.image.reader.framework;


import java.awt.image.BufferedImage;
import java.io.File;

public interface ImageReader {

    BufferedImage read(File file) throws ImageReadException;

}

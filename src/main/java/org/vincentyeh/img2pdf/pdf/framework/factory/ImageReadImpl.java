package org.vincentyeh.img2pdf.pdf.framework.factory;

import java.awt.image.BufferedImage;
import java.io.File;

public interface ImageReadImpl {
    BufferedImage readImage(File file);
}

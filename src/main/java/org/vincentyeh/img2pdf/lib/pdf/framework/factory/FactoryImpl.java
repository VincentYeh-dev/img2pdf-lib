package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import java.awt.image.BufferedImage;
import java.io.File;

public interface FactoryImpl {
    BufferedImage readImage(File file);
}

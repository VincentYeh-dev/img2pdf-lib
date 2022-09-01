package org.vincentyeh.img2pdf.lib.image.reader.concrete;

import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageFileChainReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DefaultImageChainReader extends ImageFileChainReader {
    @Override
    protected BufferedImage handleImpl(File file) throws Exception {
        return ImageIO.read(file);
    }

    @Override
    protected boolean canHandle(File file) {
        return file.exists();
    }
}

package org.vincentyeh.IMG2PDF.pdf.page.core;

import java.awt.image.BufferedImage;

public class ProxyImage {
    private final BufferedImage image;
    private final Size size;

    public ProxyImage(BufferedImage image) {
        this(image, new Size(image.getHeight(), image.getWidth()));
    }

    public ProxyImage(BufferedImage image, Size size) {
        this.image = image;
        this.size = size;
    }


    public BufferedImage get() {
        return image;
    }

    public Size getSize() {
        return size;
    }
}

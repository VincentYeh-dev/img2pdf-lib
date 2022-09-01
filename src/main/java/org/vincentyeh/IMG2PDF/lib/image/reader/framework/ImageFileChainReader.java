package org.vincentyeh.img2pdf.lib.image.reader.framework;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

public abstract class ImageFileChainReader implements ImageReader {
    private ImageFileChainReader nextChain;

    public ImageFileChainReader setNextChain(ImageFileChainReader nextChain) {
        this.nextChain = nextChain;
        return this;
    }

    protected abstract BufferedImage handleImpl(File file) throws Exception;

    protected abstract boolean canHandle(File file);

    public final BufferedImage handle(File file) throws Exception {
        if (canHandle(file))
            return handleImpl(file);
        else if (nextChain != null)
            return nextChain.handle(file);
        else
            throw new RuntimeException("Can't handle");
    }


    @Override
    public final BufferedImage read(File file) throws ImageReadException {
        try {
            return handle(file);
        } catch (Exception e) {
            throw new ImageReadException(e);
        }
    }

    public static BufferedImage rotateImage(BufferedImage img, double degrees) {
        if (degrees == 0 || degrees == 360)
            return img;
        double rads = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();

        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, img.getType());
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2., (newHeight - h) / 2.);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        g2d.dispose();
        return rotated;
    }
}

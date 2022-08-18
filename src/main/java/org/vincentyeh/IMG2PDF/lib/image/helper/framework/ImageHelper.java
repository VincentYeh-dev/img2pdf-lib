package org.vincentyeh.IMG2PDF.lib.image.helper.framework;

import org.vincentyeh.IMG2PDF.lib.util.file.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

public abstract class ImageHelper {
    private final ImageHelper imageHelper;

    protected abstract BufferedImage read(File file, BufferedImage image) throws Exception;

    public ImageHelper() {
        this.imageHelper = null;
    }

    public ImageHelper(ImageHelper imageHelper) {
        this.imageHelper = imageHelper;
    }

    public final BufferedImage read(File file) throws Exception {
        FileUtils.checkExists(file);
        if (imageHelper != null)
            return read(file, imageHelper.read(file));
        else
            return read(file, ImageIO.read(file));
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

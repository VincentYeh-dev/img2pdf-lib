package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import org.vincentyeh.img2pdf.lib.annotation.NotNull;

import java.awt.image.BufferedImage;

public interface IPage {
    void drawImage(@NotNull BufferedImage image, @NotNull PointF imagePosition, @NotNull SizeF imageSize) throws RuntimeException;
    int getPageNumber();
    void render(IDocument document);
}

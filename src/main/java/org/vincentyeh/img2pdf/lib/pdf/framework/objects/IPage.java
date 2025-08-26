package org.vincentyeh.img2pdf.lib.pdf.framework.objects;

import com.drew.lang.annotations.NotNull;

import java.awt.image.BufferedImage;

public interface IPage {
    void drawImage(@NotNull BufferedImage image, @NotNull PointF imagePosition, @NotNull SizeF imageSize) throws RuntimeException;
    int getPageNumber();
}

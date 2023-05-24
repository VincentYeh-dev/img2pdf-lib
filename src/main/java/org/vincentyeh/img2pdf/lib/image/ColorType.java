package org.vincentyeh.img2pdf.lib.image;

import java.awt.color.ColorSpace;

public enum ColorType {
    sRGB(ColorSpace.CS_sRGB), GRAY(ColorSpace.CS_GRAY);

    private final int colorSpace;

    ColorType(int colorSpace) {
        this.colorSpace = colorSpace;
    }

    public int getColorSpace() {
        return colorSpace;
    }
}

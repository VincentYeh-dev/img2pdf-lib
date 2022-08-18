package org.vincentyeh.IMG2PDF.lib.pdf.parameter;

import java.awt.color.ColorSpace;

public enum PDFImageColor {
    color(ColorSpace.CS_sRGB),gray(ColorSpace.CS_GRAY);

    private final int colorSpace;

    PDFImageColor(int colorSpace) {
        this.colorSpace = colorSpace;
    }

    public ColorSpace getColorSpace() {
        return ColorSpace.getInstance(colorSpace);
    }
}

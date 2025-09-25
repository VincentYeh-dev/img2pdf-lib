package org.vincentyeh.img2pdf.lib.pdf.parameter;


import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

/**
 * Direction of page.Horizontal or Vertical.
 *
 * @author VincentYeh
 */
public enum PageDirection {
    Landscape, Portrait;

    public static PageDirection detectDirection(SizeF size) {
        return detectDirection(size.width, size.height);
    }

    public static PageDirection detectDirection(float width, float height) {
        return height / width > 1 ? Portrait : Landscape;
    }
}

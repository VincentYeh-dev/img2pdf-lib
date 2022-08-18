package org.vincentyeh.img2pdf.lib.pdf.framework.calculation.strategy;

import org.vincentyeh.img2pdf.lib.pdf.framework.calculation.Position;
import org.vincentyeh.img2pdf.lib.pdf.framework.calculation.Size;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

public interface ImagePageCalculateStrategy {

    Position getImagePosition();

    Size getPageSize();

    Size getImageSize();

    void execute(PageArgument pageArgument, Size imageSize);
}

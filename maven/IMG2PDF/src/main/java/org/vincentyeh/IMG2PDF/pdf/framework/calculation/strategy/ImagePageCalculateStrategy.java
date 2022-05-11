package org.vincentyeh.IMG2PDF.pdf.framework.calculation.strategy;

import org.vincentyeh.IMG2PDF.pdf.framework.calculation.Position;
import org.vincentyeh.IMG2PDF.pdf.framework.calculation.Size;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageArgument;

public interface ImagePageCalculateStrategy {

    Position getImagePosition();

    Size getPageSize();

    Size getImageSize();

    void execute(PageArgument pageArgument, Size imageSize);
}

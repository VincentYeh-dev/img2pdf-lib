package org.vincentyeh.img2pdf.pdf.framework.factory;

import org.vincentyeh.img2pdf.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.pdf.parameter.PageArgument;

public interface ImageScalingStrategy {

    PointF getImagePosition();

    SizeF getPageSize();

    SizeF getImageSize();

    void execute(PageArgument pageArgument, SizeF imageSize);
}

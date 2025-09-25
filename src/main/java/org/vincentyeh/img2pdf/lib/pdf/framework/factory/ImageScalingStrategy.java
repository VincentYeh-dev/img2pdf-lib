package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

public interface ImageScalingStrategy {
    ImageScalingResult execute(PageArgument pageArgument, SizeF imageSize);
}

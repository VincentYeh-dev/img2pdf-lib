package org.vincentyeh.img2pdf.lib.pdf.framework.objects;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface PdfPage<PAGE> {
    PAGE get();

    void setSize(SizeF size);

    SizeF getSize();

    void putImage(BufferedImage image, PointF position, SizeF size) throws IOException;
}

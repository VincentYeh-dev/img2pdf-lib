package org.vincentyeh.IMG2PDF.pdf.page.core;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

class ProxyPage  {
    private final PDPage page;
    private final Size size;

    public ProxyPage(PDPage page, Size size) {
        this.page = page;
        page.setMediaBox(new PDRectangle(size.getWidth(),size.getHeight()));
        this.size = size;
    }

    public PDPage get() {
        return page;
    }

    public Size getSize() {
        return size;
    }
}

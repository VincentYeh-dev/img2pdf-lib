package org.vincentyeh.IMG2PDF.pdf.page.core;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

class ProxyPage implements Resizable<PDPage> {
    private final PDPage page;
    private final Size size;

    public ProxyPage(PDPage page, Size size) {
        this.page = page;
        page.setMediaBox(new PDRectangle(size.getWidth(),size.getHeight()));
        this.size = size;
    }

    @Override
    public PDPage get() {
        return page;
    }

    @Override
    public Size getSize() {
        return size;
    }
}

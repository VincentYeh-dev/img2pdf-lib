package org.vincentyeh.IMG2PDF.task;


import org.vincentyeh.IMG2PDF.pdf.parameter.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageSize;

public class PageArgument {
    public static class Builder {
        private PageAlign align;
        private PageSize size;
        private PageDirection direction;
        private boolean auto_rotate;

        public Builder setAlign(PageAlign align) {
            this.align = align;
            return this;
        }

        public Builder setSize(PageSize size) {
            this.size = size;
            return this;
        }

        public Builder setDirection(PageDirection direction) {
            this.direction = direction;
            return this;
        }

        public Builder setAutoRotate(boolean auto_rotate) {
            this.auto_rotate = auto_rotate;
            return this;
        }

        public PageArgument build() {
            return new PageArgument(align, size, direction, auto_rotate);
        }
    }

    private final PageAlign align;
    private final PageSize size;
    private final PageDirection direction;
    private final boolean auto_rotate;

    private PageArgument(PageAlign align, PageSize size, PageDirection direction, boolean auto_rotate) {
        this.align = align;
        this.size = size;
        this.direction = direction;
        this.auto_rotate = auto_rotate;
    }

    public PageAlign getAlign() {
        return align;
    }

    public PageDirection getDirection() {
        return direction;
    }

    public PageSize getSize() {
        return size;
    }

    public boolean getAutoRotate() {
        return auto_rotate;
    }

}

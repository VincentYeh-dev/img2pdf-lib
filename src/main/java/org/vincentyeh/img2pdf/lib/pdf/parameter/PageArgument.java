package org.vincentyeh.img2pdf.lib.pdf.parameter;


public class PageArgument {
    public PageAlign align;
    public PageSize size;
    public PageDirection direction;
    public boolean autoRotate;

    public PageArgument(PageAlign align, PageSize size, PageDirection direction, boolean autoRotate) {
        this.align = align;
        this.size = size;
        this.direction = direction;
        this.autoRotate = autoRotate;
    }

    public PageArgument(PageAlign.VerticalAlign verticalAlign,
                        PageAlign.HorizontalAlign horizontalAlign,
                        PageSize size, PageDirection direction,
                        boolean autoRotate) {
        this(new PageAlign(verticalAlign, horizontalAlign), size, direction, autoRotate);
    }

    public PageArgument(PageAlign.VerticalAlign verticalAlign,
                        PageAlign.HorizontalAlign horizontalAlign,
                        PageSize size) {
        this(new PageAlign(verticalAlign, horizontalAlign), size, PageDirection.Portrait, true);
    }

    public PageArgument(PageAlign.VerticalAlign verticalAlign,
                        PageAlign.HorizontalAlign horizontalAlign,
                        PageSize size, PageDirection direction) {
        this(verticalAlign, horizontalAlign, size, direction, false);
    }

    public PageArgument(PageSize size) {
        this(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER, size);
    }

    public PageArgument() {
        this(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER, PageSize.DEPEND_ON_IMG);
    }

    public PageAlign getAlign() {
        return align;
    }
    public PageSize getSize() {
        return size;
    }

    public PageDirection getDirection() {
        return direction;
    }

    public boolean isAutoRotate() {
        return autoRotate;
    }

    public void setAlign(PageAlign align) {
        this.align = align;
    }

    public void setSize(PageSize size) {
        this.size = size;
    }

    public void setDirection(PageDirection direction) {
        this.direction = direction;
    }

    public void setAutoRotate(boolean autoRotate) {
        this.autoRotate = autoRotate;
    }

}

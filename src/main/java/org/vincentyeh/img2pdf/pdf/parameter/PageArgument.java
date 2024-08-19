package org.vincentyeh.img2pdf.pdf.parameter;


public class PageArgument {
    public final PageAlign align;
    public final PageSize size;
    public final PageDirection direction;
    public final boolean autoRotate;

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
}

package org.vincentyeh.img2pdf.lib.pdf.parameter;


public record PageArgument(
        PageAlign align,
        PageSize size,
        PageDirection direction,
        boolean autoRotate
) {
    public PageArgument(PageAlign.VerticalAlign verticalAlign, PageAlign.HorizontalAlign horizontalAlign, PageSize size, PageDirection direction, boolean autoRotate) {
        this(new PageAlign(verticalAlign, horizontalAlign), size, direction, autoRotate);
    }

    public PageArgument(PageAlign.VerticalAlign verticalAlign, PageAlign.HorizontalAlign horizontalAlign, PageSize size) {
        this(verticalAlign, horizontalAlign, size, PageDirection.Portrait, true);
    }

    public PageArgument(PageAlign.VerticalAlign verticalAlign, PageAlign.HorizontalAlign horizontalAlign) {
        this(verticalAlign, horizontalAlign, PageSize.DEPEND_ON_IMG);
    }

    public PageArgument(PageSize size) {
        this(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER, size);
    }

    public PageArgument() {
        this(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER);
    }

}

package org.vincentyeh.IMG2PDF.lib.pdf.parameter;


public final class PageArgument {
    private final PageAlign align;
    private final PageSize size;
    private final PageDirection direction;
    private final boolean autoRotate;

    public PageArgument(PageAlign.VerticalAlign verticalAlign, PageAlign.HorizontalAlign horizontalAlign, PageSize size, PageDirection direction, boolean autoRotate){
        this.align = new PageAlign(verticalAlign,horizontalAlign);
        this.size = size;
        this.direction = direction;
        this.autoRotate = autoRotate;
    }
    public PageArgument(PageAlign.VerticalAlign verticalAlign, PageAlign.HorizontalAlign horizontalAlign,PageSize size){
        this(verticalAlign,horizontalAlign,size,PageDirection.Portrait,true);
    }
    public PageArgument(PageAlign.VerticalAlign verticalAlign, PageAlign.HorizontalAlign horizontalAlign){
        this(verticalAlign,horizontalAlign,PageSize.DEPEND_ON_IMG);
    }
    public PageArgument(PageSize size){
        this(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER,size);
    }
    public PageArgument(){
        this(PageAlign.VerticalAlign.CENTER, PageAlign.HorizontalAlign.CENTER);
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

    public boolean autoRotate() {
        return autoRotate;
    }

}

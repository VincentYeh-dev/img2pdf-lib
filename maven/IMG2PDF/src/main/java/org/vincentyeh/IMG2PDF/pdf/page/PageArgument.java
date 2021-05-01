package org.vincentyeh.IMG2PDF.pdf.page;

import org.jdom2.Element;


public class PageArgument {
    private PageAlign align=new PageAlign("CENTER-CENTER");
    private PageSize size=PageSize.A4;
    private PageDirection direction=PageDirection.Portrait;
    private boolean auto_rotate=true;

    public PageArgument(){

    }

    public PageArgument(Element element){
        this.align = new PageAlign(element.getChild("align").getValue());
        this.size = PageSize.valueOf(element.getChild("size").getValue());
        this.direction = PageDirection.valueOf(element.getChild("default-direction").getValue());
        this.auto_rotate = Boolean.parseBoolean(element.getChild("auto-rotate").getValue());
    }

    public Element toElement(){
        Element element = new Element("PageArgument");
        element.addContent(new Element("size").addContent(size.toString()));
        element.addContent(new Element("align").addContent(align.toString()));
        element.addContent(new Element("default-direction").addContent(direction.toString()));
        element.addContent(new Element("auto-rotate").addContent(Boolean.toString(auto_rotate)));
        return element;

    }

    public void setAlign(PageAlign align) {
        this.align = align;
    }

    public void setAutoRotate(boolean auto_rotate) {
        this.auto_rotate = auto_rotate;
    }

    public void setDirection(PageDirection direction) {
        this.direction = direction;
    }

    public void setSize(PageSize size) {
        this.size = size;
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

    public boolean getAutoRotate(){
        return auto_rotate;
    }

}

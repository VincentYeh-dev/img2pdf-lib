package org.vincentyeh.IMG2PDF.pdf.page;

import org.jdom2.Element;


public class PageArgument {
    private PageAlign pdf_align=new PageAlign("CENTER-CENTER");
    private PageSize pdf_size=PageSize.A4;
    private PageDirection pdf_direction=PageDirection.Vertical;
    private boolean pdf_auto_rotate=true;

    public PageArgument(){

    }

    public PageArgument(Element element){
        this.pdf_align = new PageAlign(element.getChild("align").getValue());
        this.pdf_size = PageSize.getByString(element.getChild("size").getValue());
        this.pdf_direction = PageDirection.getByString(element.getChild("default-direction").getValue());
        this.pdf_auto_rotate = Boolean.parseBoolean(element.getChild("auto-rotate").getValue());
    }

    public Element toElement(){
        Element element = new Element("PageArgument");
        element.addContent(new Element("size").addContent(pdf_size.toString()));
        element.addContent(new Element("align").addContent(pdf_align.toString()));
        element.addContent(new Element("default-direction").addContent(pdf_direction.toString()));
        element.addContent(new Element("auto-rotate").addContent(Boolean.toString(pdf_auto_rotate)));
//		task.setAttribute("size", pdf_size.toString() + "");
//		task.setAttribute("align", pdf_align.toString());
//		task.setAttribute("default-direction", pdf_direction.toString());
//		task.setAttribute("auto-rotate", String.valueOf(pdf_auto_rotate));
        return element;

    }

    public void setPdf_align(PageAlign pdf_align) {
        this.pdf_align = pdf_align;
    }

    public void setPdf_auto_rotate(boolean pdf_auto_rotate) {
        this.pdf_auto_rotate = pdf_auto_rotate;
    }

    public void setPdf_direction(PageDirection pdf_direction) {
        this.pdf_direction = pdf_direction;
    }

    public void setPdf_size(PageSize pdf_size) {
        this.pdf_size = pdf_size;
    }

    public PageAlign getPdf_align() {
        return pdf_align;
    }

    public PageDirection getPdf_direction() {
        return pdf_direction;
    }

    public PageSize getPdf_size() {
        return pdf_size;
    }

    public boolean getAutoRotate(){
        return pdf_auto_rotate;
    }

}

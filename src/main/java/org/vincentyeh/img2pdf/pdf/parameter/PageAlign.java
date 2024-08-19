package org.vincentyeh.img2pdf.pdf.parameter;


/**
 * The class which define Alignment of page of PDF
 *
 * @author vincent
 */
public class PageAlign {
    public final VerticalAlign vertical_align;
    public final HorizontalAlign horizontal_align;

    public PageAlign(VerticalAlign vertical_align, HorizontalAlign horizontal_align) {
        this.vertical_align = vertical_align;
        this.horizontal_align = horizontal_align;
    }

    public static PageAlign valueOf(String value) {
        String[] verti_hori_align = value.split("-");
        return new PageAlign(VerticalAlign.valueOf(verti_hori_align[0]), HorizontalAlign.valueOf(verti_hori_align[1]));
    }

    @Override
    public String toString() {
        return String.format("%s-%s", vertical_align, horizontal_align);
    }

    public enum HorizontalAlign {
        RIGHT, LEFT, CENTER

    }

    public enum VerticalAlign {
        TOP, BOTTOM, CENTER
    }

}
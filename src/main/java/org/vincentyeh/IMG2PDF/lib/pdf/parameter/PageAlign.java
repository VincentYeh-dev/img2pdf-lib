package org.vincentyeh.img2pdf.lib.pdf.parameter;


/**
 * The class which define Alignment of page of PDF
 *
 * @author vincent
 */
public record PageAlign(
        VerticalAlign vertical_align,
        HorizontalAlign horizontal_align
) {


    public static PageAlign valueOf(String value) {
        var verti_hori_align = value.split("-");
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
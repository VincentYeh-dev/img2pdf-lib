package org.vincentyeh.img2pdf.lib.pdf.framework.objects;


public record PointF(float x, float y) {

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointF position = (PointF) o;
        return Float.compare(position.x, x) == 0 && Float.compare(position.y, y) == 0;
    }
}

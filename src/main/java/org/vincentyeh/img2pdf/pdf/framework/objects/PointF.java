package org.vincentyeh.img2pdf.pdf.framework.objects;


public class PointF implements Comparable<PointF> {

    public final float x;
    public final float y;

    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointF() {
        this(0, 0);
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointF position = (PointF) o;
        return compareTo(position) == 0;
    }


    @Override
    public int compareTo(PointF o) {
        return Double.compare(hypotenuse(), o.hypotenuse());
    }

    public float hypotenuse() {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}

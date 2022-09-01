package org.vincentyeh.img2pdf.lib.pdf.framework.objects;

import java.util.Objects;

public record SizeF(float width, float height) implements Comparable<SizeF> {
    public SizeF {
        if (height < 0)
            throw new IllegalArgumentException("height<0");
        if (width < 0)
            throw new IllegalArgumentException("width<0");
    }

    public SizeF() {
        this(0f, 0f);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SizeF size = (SizeF) o;
        return compareTo(size)==0;
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", width, height);
    }

    @Override
    public int compareTo(SizeF o) {
        try{
            Objects.requireNonNull(o);
        }catch (NullPointerException e){
            throw new IllegalArgumentException(e);
        }
        return Float.compare(getArea(), o.getArea());
    }

    public float getArea() {
        return height * width;
    }
}

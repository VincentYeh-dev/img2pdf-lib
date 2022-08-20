package org.vincentyeh.img2pdf.lib.pdf.framework.objects;

public record SizeF(float width, float height) {
    public SizeF {
        if (height < 0)
            throw new IllegalArgumentException("height<0");
        if (width < 0)
            throw new IllegalArgumentException("width<0");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SizeF size = (SizeF) o;
        return Float.compare(size.height, height) == 0 && Float.compare(size.width, width) == 0;
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", width, height);
    }
}

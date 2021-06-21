package org.vincentyeh.IMG2PDF.pdf.converter.core;

class Size {
    private final float height;
    private final float width;

    public Size(float height,float width){
        this.height=height;
        this.width=width;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

}

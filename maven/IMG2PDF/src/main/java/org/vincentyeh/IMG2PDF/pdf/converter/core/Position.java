package org.vincentyeh.IMG2PDF.pdf.converter.core;

class Position {
    private final float x;
    private final float y;

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", x, y);
    }


}

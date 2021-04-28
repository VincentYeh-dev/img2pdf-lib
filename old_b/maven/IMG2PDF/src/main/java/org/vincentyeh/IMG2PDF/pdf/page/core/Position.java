package org.vincentyeh.IMG2PDF.pdf.page.core;

public class Position {
    private final float x;
    private final float y;

    public Position() {
        this.x = 0;
        this.y = 0;
    }
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

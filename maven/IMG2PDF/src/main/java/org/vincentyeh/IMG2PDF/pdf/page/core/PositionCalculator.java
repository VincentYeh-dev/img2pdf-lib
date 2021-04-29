package org.vincentyeh.IMG2PDF.pdf.page.core;

import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;

public class PositionCalculator {
    private static PositionCalculator calculator = null;

    public static PositionCalculator getInstance() {
        if (calculator == null) {
            calculator = new PositionCalculator();
        }
        return calculator;
    }

    public static void init(boolean rotated, float img_height, float img_width, float page_height,
                            float page_width) {
//        calculator=getInstance();

        calculator.rotated = rotated;
        if (rotated) {
            calculator.x_space = page_height - img_height;
            calculator.y_space = page_width - img_width;
        } else {
            calculator.x_space = page_width - img_width;
            calculator.y_space = page_height - img_height;
        }

    }

    public Position calculate(PageAlign align) {
        if (rotated)
            return rotated_position_calculate(align);
        else
            return non_rotate_position_calculate(align);
    }


    private Position non_rotate_position_calculate(PageAlign align) {

        float position_x = 0, position_y = 0;

//        水平 X
        PageAlign.HorizontalAlign hori_align = align.getHorizontal();

//        垂直 Y
        PageAlign.VerticalAlign verti_align = align.getVertical();

        switch (hori_align) {
            case LEFT:
                position_x = 0;
                break;
            case RIGHT:
                position_x = x_space;
                break;
            case CENTER:
                position_x = x_space / 2;
                break;
            default:
                break;
        }

        switch (verti_align) {
            case BOTTOM:
                position_y = 0;
                break;
            case TOP:
                position_y = y_space;
                break;
            case CENTER:
                position_y = y_space / 2;
                break;
            default:
                break;

        }

        return new Position(position_x, position_y);
    }

    private Position rotated_position_calculate(PageAlign align) {

        Position rotated_position = non_rotate_position_calculate(align);

        return new Position(y_space - rotated_position.getY(), rotated_position.getX() + 0);

    }

    public Position toRotatedPosition(Position raw) {
        return new Position(y_space - raw.getY(), raw.getX() + 0);
    }

    private float x_space;
    private float y_space;
    private boolean rotated;

}

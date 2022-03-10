package org.vincentyeh.IMG2PDF.pdf.concrete.calculation.strategy;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageArgument;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageSize;
import org.vincentyeh.IMG2PDF.pdf.framework.calculation.Position;
import org.vincentyeh.IMG2PDF.pdf.framework.calculation.Size;
import org.vincentyeh.IMG2PDF.pdf.framework.calculation.strategy.ImagePageCalculateStrategy;

import java.awt.image.BufferedImage;

import static org.vincentyeh.IMG2PDF.pdf.parameter.PageDirection.Landscape;
import static org.vincentyeh.IMG2PDF.pdf.parameter.PageDirection.Portrait;

public final class StandardImagePageCalculationStrategy implements ImagePageCalculateStrategy {
    private Size pageSize;
    private Size imageSize;
    private Position imagePosition;

    @Override
    public Position getImagePosition() {
        return imagePosition;
    }

    @Override
    public Size getPageSize() {
        return pageSize;
    }

    @Override
    public Size getImageSize() {
        return imageSize;
    }

    @Override
    public void execute(PageArgument argument, BufferedImage bufferedImage) {
        PageDirection direction = getSuitableDirection(argument, bufferedImage);
        pageSize = getSuitablePageSize(direction, argument.getSize(), bufferedImage);
        imageSize = getMaxScaleImageSize(bufferedImage, pageSize);
        imagePosition = calculateImagePosition(getMaxScaleImageSize(bufferedImage, pageSize), pageSize, argument.getAlign());
    }

    private Size getMaxScaleImageSize(BufferedImage rawImage, Size page_size) {
        return scaleUpToMax(new Size(rawImage.getWidth(), rawImage.getHeight()), page_size);
    }

    private Position calculateImagePosition(Size img_size, Size page_size, PageAlign align) {
        return calculatePosition(align, img_size, page_size);
    }

    private PageDirection getSuitableDirection(PageArgument argument, BufferedImage image) {
        if (argument.getSize() == PageSize.DEPEND_ON_IMG) {
            return Portrait;
        }
        if (argument.autoRotate()) {
            return detectDirection(image.getHeight(), image.getWidth());
        } else {
            return argument.getDirection();
        }
    }

    private Size getSuitablePageSize(PageDirection direction, PageSize pageSize, BufferedImage image) {
        if (pageSize == PageSize.DEPEND_ON_IMG) {
            return getImageSize(image, false);
        } else {
            PDRectangle rect = pageSize.getPdrectangle();
            return getPageSize(rect, direction == Landscape);
        }
    }

    private Size getImageSize(BufferedImage image, boolean reverse) {
        return new Size(reverse ? image.getHeight() : image.getWidth(), reverse ? image.getWidth() : image.getHeight());
    }

    private Size getPageSize(PDRectangle rectangle, boolean reverse) {
        return new Size(reverse ? rectangle.getHeight() : rectangle.getWidth(), reverse ? rectangle.getWidth() : rectangle.getHeight());
    }

    private PageDirection detectDirection(float height, float width) {
        return PageDirection.detectDirection(height, width);
    }

    private Size scaleUpToMax(Size img_size, Size page_size) {
        if (img_size == null)
            throw new IllegalArgumentException("img_size==null");
        if (page_size == null)
            throw new IllegalArgumentException("page_size==null");

        float img_height = img_size.getHeight();
        float img_width = img_size.getWidth();
        float page_height = page_size.getHeight();
        float page_width = page_size.getWidth();

        float out_width = (img_width / img_height) * page_height;
        float out_height = (img_height / img_width) * page_width;
        if (out_height <= page_height) {
//			width fill
            out_width = page_width;
//			out_height = (img_height / img_width) * page_width;
        } else if (out_width <= page_width) {
//			height fill
            out_height = page_height;
//			out_width = (img_width / img_height) * page_height;
        }

        return new Size(out_width, out_height);
    }

    private Position calculatePosition(PageAlign align, Size object_size, Size container_size) {
        if (object_size == null)
            throw new IllegalArgumentException("object_size==null");
        if (container_size == null)
            throw new IllegalArgumentException("container_size==null");

        return calculatePosition(align, object_size.getHeight(), object_size.getWidth(), container_size.getHeight(), container_size.getWidth());
    }

    private Position calculatePosition(PageAlign align, float object_height, float object_width, float container_height,
                                       float container_width) {
        final float x_space;
        final float y_space;
        if (object_height < 0)
            throw new IllegalArgumentException("object_height<0");
        if (object_width < 0)
            throw new IllegalArgumentException("object_width<0");

        if (container_height < 0)
            throw new IllegalArgumentException("container_height<0");
        if (container_width < 0)
            throw new IllegalArgumentException("container_width<0");

        x_space = container_width - object_width;
        if (x_space < 0)
            throw new IllegalArgumentException("The space of x axis is negative.");
        y_space = container_height - object_height;
        if (y_space < 0)
            throw new IllegalArgumentException("The space of y axis is negative.");

        float position_x = 0f, position_y = 0f;
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
}

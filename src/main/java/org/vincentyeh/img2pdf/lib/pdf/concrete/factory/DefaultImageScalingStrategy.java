package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageAlign;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageDirection;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;

import static org.vincentyeh.img2pdf.lib.pdf.parameter.PageDirection.Landscape;
import static org.vincentyeh.img2pdf.lib.pdf.parameter.PageDirection.Portrait;


public final class DefaultImageScalingStrategy implements ImageScalingStrategy {
    private SizeF pageSize;
    private SizeF newImageSize;
    private PointF imagePosition;

    public PointF getImagePosition() {
        return imagePosition;
    }

    public SizeF getPageSize() {
        return pageSize;
    }

    public SizeF getImageSize() {
        return newImageSize;
    }

    public void execute(PageArgument argument, SizeF imageSize) {
        pageSize = getSuitablePageSize(argument.direction, argument.size, imageSize, argument.autoRotate);
        newImageSize = scaleUpToMax(imageSize, pageSize);
        imagePosition = calculatePosition(argument.align, newImageSize, pageSize);
    }

    private SizeF getSuitablePageSize(PageDirection default_direction, PageSize pageSize, SizeF imageSize, boolean autoRotate) {

        final PageDirection direction;
        if (pageSize == PageSize.DEPEND_ON_IMG) {
            return imageSize;
        } else if (autoRotate) {
            direction = PageDirection.detectDirection(imageSize);
        } else {
            direction = default_direction;
        }
        SizeF size = pageSize.getSizeInPixels();
        if (direction == Portrait) {
            return new SizeF(size.width, size.height);
        } else if (direction == Landscape) {
            return new SizeF(size.height, size.width);
        } else throw new IllegalArgumentException();
    }

    private SizeF scaleUpToMax(SizeF img_size, SizeF page_size) {
        if (img_size == null) throw new IllegalArgumentException("img_size==null");
        if (page_size == null) throw new IllegalArgumentException("page_size==null");

        float scaled_width = (img_size.width / img_size.height) * page_size.height;
        float scaled_height = (img_size.height / img_size.width) * page_size.width;

//			width fill
        if (scaled_height <= page_size.height)
            return new SizeF(page_size.width, scaled_height);
//			height fill
        if (scaled_width <= page_size.width)
            return new SizeF(scaled_width, page_size.height);

        throw new IllegalStateException();
    }

    private PointF calculatePosition(PageAlign align, SizeF object_size, SizeF container_size) {
        if (object_size == null) throw new IllegalArgumentException("object_size==null");
        if (container_size == null) throw new IllegalArgumentException("container_size==null");


        float x_space = container_size.width - object_size.width;
        if (x_space < 0) throw new IllegalArgumentException("The space of x axis is negative.");

        float y_space = container_size.height - object_size.height;
        if (y_space < 0) throw new IllegalArgumentException("The space of y axis is negative.");

//        水平 X
        PageAlign.HorizontalAlign hori_align = align.horizontal_align;

//        垂直 Y
        PageAlign.VerticalAlign verti_align = align.vertical_align;

        float position_x = 0f, position_y = 0f;
        switch (hori_align) {
            case LEFT :
                position_x = 0;
                break;
            case RIGHT:
                position_x = x_space;
                break;
            case CENTER :
                position_x = x_space / 2;
        }

        switch (verti_align) {
            case BOTTOM :
                position_y = 0;
                break;
            case TOP :
                position_y = y_space;
                break;
            case CENTER :
                position_y = y_space / 2;
                break;
        }

        return new PointF(position_x, position_y);
    }
}

package org.vincentyeh.img2pdf.pdf.concrete.factory;

import org.vincentyeh.img2pdf.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.pdf.parameter.PageAlign;
import org.vincentyeh.img2pdf.pdf.parameter.PageArgument;
import org.vincentyeh.img2pdf.pdf.parameter.PageDirection;
import org.vincentyeh.img2pdf.pdf.parameter.PageSize;

import static org.vincentyeh.img2pdf.pdf.parameter.PageDirection.Landscape;
import static org.vincentyeh.img2pdf.pdf.parameter.PageDirection.Portrait;


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
        newImageSize = getImageMaxSize(imageSize, pageSize);
        imagePosition = calculateImagePosition(argument.align, newImageSize, pageSize);
    }

    private SizeF getSuitablePageSize(PageDirection default_direction, PageSize pageSize, SizeF imageSize, boolean autoRotate) {

        final PageDirection direction;
        if (pageSize == PageSize.DEPEND_ON_IMG)
            return imageSize;

        if (autoRotate) {
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

    private SizeF getImageMaxSize(SizeF img_size, SizeF page_size) {
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

    private PointF calculateImagePosition(PageAlign align, SizeF objectSize, SizeF pageSize) {
        if (objectSize == null) throw new IllegalArgumentException("objectSize==null");
        if (pageSize == null) throw new IllegalArgumentException("pageSize==null");
        float positionX = calculatePositionX(align.horizontal_align, objectSize, pageSize);
        float positionY = calculatePositionY(align.vertical_align, objectSize, pageSize);

        return new PointF(positionX, positionY);
    }

    private float calculatePositionX(PageAlign.HorizontalAlign horizontalAlign, SizeF objectSize, SizeF pageSize) {
        float xSpace = pageSize.width - objectSize.width;
        if (xSpace < 0) throw new IllegalArgumentException("The space of x axis is negative.");

        switch (horizontalAlign) {
            case LEFT:
                return 0;
            case RIGHT:
                return xSpace;
            default:
                return xSpace / 2;
        }
    }

    private float calculatePositionY(PageAlign.VerticalAlign verticalAlign, SizeF objectSize, SizeF pageSize) {
        float ySpace = pageSize.height - objectSize.height;
        if (ySpace < 0) throw new IllegalArgumentException("The space of y axis is negative.");
        switch (verticalAlign) {
            case BOTTOM:
                return 0;
            case TOP:
                return ySpace;
            default:
                return ySpace / 2;
        }
    }
}

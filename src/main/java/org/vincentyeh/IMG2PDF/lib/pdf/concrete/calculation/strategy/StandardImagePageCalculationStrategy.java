package org.vincentyeh.img2pdf.lib.pdf.concrete.calculation.strategy;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.vincentyeh.img2pdf.lib.pdf.framework.calculation.strategy.ImagePageCalculateStrategy;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageAlign;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageDirection;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;

import static org.vincentyeh.img2pdf.lib.pdf.parameter.PageDirection.Landscape;
import static org.vincentyeh.img2pdf.lib.pdf.parameter.PageDirection.Portrait;


public final class StandardImagePageCalculationStrategy implements ImagePageCalculateStrategy {
    private SizeF pageSize;
    private SizeF newImageSize;
    private PointF imagePosition;

    @Override
    public PointF getImagePosition() {
        return imagePosition;
    }

    @Override
    public SizeF getPageSize() {
        return pageSize;
    }

    @Override
    public SizeF getImageSize() {
        return newImageSize;
    }

    @Override
    public void execute(PageArgument argument, SizeF imageSize) {
        PageDirection direction = getSuitableDirection(argument, imageSize);
        pageSize = getSuitablePageSize(direction, argument.size(), imageSize);
        newImageSize = getMaxScaleImageSize(imageSize, pageSize);
        imagePosition = calculateImagePosition(newImageSize, pageSize, argument.align());
    }

    private SizeF getMaxScaleImageSize(SizeF imageSize, SizeF page_size) {
        return scaleUpToMax(imageSize, page_size);
    }

    private PointF calculateImagePosition(SizeF img_size, SizeF page_size, PageAlign align) {
        return calculatePosition(align, img_size, page_size);
    }

    private PageDirection getSuitableDirection(PageArgument argument, SizeF size) {
        if (argument.size() == PageSize.DEPEND_ON_IMG) {
            return Portrait;
        }
        if (argument.autoRotate()) {
            return detectDirection(size.height(), size.width());
        } else {
            return argument.direction();
        }
    }

    private SizeF getSuitablePageSize(PageDirection direction, PageSize pageSize, SizeF imageSize) {
        if (pageSize == PageSize.DEPEND_ON_IMG) {
            return imageSize;
        } else {
            PDRectangle rect = pageSize.getPdrectangle();
            return getPageSize(rect, direction == Landscape);
        }
    }

    private SizeF getPageSize(PDRectangle rectangle, boolean reverse) {
        return new SizeF(reverse ? rectangle.getHeight() : rectangle.getWidth(), reverse ? rectangle.getWidth() : rectangle.getHeight());
    }

    private PageDirection detectDirection(float height, float width) {
        return PageDirection.detectDirection(height, width);
    }

    private SizeF scaleUpToMax(SizeF img_size, SizeF page_size) {
        if (img_size == null)
            throw new IllegalArgumentException("img_size==null");
        if (page_size == null)
            throw new IllegalArgumentException("page_size==null");

        float img_height = img_size.height();
        float img_width = img_size.width();
        float page_height = page_size.height();
        float page_width = page_size.width();

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

        return new SizeF(out_width, out_height);
    }

    private PointF calculatePosition(PageAlign align, SizeF object_size, SizeF container_size) {
        if (object_size == null)
            throw new IllegalArgumentException("object_size==null");
        if (container_size == null)
            throw new IllegalArgumentException("container_size==null");

        return calculatePosition(align, object_size.height(), object_size.width(), container_size.height(), container_size.width());
    }

    private PointF calculatePosition(PageAlign align, float object_height, float object_width, float container_height,
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
        var hori_align = align.horizontal_align();

//        垂直 Y
        var verti_align = align.vertical_align();

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

        return new PointF(position_x, position_y);
    }
}

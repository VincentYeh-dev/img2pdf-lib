package org.vincentyeh.IMG2PDF.pdf.converter.core;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.parameter.PageSize;

import static org.vincentyeh.IMG2PDF.pdf.parameter.PageDirection.Landscape;
import static org.vincentyeh.IMG2PDF.pdf.parameter.PageDirection.Portrait;

/**
 * Page that contain image in PDF File.
 *
 * @author VincentYeh
 */
public class ImagePageFactory {

    public static class Builder{
        private PageAlign align;
        private PageSize size;
        private PageDirection direction;
        private boolean auto_rotate;

        public void setAlign(PageAlign align) {
            if(align==null)
                throw new IllegalArgumentException("align==null");
            this.align = align;
        }

        public void setSize(PageSize size) {
            if(size==null)
                throw new IllegalArgumentException("size==null");
            this.size = size;
        }

        public void setDirection(PageDirection direction) {
            if(direction==null)
                throw new IllegalArgumentException("direction==null");
            this.direction = direction;
        }

        public void setAutoRotate(boolean auto_rotate) {
            this.auto_rotate = auto_rotate;
        }

        public ImagePageFactory build(){
            return new ImagePageFactory(align,size,direction,auto_rotate);
        }
    }

    private final PageAlign align;
    private final PageSize size;
    private final PageDirection direction;
    private final boolean auto_rotate;

    private ImagePageFactory(PageAlign align, PageSize size, PageDirection direction, boolean auto_rotate) {
        this.align = align;
        this.size = size;
        this.direction = direction;
        this.auto_rotate = auto_rotate;
    }

    public PDPage getImagePage(PDDocument document, BufferedImage raw_image) throws Exception {
        if (document == null)
            throw new IllegalArgumentException("document==null");

        if (raw_image == null)
            throw new IllegalArgumentException("rawImage==null");

        PageDirection direction= getSuitableDirection(raw_image);
        Size new_page_size = getSuitablePageSize(direction,size, raw_image);
        Size new_image_size = getMaxScaleImageSize(raw_image, new_page_size);
        final Position new_position = calculateImagePosition(new_image_size, new_page_size,align);

        return createPDPage(document,raw_image,new_image_size,new_position,new_page_size);
    }


    private PDPage createPDPage(PDDocument document, BufferedImage image, Size image_size, Position position, Size page_size) throws IOException {
        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, image);

        PDPage pdPage = new PDPage(new PDRectangle(page_size.getWidth(), page_size.getHeight()));

        PDPageContentStream contentStream = new PDPageContentStream(document, pdPage);
        contentStream.drawImage(pdImageXObject, position.getX(), position.getY(), image_size.getWidth(), image_size.getHeight());
        contentStream.close();

        return pdPage;
    }

    private Size getMaxScaleImageSize(BufferedImage rawImage, Size page_size) {
        return SizeCalculator.getInstance().scaleUpToMax(new Size(rawImage.getHeight(), rawImage.getWidth()), page_size);
    }

    private Position calculateImagePosition(Size img_size, Size page_size, PageAlign align) {
        PositionCalculator positionCalculator = PositionCalculator.getInstance();
        PositionCalculator.init(img_size.getHeight(), img_size.getWidth(), page_size.getHeight(), page_size.getWidth());
        return positionCalculator.calculate(align);
    }

    private PageDirection getSuitableDirection(BufferedImage image) {
        if (size == PageSize.DEPEND_ON_IMG) {
            return Portrait;
        }
        if (auto_rotate) {
            return detectDirection(image.getHeight(),image.getWidth());
        } else {
            return direction;
        }
    }

    private Size getSuitablePageSize(PageDirection direction, PageSize pageSize, BufferedImage image) {
        if (pageSize == PageSize.DEPEND_ON_IMG) {
            return new Size(image.getHeight(), image.getWidth());
        } else {
            PDRectangle rect = pageSize.getPdrectangle();
            if (direction == Landscape)
                return new Size(rect.getWidth(), rect.getHeight());
            else
                return new Size(rect.getHeight(), rect.getWidth());
        }
    }

    private PageDirection detectDirection(float height,float width) {
        return PageDirection.detectDirection(height,width);
    }

}

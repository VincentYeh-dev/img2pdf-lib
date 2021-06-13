package org.vincentyeh.IMG2PDF.pdf.page.core;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.task.PageArgument;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;

import static org.vincentyeh.IMG2PDF.pdf.page.PageDirection.Landscape;
import static org.vincentyeh.IMG2PDF.pdf.page.PageDirection.Portrait;

/**
 * Page that contain image in PDF File.
 *
 * @author VincentYeh
 */
public class ImagePageFactory {
    public static PDPage getImagePage(PDDocument document, PageArgument argument, BufferedImage raw_image) throws Exception {
        if (document == null)
            throw new IllegalArgumentException("document==null");

        if (argument == null)
            throw new IllegalArgumentException("argument==null");

        if (raw_image == null)
            throw new IllegalArgumentException("rawImage==null");

        PageDirection direction= getSuitableDirection(raw_image,argument);
        Size new_page_size = getSuitablePageSize(direction, argument.getSize(), raw_image);
        Size new_image_size = getMaxScaleImageSize(raw_image, new_page_size);
        final Position new_position = calculateImagePosition(new_image_size, new_page_size, argument.getAlign());

        return createPDPage(document,raw_image,new_image_size,new_position,new_page_size);
    }


    private static PDPage createPDPage(PDDocument document, BufferedImage image, Size image_size, Position position, Size page_size) throws IOException {
        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, image);

        PDPage pdPage = new PDPage(new PDRectangle(page_size.getWidth(), page_size.getHeight()));

        PDPageContentStream contentStream = new PDPageContentStream(document, pdPage);
        contentStream.drawImage(pdImageXObject, position.getX(), position.getY(), image_size.getWidth(), image_size.getHeight());
        contentStream.close();

        return pdPage;
    }

    private static Size getMaxScaleImageSize(BufferedImage rawImage, Size page_size) {
        return SizeCalculator.getInstance().scaleUpToMax(new Size(rawImage.getHeight(), rawImage.getWidth()), page_size);
    }

    private static Position calculateImagePosition(Size img_size, Size page_size, PageAlign align) {
        PositionCalculator positionCalculator = PositionCalculator.getInstance();
        PositionCalculator.init(img_size.getHeight(), img_size.getWidth(), page_size.getHeight(), page_size.getWidth());
        return positionCalculator.calculate(align);
    }

    private static PageDirection getSuitableDirection(BufferedImage image,PageArgument argument) {
        if (argument.getSize() == PageSize.DEPEND_ON_IMG) {
            return Portrait;
        }
        if (argument.getAutoRotate()) {
            return detectDirection(image.getHeight(),image.getWidth());
        } else {
            return argument.getDirection();
        }
    }

    private static Size getSuitablePageSize(PageDirection direction, PageSize pageSize, BufferedImage image) {
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

    private static PageDirection detectDirection(float height,float width) {
        return PageDirection.detectDirection(height,width);
    }

}

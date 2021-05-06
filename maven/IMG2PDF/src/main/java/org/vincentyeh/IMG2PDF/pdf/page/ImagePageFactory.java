package org.vincentyeh.IMG2PDF.pdf.page;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.IMG2PDF.pdf.page.core.Position;
import org.vincentyeh.IMG2PDF.pdf.page.core.PositionCalculator;
import org.vincentyeh.IMG2PDF.pdf.page.core.Size;
import org.vincentyeh.IMG2PDF.pdf.page.core.SizeCalculator;

import static org.vincentyeh.IMG2PDF.pdf.page.PageDirection.Landscape;
import static org.vincentyeh.IMG2PDF.pdf.page.PageDirection.Portrait;

/**
 * Page that contain image in PDF File.
 *
 * @author VincentYeh
 */
public class ImagePageFactory {

    public static PDPage getImagePage(PDDocument document, PageArgument argument, BufferedImage rawImage) throws Exception {
        final PDPage page = new PDPage();

        final Size page_size = getPageSize(argument.getSize(), new Size(rawImage.getHeight(), rawImage.getWidth()));
        final Size rawImageSize = new Size(rawImage.getHeight(), rawImage.getWidth());

        page.setMediaBox(new PDRectangle(page_size.getWidth(), page_size.getHeight()));

        final PageDirection page_direction = getPageDirection(argument.getSize(), rawImageSize, argument.getDirection(), argument.getAutoRotate());
        page.setRotation(getRotateAngle(page_direction));

        final BufferedImage imageOut = getRotatedImage(rawImage, page_direction);
        final Size rotated_img_size = new Size(imageOut.getHeight(), imageOut.getWidth());
        final Size img_size = calculateImageSize(rotated_img_size, page_size);
        final Position position = calculatePosition(page.getRotation() != 0, img_size, page_size, argument.getAlign());

        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, imageOut);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(pdImageXObject, position.getX(), position.getY(), img_size.getWidth(), img_size.getHeight());
        contentStream.close();
        return page;
    }

    private static int getRotateAngle(PageDirection page_direction) {
        return page_direction == Landscape ? -90 : 0;
    }

    private static Size calculateImageSize(Size image_size, Size page_size) {
        return SizeCalculator.getInstance().scaleUpToMax(image_size, page_size);
    }

    private static Position calculatePosition(boolean rotated, Size img_size, Size page_size, PageAlign align) {
        PositionCalculator positionCalculator = PositionCalculator.getInstance();
        PositionCalculator.init(rotated, img_size.getHeight(), img_size.getWidth(), page_size.getHeight(), page_size.getWidth());
        return positionCalculator.calculate(align);
    }

    private static PageDirection getPageDirection(PageSize page_size, Size image_size, PageDirection direction, boolean autoRotate) {
        if (page_size == PageSize.DEPEND_ON_IMG) {
            return Portrait;
        }
        if (autoRotate) {
            return detectDirection(image_size);
        } else {
            return direction;
        }
    }


    private static BufferedImage getRotatedImage(BufferedImage rawImage, PageDirection page_direction) {
        return rotateImg(rawImage, page_direction == Landscape ? 90 : 0);
    }

    private static Size getPageSize(PageSize pageSize, Size imageSize) {
        if (pageSize == PageSize.DEPEND_ON_IMG) {
            return new Size(imageSize.getHeight(), imageSize.getWidth());
        } else {
            PDRectangle rect = pageSize.getPdrectangle();
            return new Size(rect.getHeight(), rect.getWidth());
        }
    }

    private static BufferedImage rotateImg(BufferedImage raw, int rotate_angle) {
        if (rotate_angle == 0) return raw;
        final double rads = Math.toRadians(rotate_angle);
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int w = (int) Math.floor(raw.getWidth() * cos + raw.getHeight() * sin);
        final int h = (int) Math.floor(raw.getHeight() * cos + raw.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, raw.getType());
        final AffineTransform at = new AffineTransform();
        at.translate(w / 2., h / 2.);
        at.rotate(rads, 0, 0);
        at.translate(-raw.getWidth() / 2., -raw.getHeight() / 2.);
        AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(raw, rotatedImage);
        return rotatedImage;
    }

    private static PageDirection detectDirection(Size size) {
        return PageDirection.detectDirection(size.getHeight(), size.getHeight());
    }

}

package org.vincentyeh.IMG2PDF.pdf.page;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

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
public class ImagePageConverter implements Callable<PDPage> {

    private final PDDocument document;
    private final BufferedImage rawImage;
    private final PageArgument argument;

    public ImagePageConverter(PDDocument document, PageArgument argument, BufferedImage image) {
        this.document = document;
        this.rawImage = image;
        this.argument = argument;
    }

    @Override
    public PDPage call() throws Exception {
        final BufferedImage imageOut;
        final Size img_size;
        final Position position;
        final Size page_size;
        final PDPage page = new PDPage();

        if (argument.getSize() == PageSize.DEPEND_ON_IMG) {
            page_size = new Size(rawImage.getHeight(), rawImage.getWidth());

        } else {
            PDRectangle rect = argument.getSize().getPdrectangle();
            page_size = new Size(rect.getHeight(), rect.getWidth());
        }

        page.setMediaBox(new PDRectangle(page_size.getWidth(), page_size.getHeight()));

        if (argument.getSize() == PageSize.DEPEND_ON_IMG) {
            img_size = new Size(rawImage.getHeight(), rawImage.getWidth());
            imageOut = rawImage;
            position = new Position(0, 0);
        } else {
            PageDirection page_direction = argument.getDirection();
            if (argument.getAutoRotate()) {
                page_direction = getDirection(rawImage);
            }

            page.setRotation(page_direction == Landscape ? -90 : 0);
            imageOut = rotateImg(rawImage, page_direction == Landscape ? 90 : 0);

            Size rotated_img_size = new Size(imageOut.getHeight(), imageOut.getWidth());
            SizeCalculator sizeCalculator = new SizeCalculator(rotated_img_size, page_size);
            img_size = sizeCalculator.scaleUpToMax();

            PositionCalculator calculator = new PositionCalculator(page.getRotation() != 0, img_size.getHeight(), img_size.getWidth(), page_size.getHeight(), page_size.getWidth());
            position = calculator.calculate(argument.getAlign());
        }

        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, imageOut);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(pdImageXObject, position.getX(), position.getY(), img_size.getWidth(), img_size.getHeight());
        contentStream.close();
        return page;
    }

    public BufferedImage rotateImg(BufferedImage raw, int rotate_angle) {
        if (rotate_angle == 0) return raw;
        final double rads = Math.toRadians(rotate_angle);
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int w = (int) Math.floor(raw.getWidth() * cos + raw.getHeight() * sin);
        final int h = (int) Math.floor(raw.getHeight() * cos + raw.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, raw.getType());
        final AffineTransform at = new AffineTransform();
        at.translate(w / 2, h / 2);
        at.rotate(rads, 0, 0);
        at.translate(-raw.getWidth() / 2, -raw.getHeight() / 2);
        AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(raw, rotatedImage);
        return rotatedImage;
    }


    private PageDirection getDirection(BufferedImage image) {
        return getDirection(image.getHeight(), image.getWidth());
    }

    private PageDirection getDirection(Size size) {
        return getDirection(size.getHeight(), size.getWidth());
    }

    private PageDirection getDirection(PDRectangle rectangle) {
        return getDirection(rectangle.getHeight(), rectangle.getWidth());
    }

    private PageDirection getDirection(float height, float width) {
        return height / width > 1 ? Portrait : Landscape;
    }
}

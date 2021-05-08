package org.vincentyeh.IMG2PDF.pdf.page;

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
        ProxyImage proxyImage = new ProxyImage(rawImage);
        final PDPage page = new PDPage();
        final PageDirection page_direction = getPageDirection(argument.getSize(), proxyImage.getSize(), argument.getDirection(), argument.getAutoRotate());

        final Size page_size = getPageSize(page_direction, argument.getSize(), proxyImage.getSize());
        page.setMediaBox(new PDRectangle(page_size.getWidth(), page_size.getHeight()));

        ProxyImage outImage = getCalculatedImage(proxyImage, page_size);
        final Position position = calculatePosition(outImage.getSize(), page_size, argument.getAlign());

//        drawImageToPage(document,)
        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, outImage.getImage());
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(pdImageXObject, position.getX(), position.getY(), outImage.getSize().getWidth(), outImage.getSize().getHeight());
        contentStream.close();
        return page;
    }

    private static ProxyImage getCalculatedImage(ProxyImage proxyImage, Size page_size) {
        final Size img_size = calculateImageSize(proxyImage.getSize(), page_size);
        return new ProxyImage(proxyImage.getImage(), img_size);
    }

    private static Size calculateImageSize(Size image_size, Size page_size) {
        return SizeCalculator.getInstance().scaleUpToMax(image_size, page_size);
    }

    private static Position calculatePosition(Size img_size, Size page_size, PageAlign align) {
        PositionCalculator positionCalculator = PositionCalculator.getInstance();
        PositionCalculator.init(img_size.getHeight(), img_size.getWidth(), page_size.getHeight(), page_size.getWidth());
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


    private static Size getPageSize(PageDirection direction, PageSize pageSize, Size imageSize) {
        if (pageSize == PageSize.DEPEND_ON_IMG) {
            return new Size(imageSize.getHeight(), imageSize.getWidth());
        } else {
            PDRectangle rect = pageSize.getPdrectangle();
            if (direction == Landscape)
                return new Size(rect.getWidth(), rect.getHeight());
            else
                return new Size(rect.getHeight(), rect.getWidth());
        }
    }

//    private static BufferedImage rotateImg(BufferedImage raw, int rotate_angle) {
//        if (rotate_angle == 0) return raw;
//        final double rads = Math.toRadians(rotate_angle);
//        final double sin = Math.abs(Math.sin(rads));
//        final double cos = Math.abs(Math.cos(rads));
//        final int w = (int) Math.floor(raw.getWidth() * cos + raw.getHeight() * sin);
//        final int h = (int) Math.floor(raw.getHeight() * cos + raw.getWidth() * sin);
//        final BufferedImage rotatedImage = new BufferedImage(w, h, raw.getType());
//        final AffineTransform at = new AffineTransform();
//        at.translate(w / 2., h / 2.);
//        at.rotate(rads, 0, 0);
//        at.translate(-raw.getWidth() / 2., -raw.getHeight() / 2.);
//        AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//        rotateOp.filter(raw, rotatedImage);
//        return rotatedImage;
//    }

    private static PageDirection detectDirection(Size size) {
        return PageDirection.detectDirection(size.getHeight(), size.getWidth());
    }

    private static class ProxyImage {
        private final BufferedImage image;
        private final Size size;

        public ProxyImage(BufferedImage image) {
            this(image, new Size(image.getHeight(), image.getWidth()));
        }

        public ProxyImage(BufferedImage image, Size size) {
            this.image = image;
            this.size = size;
        }

        public Size getSize() {
            return size;
        }

        public BufferedImage getImage() {
            return image;
        }
    }
}

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
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign.HorizontalAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign.VerticalAlign;
import org.vincentyeh.IMG2PDF.pdf.page.core.Position;
import org.vincentyeh.IMG2PDF.pdf.page.core.PositionCalculator;
import org.vincentyeh.IMG2PDF.pdf.page.core.Size;

/**
 * Page that contain image in PDF File.
 *
 * @author VincentYeh
 */
public class ImagePage extends PDPage {
    private final BufferedImage image;
    private final Size img_size;
    private final Position position;
    public ImagePage(PageAlign align, PageSize size, boolean autoRotate, PageDirection direction, BufferedImage image) {

        final Size page_size;

        if (size == PageSize.DEPEND_ON_IMG) {
            page_size = new Size(image.getHeight(), image.getWidth());
        } else {
            PDRectangle rect = size.getPdrectangle();
            page_size = new Size(rect.getHeight(), rect.getWidth());
        }
        setMediaBox(new PDRectangle(page_size.getWidth(), page_size.getHeight()));

        if (size == PageSize.DEPEND_ON_IMG) {
            img_size = new Size(image.getHeight(), image.getWidth());
            this.image = image;

        } else {
            if (autoRotate) {
                direction = PageDirection.getDirection(image);
            }

            this.setRotation(direction.getPageRotateAngle());
            this.image = rotateImg(image, direction.getImageRotateAngle());
            Size rotated_img_size = new Size(this.image.getHeight(), this.image.getWidth());
            img_size = sizeCalculate(rotated_img_size,page_size);
        }
        PositionCalculator calculator = new PositionCalculator(this.getRotation() != 0, img_size.getHeight(), img_size.getWidth(), page_size.getHeight(), page_size.getWidth());
        position = calculator.calculate(align);
    }

    public ImagePage(PageAlign align, PageSize size, BufferedImage image) {
        this(align, size, true, PageDirection.Vertical, image);
    }

    public ImagePage(PageAlign align, BufferedImage image) {
        this(align, PageSize.DEPEND_ON_IMG, false, PageDirection.Vertical, image);
    }

    public void drawImageToPage(PDDocument doc) throws Exception {

        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(doc, image);
        PDPageContentStream contentStream = new PDPageContentStream(doc, this);
        contentStream.drawImage(pdImageXObject, position.getX(), position.getY(), img_size.getWidth(), img_size.getHeight());
        contentStream.close();
    }

    private Size sizeCalculate(Size img_size, Size page_size) {
        float img_height=img_size.getHeight();
        float img_width=img_size.getWidth();
        float page_height=page_size.getHeight();
        float page_width=page_size.getWidth();

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

        return new Size(out_height, out_width);
    }


    private float[] sizeCalculate(float img_height, float img_width, float page_height, float page_width) {

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

        return new float[]{out_height, out_width};
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
}

package org.vincentyeh.IMG2PDF.pdf;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.IMG2PDF.pdf.argument.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.argument.PageAlign.HorizontalAlign;
import org.vincentyeh.IMG2PDF.pdf.argument.PageAlign.VerticalAlign;
import org.vincentyeh.IMG2PDF.pdf.argument.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.argument.PageSize;

/**
 * Page that contain image in PDF File.
 * 
 * @author VincentYeh
 */
public class ImagePage extends PDPage {
	private final PageAlign align;
	private final BufferedImage image;
	private final float page_height;
	private final float page_width;
	private final float img_height;
	private final float img_width;
	private final float position_y;
	private final float position_x;

	public ImagePage(PageAlign align, PageSize size, boolean autoRotate, PageDirection direction, BufferedImage image) {
		this.align = align;
		if (size == PageSize.DEPEND_ON_IMG) {
			this.img_width = image.getWidth();
			this.img_height = image.getHeight();
			this.page_width = this.img_width;
			this.page_height = this.img_height;

			this.image = image;

			setMediaBox(new PDRectangle(page_width, page_height));
		} else {
			if (autoRotate) {
				direction = PageDirection.getDirection(image);
			}
			
			PDRectangle rect = size.getPdrectangle();
			this.page_width = rect.getWidth();
			this.page_height = rect.getHeight();
			this.setRotation(direction.getPageRotateAngle());
			this.image = rotateImg(image, direction.getImageRotateAngle());
			
			float rotated_img_width = this.image.getWidth();
			float rotated_img_height = this.image.getHeight();

			float[] f_size = sizeCalculate(rotated_img_height, rotated_img_width, page_height, page_width);
			this.img_height = f_size[0];
			this.img_width = f_size[1];

			setMediaBox(rect);
		}
		
		float[] f_position = positionCalculate(this.getRotation() != 0, img_height, img_width, page_height, page_width);
		position_y = f_position[0];
		position_x = f_position[1];

	}

	public ImagePage(PageAlign align, PageSize size, BufferedImage image) {
		this(align, size,true,PageDirection.Vertical,image);
	}
	
	public ImagePage(PageAlign align, BufferedImage image) {
		this(align,PageSize.DEPEND_ON_IMG,false,PageDirection.Vertical,image);
	}

	public void drawImageToPage(PDDocument doc) throws Exception {

		PDImageXObject pdImageXObject = LosslessFactory.createFromImage(doc, image);
		PDPageContentStream contentStream = new PDPageContentStream(doc, this);
		contentStream.drawImage(pdImageXObject, position_x, position_y, img_width, img_height);
		contentStream.close();
	}

	/**
	 * Compute the position of image by align value.
	 *
	 * @return
	 *         <ol>
	 *         <li>x</li>
	 *         <li>y</li>
	 *         <li>image width</li>
	 *         <li>image height</li>
	 *         </ol>
	 */
	private float[] positionCalculate(boolean isRotated, float img_height, float img_width, float page_height,
			float page_width) {

		float position_x = 0, position_y = 0;

		HorizontalAlign hori_align = align.getHorizontal();
		VerticalAlign verti_align = align.getVertical();

		float x_space = page_width - img_width;
		float y_space = page_height - img_height;

		if (y_space == 0) {
			if (!isRotated) {
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
				case FILL:
					break;
				default:
					break;
				}
			} else {
				switch (verti_align) {
				case TOP:
					position_x = 0;
					break;
				case BOTTOM:
					position_x = x_space;
					break;
				case CENTER:
					position_x = x_space / 2;
					break;
				case FILL:
					break;
				default:
					break;

				}
			}
		} else if (x_space == 0) {

			if (!isRotated) {
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
				case FILL:
					break;
				default:
					break;

				}
			} else {
				switch (hori_align) {
				case LEFT:
					position_y = 0;
					break;
				case RIGHT:
					position_y = y_space;
					break;
				case CENTER:
					position_y = y_space / 2;
					break;
				case FILL:
					break;
				default:
					break;
				}
			}

		}
		return new float[] { position_y, position_x };
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

		return new float[] { out_height, out_width };
	}

	public BufferedImage rotateImg(BufferedImage raw, int rotate_angle) {
		if(rotate_angle==0)return raw;
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

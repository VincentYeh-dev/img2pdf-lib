package org.vincentyeh.IMG2PDF.pdf;

import java.awt.image.BufferedImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.IMG2PDF.pdf.Align.LeftRightAlign;
import org.vincentyeh.IMG2PDF.pdf.Align.TopBottomAlign;
import org.vincentyeh.IMG2PDF.util.ImageProcess;

public class ImagePage extends PDPage{
	private final Align align;
	private BufferedImage image;
	private final float page_height;
	private final float page_width;
	private final Size size;
	private boolean autoRotate=true;
	
	/**
	 * <b>The calculation of diff</b>
	 * <p>
	 * resize=abs((image height/image width)-(page height/page width))
	 * </p>
	 * 
	 * <b>Feature</b>
	 * <p>
	 * resize is the variable that can be set to prevent raw image over-deformed.
	 * </p>
	 * <p>
	 * The default value is less than 0.It do nothing when you don't set it to the
	 * value that more than 0.
	 * </p>
	 * <p>
	 * If you do that before execution of process() method,the program will throw a
	 * Exception that warn a user the sub is out of range when the resize larger than
	 * max_resize.
	 * </p>
	 * 
	 */
	private float max_resize;
	
	
	public ImagePage(Align align, Size size, float height, float width) {
		this.align = align;
		this.size = size;
		if (size == Size.DEPEND_ON_IMG) {
			setMediaBox(new PDRectangle(width, height));
			this.page_width = width;
			this.page_height = height;
		} else {
			PDRectangle rect=size.getPdrectangle();
			setMediaBox(rect);
			this.page_width = rect.getWidth();
			this.page_height = rect.getHeight();
		}
	}

	public ImagePage(Align align, Size size) {
		this(align,size,-1,-1);
	}
	
	public ImagePage(Align algin,float height, float width) {
		this(algin,Size.DEPEND_ON_IMG,height,width);
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void drawImageToPage(PDDocument doc) throws Exception {

		float img_width = image.getWidth();
		float img_height = image.getHeight();
		float position_x = 0, position_y = 0;
		float out_width = 0, out_height = 0;
		BufferedImage outImage=null;
		if (size == Size.DEPEND_ON_IMG) {
			out_width = img_width;
			out_height = img_height;
			outImage=image;
		} else {
			int angle = 90;
			boolean isRotated = false;
			BufferedImage rotatedImage = null;
			
			if(autoRotate) {
				if ((img_height / img_width) >= 1) {
					this.setRotation(0);
					rotatedImage=image;
				} else {
					this.setRotation(angle);
					rotatedImage = ImageProcess.rotateImg(image, -1 * angle);
					isRotated = true;
				}
			}else {
				this.setRotation(0);
				rotatedImage=image;
			}
			
			outImage=rotatedImage;
			img_width = rotatedImage.getWidth();
			img_height = rotatedImage.getHeight();

			float[] f_size = sizeCalculate(img_height, img_width, page_height, page_width);
			out_height = f_size[0];
			out_width = f_size[1];

			float[] f_position = positionCalculate(isRotated, out_height, out_width, page_height, page_width);
			position_y = f_position[0];
			position_x = f_position[1];
		}
		PDImageXObject pdImageXObject = LosslessFactory.createFromImage(doc, outImage);
		PDPageContentStream contentStream = new PDPageContentStream(doc, this);
		contentStream.drawImage(pdImageXObject, position_x, position_y, out_width, out_height);
		contentStream.close();
	}

	/**
	 * Compute the position of image by align value.
	 * 
	 * @param raw  The image that put into this page.
	 * @param page The Page that contain image.
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

		LeftRightAlign LRA = align.getLRA();
		TopBottomAlign TBA = align.getTBA();

		float x_space = page_width - img_width;
		float y_space = page_height - img_height;

		if (y_space == 0) {
			if (!isRotated) {
				switch (LRA) {
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
				switch (TBA) {
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
				switch (TBA) {
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
				switch (LRA) {
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
	
	public void setMaxResize(float max_resize) {
		this.max_resize = max_resize;
	}
	
	/**
	 * Rotate the image when image is horizontal.
	 * @param enable enable or disable rotation.
	 */
	public void setAutoRotate(boolean enable) {
		this.autoRotate = enable;
	}
	
}

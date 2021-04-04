package org.vincentyeh.IMG2PDF.pdf.page;

import java.awt.image.BufferedImage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;

/**
 * Direction of page.Horizontal or Vertical.
 * 
 * @author VincentYeh
 */
public enum PageDirection {
	Horizontal(90), Vertical(0);
	private final int page_rotate_angle;
	private final int image_rotate_angle;
	

	private PageDirection(int angle) {
		page_rotate_angle = angle;
		image_rotate_angle = -angle;
	}

	public int getPageRotateAngle() {
		return page_rotate_angle;
	}

	public int getImageRotateAngle() {
		return image_rotate_angle;
	}

	public static PageDirection getDirection(BufferedImage image) {
		return getDirection(image.getHeight(), image.getWidth());
	}

	public static PageDirection getDirection(PDRectangle rectangle) {
		return getDirection(rectangle.getHeight(), rectangle.getWidth());
	}

	public static PageDirection getDirection(float height, float width) {
		return height / width > 1 ? Vertical : Horizontal;
	}

	public static PageDirection getByString(String str) throws UnrecognizedEnumException {
		PageDirection[] directions = PageDirection.values();
		for (PageDirection direction : directions) {
			if (direction.toString().equals(str))
				return direction;
		}
		throw new UnrecognizedEnumException(str,PageDirection.class);
	}

}

package org.vincentyeh.IMG2PDF.pdf;

public enum PageDirection {
	Horizontal(90),Vertical(0);
	final int page_rotate_angle;
	final int image_rotate_angle;
	
	PageDirection(int angle){
		page_rotate_angle=angle;
		image_rotate_angle=-angle;
	}
	
	public int getPageRotateAngle() {
		return page_rotate_angle;
	}
	public int getImageRotateAngle() {
		return image_rotate_angle;
	}
	
}

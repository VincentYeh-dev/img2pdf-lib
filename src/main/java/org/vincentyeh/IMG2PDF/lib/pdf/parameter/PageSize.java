package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;

/**
 * Size is the variable that define size of pages of PDFFile.
 * 
 * @author VincetYeh
 */
public enum PageSize {
	A0(new SizeF(2383.937F, 3370.3938F)),
	A1(new SizeF(1683.7795F, 2383.937F)),
	A2(new SizeF(1190.5513F, 1683.7795F)),
	A3(new SizeF(841.8898F, 1190.5513F)),
	A4(new SizeF(595.27563F, 841.8898F)),
	A5(new SizeF(419.52756F, 595.27563F)),
	A6(new SizeF(297.63782F, 419.52756F)),
	LEGAL(new SizeF(612.0F, 1008.0F)),
	LETTER(new SizeF(612.0F, 792.0F)),
	DEPEND_ON_IMG(new SizeF(0,0));

	private static final float POINTS_PER_INCH = 72.0F;
	private static final float POINTS_PER_MM = 2.8346457F;

	private final SizeF size;

	PageSize(SizeF size) {
		this.size= size;
	}

	public SizeF getSizeInPixels() {
		return size;
	}

	public SizeF getSizeInMillimeters() {
		return new SizeF(size.width()/POINTS_PER_MM,size.height()/POINTS_PER_MM);
	}
	public SizeF getSizeInInches() {
		return new SizeF(size.width()/POINTS_PER_INCH,size.height()/POINTS_PER_INCH);
	}
}
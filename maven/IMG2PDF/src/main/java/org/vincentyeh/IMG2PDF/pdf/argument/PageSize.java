package org.vincentyeh.IMG2PDF.pdf.argument;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;

/**
 * Size is the variable that define size of pages of PDFFile.
 * 
 * @author VincetYeh
 */
public enum PageSize {
	A0(PDRectangle.A0), A1(PDRectangle.A1), A2(PDRectangle.A2), A3(PDRectangle.A3), A4(PDRectangle.A4),
	A5(PDRectangle.A5), A6(PDRectangle.A6), LEGAL(PDRectangle.LEGAL), LETTER(PDRectangle.LETTER), DEPEND_ON_IMG(null);

	/**
	 * This is the constant used to create the PDF.
	 */
	private final PDRectangle pdrectangle;

	PageSize(PDRectangle pdrectangle) {
		this.pdrectangle = pdrectangle;
	}

	/**
	 * Create Size by String
	 * 
	 * @param str The String contain definition of Size.
	 * @return Size
	 * @throws UnrecognizedEnumException When unrecognizable enum pass in to constructor.
	 */
	public static PageSize getByString(String str) throws UnrecognizedEnumException {
		PageSize[] sizes = PageSize.values();
		for (PageSize size : sizes) {
			if (size.toString().equals(str))
				return size;
		}
		throw new UnrecognizedEnumException(str, PageSize.class);
	}

	public PDRectangle getPdrectangle() {
		return pdrectangle;
	}

}
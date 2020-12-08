package org.vincentyeh.IMG2PDF.pdf.page;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
/**
 * Size is the variable that define size of pages of PDFFile. 
 * 
 * @author VincetYeh
 */
public enum PageSize {
	A0("A0", PDRectangle.A0), A1("A1", PDRectangle.A1), A2("A2", PDRectangle.A2), A3("A3", PDRectangle.A3),
	A4("A4", PDRectangle.A4), A5("A5", PDRectangle.A5), A6("A6", PDRectangle.A6), LEGAL("LEGAL", PDRectangle.LEGAL),
	LETTER("LETTER", PDRectangle.LETTER), DEPEND_ON_IMG("DEPEND", null);

	/**
	 * This is the String constant of Size.
	 */
	private final String str;

	/**
	 * This is the constant used to create the PDF.
	 */
	private final PDRectangle pdrectangle;

	PageSize(String str, PDRectangle pdrectangle) {
		this.str = str;
		this.pdrectangle = pdrectangle;
	}

	/**
	 * Create Size by String
	 * 
	 * @param str The String contain definition of Size.
	 * @return Size
	 */
	public static PageSize getSizeFromString(String str) throws IllegalSizeException {
		switch (str) {
		case "A0":
			return A0;
		case "A1":
			return A1;
		case "A2":
			return A2;
		case "A3":
			return A3;
		case "A4":
			return A4;
		case "A5":
			return A5;
		case "A6":
			return A6;
		case "LEGAL":
			return LEGAL;
		case "LETTER":
			return LETTER;
		case "DEPEND":
			return DEPEND_ON_IMG;
		default:
			throw new IllegalSizeException(str);
		}
	}

	public String getStr() {
		return str;
	}

	public PDRectangle getPdrectangle() {
		return pdrectangle;
	}

	/**
	 * List all item of enum in String.
	 * 
	 * @return The array of enum in String.
	 */
	public static String[] valuesStr() {
		PageSize[] size_list = PageSize.values();
		String[] str_list = new String[size_list.length];
		for (int i = 0; i < str_list.length; i++) {
			str_list[i] = size_list[i].getStr();
		}
		return str_list;
	}

	public static class IllegalSizeException extends IllegalArgumentException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 182897419820418934L;

		public IllegalSizeException(String str) {
			super(str + " isn't a type of size.");
			// TODO Auto-generated constructor stub
		}

	}

}
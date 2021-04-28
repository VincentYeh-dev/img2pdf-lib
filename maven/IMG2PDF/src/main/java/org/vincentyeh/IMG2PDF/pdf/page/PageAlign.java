package org.vincentyeh.IMG2PDF.pdf.page;

import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;

/**
 * The class which define Alignment of page of PDF
 * 
 * @author vincent
 */
public class PageAlign {
//	水平對齊
	private final HorizontalAlign hori_align;
//	垂直對齊
	private final VerticalAlign verti_align;

	/**
	 * Create Align by enums.
	 * 
	 * @param hori_align Horizontal Align
	 * @param verti_align Vertical Align
	 */
	public PageAlign(final HorizontalAlign hori_align, final VerticalAlign verti_align) {
		this.hori_align = hori_align;
		this.verti_align = verti_align;
	}

	/**
	 * Create Align by String. The str must be "TopBottomAlign|LeftRightAlign"
	 * format.
	 * 
	 * @param str Alignment
	 * @throws UnrecognizedEnumException When unrecognizable enum pass in to constructor.
	 */
	public PageAlign(final String str) throws UnrecognizedEnumException{
		String[] verti_hori_align = str.split("-");
		this.verti_align = VerticalAlign.getByString(verti_hori_align[0]);
		this.hori_align = HorizontalAlign.getByString(verti_hori_align[1]);
	}

	public HorizontalAlign getHorizontal() {
		return hori_align;
	}

	public VerticalAlign getVertical() {
		return verti_align;
	}

	@Override
	public String toString() {
		return String.format("%s-%s", verti_align, hori_align);
	}

	public enum HorizontalAlign {
		RIGHT, LEFT, CENTER;

		public static HorizontalAlign getByString(String str) throws UnrecognizedEnumException {
			HorizontalAlign[] aligns = HorizontalAlign.values();
			for (HorizontalAlign align : aligns) {
				if (align.toString().equals(str))
					return align;
			}
			throw new UnrecognizedEnumException(str,"HorizontalAlign",values());
		}

	}

	public enum VerticalAlign {
		TOP, BOTTOM, CENTER;

		public static VerticalAlign getByString(String str) throws UnrecognizedEnumException {
			VerticalAlign[] aligns = VerticalAlign.values();
			for (VerticalAlign align : aligns) {
				if (align.toString().equals(str))
					return align;
			}
			throw new UnrecognizedEnumException(str,"VerticalAlign",values());
		}
	}

}
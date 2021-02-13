package org.vincentyeh.IMG2PDF.pdf.page;

/**
 * The class which define Alignment of page of PDF
 * 
 * @author vincent
 */
public class PageAlign {
//	水平對齊
	private final HorizontalAlign hori_algin;
//	垂直對齊
	private final VerticalAlign verti_algin;

	/**
	 * Create Align by enums.
	 * 
	 * @param LRA Left Right Align
	 * @param TBA Top Bottom Align
	 */
	public PageAlign(final HorizontalAlign hori_algin, final VerticalAlign verti_algin) {
		this.hori_algin = hori_algin;
		this.verti_algin = verti_algin;
	}

	/**
	 * Create Align by String. The str must be "TopBottomAlign|LeftRightAlign"
	 * format.
	 * 
	 * @param str Alignment
	 */
	public PageAlign(final String str){
		String[] verti_hori_align = str.split("-");
		this.verti_algin = VerticalAlign.getByString(verti_hori_align[0]);
		this.hori_algin = HorizontalAlign.getByString(verti_hori_align[1]);
	}

	public HorizontalAlign getHorizontal() {
		return hori_algin;
	}

	public VerticalAlign getVertical() {
		return verti_algin;
	}

	@Override
	public String toString() {
		return String.format("%s-%s", verti_algin,hori_algin);
	}

	public enum HorizontalAlign {
		RIGHT, LEFT, CENTER, FILL;

		public static HorizontalAlign getByString(String str) {
			HorizontalAlign[] aligns = HorizontalAlign.values();
			for (HorizontalAlign align : aligns) {
				if (align.toString().equals(str))
					return align;
			}
			throw new IllegalArgumentException("HorizontalAlign is invalid");
		}

		public static String[] valuesStr() {
			HorizontalAlign[] aligns = HorizontalAlign.values();
			String[] str_list = new String[aligns.length];
			for (int i = 0; i < str_list.length; i++) {
				str_list[i] = aligns[i].toString();
			}
			return str_list;
		}

	}

	public enum VerticalAlign {
		TOP, BOTTOM, CENTER, FILL;

		public static VerticalAlign getByString(String str) {
			VerticalAlign[] aligns = VerticalAlign.values();
			for (VerticalAlign align : aligns) {
				if (align.toString().equals(str))
					return align;
			}
			throw new IllegalArgumentException("VerticalAlign is invalid");
		}

		public static String[] valuesStr() {
			VerticalAlign[] aligns = VerticalAlign.values();
			String[] str_list = new String[aligns.length];
			for (int i = 0; i < str_list.length; i++) {
				str_list[i] = aligns[i].toString();
			}
			return str_list;
		}
	}

}
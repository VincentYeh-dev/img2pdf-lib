package org.vincentyeh.IMG2PDF.pdf.page;

import org.vincentyeh.IMG2PDF.file.ImgFile.Order;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * The class which define Alignment of page of PDF
 * 
 * @author vincent
 */
public class PageAlign implements ArgumentType<PageAlign>{
	private final LeftRightAlign LRA;
	private final TopBottomAlign TBA;


	@Override
	public PageAlign convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
		return new PageAlign(value);
	}
	
	/**
	 * Create Align by enums.
	 * 
	 * @param LRA Left Right Align
	 * @param TBA Top Bottom Align
	 */
	public PageAlign(LeftRightAlign LRA, TopBottomAlign TBA) {
		this.LRA = LRA;
		this.TBA = TBA;
	}

	/**
	 * Create Align by String. The str must be "TopBottomAlign|LeftRightAlign"
	 * format.
	 * 
	 * @param str Alignment
	 */
	public PageAlign(String str) throws IllegalAlignException {
		String[] LR_TB_A = str.split("-");
		try {
			TBA = TopBottomAlign.getByStr(LR_TB_A[0]);
			LRA = LeftRightAlign.getByStr(LR_TB_A[1]);
		} catch (IllegalArgumentException e) {
			throw new IllegalAlignException(e.getMessage());
		}

	}

	public LeftRightAlign getLRA() {
		return LRA;
	}

	public TopBottomAlign getTBA() {
		return TBA;
	}

	@Override
	public String toString() {
		return String.format("%s-%s", TBA.getStr(), LRA.getStr());
	}

	public static class IllegalAlignException extends IllegalArgumentException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 182897419820418934L;

		public IllegalAlignException(String str) {
			super(str + " isn't a type of align.");
			// TODO Auto-generated constructor stub
		}

	}

	public enum LeftRightAlign {
		RIGHT("RIGHT"), LEFT("LEFT"), CENTER("CENTER"), FILL("FILL");
		private String str;

		private LeftRightAlign(String str) {
			this.str = str;
		}

		public static LeftRightAlign getByStr(String str) throws IllegalArgumentException {
			switch (str) {
			case "RIGHT":
				return RIGHT;
			case "LEFT":
				return LEFT;
			case "CENTER":
				return CENTER;
			case "FILL":
				return FILL;
			default:
				throw new IllegalArgumentException(str);
			}
		}

		public String getStr() {
			return str;
		}

		public static String[] valuesStr() {
			Order[] list = Order.values();
			String[] str_list = new String[list.length];
			for (int i = 0; i < str_list.length; i++) {
				str_list[i] = list[i].getStr();
			}
			return str_list;
		}

	}

	public enum TopBottomAlign {
		TOP("TOP"), BOTTOM("BOTTOM"), CENTER("CENTER"), FILL("FILL");
		private String str;

		private TopBottomAlign(String str) {
			this.str = str;
		}

		public static TopBottomAlign getByStr(String str) throws IllegalArgumentException {
			switch (str) {
			case "TOP":
				return TOP;
			case "BOTTOM":
				return BOTTOM;
			case "CENTER":
				return CENTER;
			case "FILL":
				return FILL;
			default:
				throw new IllegalArgumentException(str);
			}
		}

		public String getStr() {
			return str;
		}

		public static String[] valuesStr() {
			Order[] list = Order.values();
			String[] str_list = new String[list.length];
			for (int i = 0; i < str_list.length; i++) {
				str_list[i] = list[i].getStr();
			}
			return str_list;
		}
	}

}
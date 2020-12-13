package org.vincentyeh.IMG2PDF.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentUtil {
	static enum SymbolItem {
		VERTICAL_BAR("$VBAR", "|");

		private final String symbol;
		private final String real;

		SymbolItem(String symbol, String real) {
			this.symbol = symbol;
			this.real = real;
		}

		public static String getRealFromSymbol(String symbol) {
			return getSymbolItem(symbol).getReal();
		}

		public static SymbolItem getSymbolItem(String symbol) {
			switch (symbol) {
			case "$VBAR":
				return VERTICAL_BAR;
			default:
				return null;
			}
		}

		String getSymbol() {
			return symbol;
		}

		String getReal() {
			return real;
		}
	}

	public static String[] fixArgumentSpaceArray(String[] args) {
		String combined = combineArray(args);

//		((\s+|[^"]*\s+)("[^"]*")|([^"]*))*\s*
		if (!combined.matches("((\\s+|[^\"]*\\s+)(\"[^\"]*\")|([^\"]*))*\\s*"))
			throw new QuotesNotpairException("symbol error.");

//		(\s+|[^"]*\s+)("[^"]*")
		Pattern pattern = Pattern.compile("(\\s+|[^\"]*\\s+)(\"[^\"]*\")");
		Matcher matcher = pattern.matcher(combined);
		String replaced = combined;
		while (matcher.find()) {
			String origin = matcher.group(2);
			String fixed = origin.replaceAll("\\s", "\\$SPACE");
			fixed = fixed.replace("\"", "");
			replaced = replaced.replace(origin, fixed);
		}

		String[] a = replaced.split("\\s");
		for (int i = 0; i < a.length; i++) {
			a[i] = a[i].replace("$SPACE", " ");
		}
		return a;
	}

	public static String combineArray(String[] array) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(array[0]);
		for (int i = 1; i < array.length; i++) {
			buffer.append(" ");
			buffer.append(array[i]);
		}
		return buffer.toString();
	}

	public static String[] fixSymbol(String[] args) {
		String combined=combineArray(args);
		SymbolItem[] items=SymbolItem.values();
		String replaced=combined;
		for(SymbolItem item:items) {
			replaced=replaced.replace(item.getSymbol(),item.getReal());
		}
		return replaced.split("\\s");
	}

	public static class QuotesNotpairException extends RuntimeException {

		private static final long serialVersionUID = -1999559893289634112L;

		public QuotesNotpairException(String msg) {
			super(msg);
		}

	}
}

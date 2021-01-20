package org.vincentyeh.IMG2PDF.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Test on https://regex101.com/
 * https://www.ocpsoft.org/regex/how-to-interrupt-a-long-running-infinite-java-regular-expression/
 * https://www.regular-expressions.info/catastrophic.html
 * 
 * @author vincent
 *
 */
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

//		regex:^[^\s].*
		if (combined.matches("^[^\\s].*")) {
//			No space in the start of the line.
			combined = " " + combined;
		}

//		infinite loop:
//		regex:(((\s+|[^"]*\s+)("[^"]*")|([^"]*))*\s*)|("[^"]*")
		
//		regex:(\s("([^"]+)"|([^\s"]+)))+
		if (!combined.matches("(\\s(\"([^\"]+)\"|([^\\s\"]+)))+")) {
			throw new ArgumentSyntaxException("symbol error.");
		}
		
//		regex:(\s+|[^"]*\s+)("[^"]*")
//		Pattern pattern = Pattern.compile("(\\s+|[^\"]*\\s+)(\"[^\"]*\")");
		
//		regex:"[^"]*"
		Pattern pattern = Pattern.compile("\"[^\"]*\"");
		Matcher matcher = pattern.matcher(combined);
		String replaced = combined;
		while (matcher.find()) {
//			String origin = matcher.group(2);
			String origin = matcher.group(0);
			String fixed = origin.replaceAll("\\s", "" + (char) 0x05);
			fixed = fixed.replace("\"", "");
			replaced = replaced.replace(origin, fixed);
		}

		String[] a = replaced.split("\\s");
		ArrayList<String> newStrArray = new ArrayList<String>();

		for (int i = 0; i < a.length; i++) {
			if (a[i] == null || a[i].isEmpty())
				continue;
			newStrArray.add(a[i].replace("" + (char) 0x05, " "));
		}

		String[] strs = new String[newStrArray.size()];
		newStrArray.toArray(strs);
		return strs;
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
		String combined = combineArray(args);
		SymbolItem[] items = SymbolItem.values();
		String replaced = combined;
		for (SymbolItem item : items) {
			replaced = replaced.replace(item.getSymbol(), item.getReal());
		}
		return replaced.split("\\s");
	}

	public static class ArgumentSyntaxException extends RuntimeException {

		private static final long serialVersionUID = -1999559893289634112L;

		public ArgumentSyntaxException(String msg) {
			super(msg);
		}
	}

}

package org.vincentyeh.IMG2PDF.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://regex101.com/
 * 
 * @author VincentYeh
 */
public class NameFormatter {
	private static final String SYMBOL_CURRENT_Y="$CY";
	private static final String SYMBOL_CURRENT_M="$CM";
	private static final String SYMBOL_CURRENT_D="$CD";
	private static final String SYMBOL_CURRENT_H="$CH";
	private static final String SYMBOL_CURRENT_N="$CN";
	private static final String SYMBOL_CURRENT_S="$CS";

	private static final String SYMBOL_MODIFY_Y="$MY";
	private static final String SYMBOL_MODIFY_M="$MM";
	private static final String SYMBOL_MODIFY_D="$MD";
	private static final String SYMBOL_MODIFY_H="$MH";
	private static final String SYMBOL_MODIFY_N="$MN";
	private static final String SYMBOL_MODIFY_S="$MS";
	
	private final ArrayList<File> parents = new ArrayList<>();
	private final File file;

	public NameFormatter(File file) throws FileNotFoundException {
		this.file = file;
		File parent = file.getParentFile();
		while (parent != null) {
			parents.add(parent);
			parent = parent.getParentFile();
		}
	}

	private String formatCurrentDateTime(String format, Date current_date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(current_date);
		format = format.replace(SYMBOL_CURRENT_Y, String.format("%d", cal.get(Calendar.YEAR)))
				.replace(SYMBOL_CURRENT_M, String.format("%02d", cal.get(Calendar.MONTH) + 1))
				.replace(SYMBOL_CURRENT_D, String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)))
				.replace(SYMBOL_CURRENT_H, String.format("%02d", cal.get(Calendar.HOUR)))
				.replace(SYMBOL_CURRENT_N, String.format("%02d", cal.get(Calendar.MINUTE)))
				.replace(SYMBOL_CURRENT_S, String.format("%02d", cal.get(Calendar.SECOND)));
		return format;
	}
	
	private String formatModifyDateTime(String format, Date modify_date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(modify_date);
		format = format.replace(SYMBOL_MODIFY_Y, String.format("%d", cal.get(Calendar.YEAR)))
				.replace(SYMBOL_MODIFY_M, String.format("%02d", cal.get(Calendar.MONTH) + 1))
				.replace(SYMBOL_MODIFY_D, String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)))
				.replace(SYMBOL_MODIFY_H, String.format("%02d", cal.get(Calendar.HOUR)))
				.replace(SYMBOL_MODIFY_N, String.format("%02d", cal.get(Calendar.MINUTE)))
				.replace(SYMBOL_MODIFY_S, String.format("%02d", cal.get(Calendar.SECOND)));
		return format;
	}

	private String formatParents(String format, ArrayList<File> parents)
			throws NumberFormatException, ParentOverPointException {
		Matcher matcher = Pattern.compile(".*?(\\$PARENT\\{[0-9]{1,}\\}).*?").matcher(format);
		while (matcher.find()) {
			String parent = matcher.group(1);
			Matcher matcher_num = Pattern.compile("\\$PARENT\\{([0-9]{1,})\\}").matcher(parent);
			if (matcher_num.find()) {
				int index = Integer.parseInt(matcher_num.group(1));
				if (index >= parents.size()) {
					throw new ParentOverPointException(index);
				}
				format = format.replace(parent, parents.get(index).getName());
			}
		}
		return format;
	}

	public String format(String format) throws ParentOverPointException {
		String[] buf = file.getName().split("\\.");

		format = format.replace("$NAME", buf[0]);

		if (format.matches(".*\\$PARENT\\{([0-9]{1,})\\}.*"))
			format = formatParents(format, parents);

		if (format.matches(".*\\$(CY|CM|CD|CH|CN|CS).*"))
			format = formatCurrentDateTime(format,new Date());
		
		if (format.matches(".*\\$(MY|MM|MD|MH|MN|MS).*"))
			format = formatModifyDateTime(format, new Date(file.lastModified()));

		return format;
	}

	public static class ParentOverPointException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2237547036157477461L;

		public ParentOverPointException(int index) {
			super(String.format("Parent in index:%d not found.", index));
		}

	}
}

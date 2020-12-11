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

	private String formatDateTime(String format, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		format = format.replace("$Y", String.format("%d", cal.get(Calendar.YEAR)))
				.replace("$M", String.format("%02d", cal.get(Calendar.MONTH) + 1))
				.replace("$D", String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)))
				.replace("$H", String.format("%02d", cal.get(Calendar.HOUR)))
				.replace("$N", String.format("%02d", cal.get(Calendar.MINUTE)))
				.replace("$S", String.format("%02d", cal.get(Calendar.SECOND)));
		return format;
	}

	private String formatParents(String format, ArrayList<File> parents)
			throws FileNotFoundException, ParentOverPointException {
		Matcher matcher = Pattern.compile(".*?(\\$PARENT\\{[0-9]{1,}\\}).*?").matcher(format);
		while (matcher.find()) {
			String parent = matcher.group(1);
			Matcher matcher_num = Pattern.compile("\\$PARENT\\{([0-9]{1,})\\}").matcher(parent);
			if (matcher_num.find()) {
				int index = Integer.valueOf(matcher_num.group(1));
				if (index >= parents.size()) {
					throw new ParentOverPointException(index);
				}
				format = format.replace(parent, parents.get(index).getName());
			}
		}
		return format;
	}

	public String format(String format) throws FileNotFoundException, ParentOverPointException {
		String buf[] = file.getName().split("\\.");

		format = format.replace("$NAME", buf[0]);

		if (format.matches(".*\\$PARENT\\{([0-9]{1,})\\}.*"))
			format = formatParents(format, parents);

		if (format.matches(".*\\$(Y|M|D|H|N|S){1,}.*"))
			format = formatDateTime(format, new Date(file.lastModified()));

		return format;
	}

	public class ParentOverPointException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2237547036157477461L;

		public ParentOverPointException(int index) {
			super(String.format("Parent in index:%d not found.", index));
		}

	}
}

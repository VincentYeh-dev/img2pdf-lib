package org.vincentyeh.IMG2PDF.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 
 * @author vincent
 */
public class NameFormatter {
	private String converted_name;

	public NameFormatter(String format, File file) throws FileNotFoundException {
		String changed = format;
		HashMap<String, Integer> ps = parentFormatter(format);
		for (String key : ps.keySet()) {
			int level = ps.get(key);
			changed = changed.replace(key, getParentFile(file, level).getName());
		}

		Date date = new Date(file.lastModified());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");
		String buf[] = file.getName().split("\\.");

		changed = changed.replace("$NAME", buf[0]).replace("$DATE", sdf.format(date));
		converted_name = changed;
	}

	HashMap<String, Integer> parentFormatter(String format) {
		Matcher matcher = Pattern.compile(".*?(\\$PARENT\\{[0-9]{1,}\\}).*?").matcher(format);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		while (matcher.find()) {
			String parent = matcher.group(1);
			Matcher matcher_num = Pattern.compile("\\$PARENT\\{([0-9]{1,})\\}").matcher(parent);
			if (matcher_num.find()) {
				int level = Integer.valueOf(matcher_num.group(1));
				map.put(parent, level);
			}
		}
		return map;
	}

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		System.out.println("NameFormatter Testing");
		NameFormatter nf = new NameFormatter("$PARENT{7}\\$PARENT{1}\\$PARENT{0}\\$NAME.pdf", new File("test_file\\"));
		System.out.println(nf.getConverted());
	}

	public String getConverted() {
		return converted_name;
	}
	
	@Override
	public String toString() {
		return getConverted();
	}
	
	private File getParentFile(File file, int index) throws FileNotFoundException {
		File buf = new File(file.getAbsolutePath());
		if (!file.exists()) {
			throw new FileNotFoundException(file.getName() + " not found.");
		}

		for (int i = 0; i <= index; i++) {
			if (buf != null) {
				buf = buf.getParentFile();
			}else {
				throw new ParentOverPointException(file,index);
			}
		}
		return buf;
	}

	public class ParentOverPointException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2237547036157477461L;
		
		public ParentOverPointException(File file,int index) {
			super(String.format("Index:%d at %s not found.",index,file.getAbsoluteFile()));
		}
		
	}
}

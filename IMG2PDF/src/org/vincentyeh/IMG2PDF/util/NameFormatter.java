package org.vincentyeh.IMG2PDF.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameFormatter {
	public NameFormatter(String format,File file) {
		// TODO Auto-generated constructor stub
		Matcher m = Pattern.compile(".*?\\$PARENT\\{([0-9]{1,})\\}").matcher(format); 
		int level=-1;
		String str_level=null;
		if (m.matches()) 
		{ 
			str_level = m.group(1);
			level=Integer.valueOf(str_level);
		} 
		
		Date date = new Date(file.lastModified());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");
		String buf[] = file.getName().split("\\.");

		String changed = format.replace("$NAME", buf[0]).replace("$DATE", sdf.format(date));
		
		if(level!=-1&&str_level!=null)
			changed = changed.replace("$PARENT{"+str_level+"}", getParentFile(file, level).getAbsolutePath());
		System.out.println(changed);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("NameFormatter Testing");
		new NameFormatter("$PARENT{0}", new File("test_file\\"));
	}

	File getParentFile(File file, int index) {
		File buf = new File(file.getAbsolutePath());

		for (int i = 0; i <= index; i++) {
			buf = buf.getParentFile();
		}
		return buf;
	}
}

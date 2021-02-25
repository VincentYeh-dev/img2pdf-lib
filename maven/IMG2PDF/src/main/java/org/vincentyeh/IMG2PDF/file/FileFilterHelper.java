package org.vincentyeh.IMG2PDF.file;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FileFilterHelper implements FileFilter{
	private final String regex;

	public FileFilterHelper(String regex){
		try {
			Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			throw e;
		}
		this.regex = regex;
	}

	@Override
	public boolean accept(File file) {
		return file.getName().matches(regex);
	}
	
	public String getRegex() {
		return regex;
	}
}

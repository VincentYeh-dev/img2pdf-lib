package org.vincentyeh.IMG2PDF.file;

import java.io.File;
import java.io.FileFilter;

public class FileFilterHelper implements FileFilter {
	private final String regex;
	public FileFilterHelper(String regex) {
		this.regex=regex;
	}
	
	@Override
	public boolean accept(File file) {
		return file.getName().matches(regex);
	}

}

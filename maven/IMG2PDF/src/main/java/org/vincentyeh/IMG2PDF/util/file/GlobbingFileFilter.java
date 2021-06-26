package org.vincentyeh.IMG2PDF.util.file;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.regex.PatternSyntaxException;

public class GlobbingFileFilter implements FileFilter {
	private final PathMatcher matcher;
	private final String pattern;
	private static final String syntax="glob";

	public GlobbingFileFilter(String pattern)
			throws PatternSyntaxException, UnsupportedOperationException {
		checkPatternNull(pattern);

		this.pattern = pattern;
		FileSystem fs = FileSystems.getDefault();
		matcher = fs.getPathMatcher(syntax+":"+ pattern);
	}

	@Override
	public boolean accept(File file) {
		FileUtils.checkFileValidity(file);

//		Only the file in particular folder will be passed to this method.
		Path name = file.toPath().getFileName();
		return matcher.matches(name);
	}

	@Override
	public String toString() {
		return syntax+":"+pattern;
	}

	private static void checkPatternNull(String obj){
		if(obj==null)
			throw new IllegalArgumentException("pattern" +"==null");
	}
}

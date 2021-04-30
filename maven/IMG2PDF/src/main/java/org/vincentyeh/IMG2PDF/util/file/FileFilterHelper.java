package org.vincentyeh.IMG2PDF.util.file;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.regex.PatternSyntaxException;

public class FileFilterHelper implements FileFilter {
	private final String operator;
	private final PathMatcher matcher;

	/**
	 * @param operator filter operator
	 * @throws IllegalArgumentException 
	 * @throws PatternSyntaxException when using regex
	 * @throws UnsupportedOperationException 
	 */
	public FileFilterHelper(String operator)
			throws IllegalArgumentException, PatternSyntaxException, UnsupportedOperationException {
		this.operator = operator;
		FileSystem fs = FileSystems.getDefault();
		matcher = fs.getPathMatcher(operator);

	}

	@Override
	public boolean accept(File file) {
//		Only the file in particular folder will be passed to this method.
		Path name = file.toPath().getFileName();
		return matcher.matches(name);
	}

	public String getOperator() {
		return operator;
	}

}

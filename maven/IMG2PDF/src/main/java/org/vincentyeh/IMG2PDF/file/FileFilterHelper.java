package org.vincentyeh.IMG2PDF.file;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

public class FileFilterHelper implements FileFilter,ArgumentType<FileFilterHelper>{
	private final String regex;

	public FileFilterHelper(String regex){
		this.regex = regex;
	}

	@Override
	public boolean accept(File file) {
		return file.getName().matches(regex);
	}

	@Override
	public FileFilterHelper convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
		String regex = value.replace("${OR}", "|");
		try {
			//Test if regex is valid.
			Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			throw new ArgumentParserException(e,parser);
		}
		
		return new FileFilterHelper(regex);
	}
	
}

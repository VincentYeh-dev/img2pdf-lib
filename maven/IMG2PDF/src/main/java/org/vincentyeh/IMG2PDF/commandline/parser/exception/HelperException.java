package org.vincentyeh.IMG2PDF.commandline.parser.exception;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class HelperException extends ParseException{
	private final Options options;
	public HelperException(Options options) {
		super("help options");
		this.options=options;
	}

	public Options getOptions() {
		return options;
	}
}

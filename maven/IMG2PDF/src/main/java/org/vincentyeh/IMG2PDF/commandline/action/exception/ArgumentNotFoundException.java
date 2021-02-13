package org.vincentyeh.IMG2PDF.commandline.action.exception;

import org.apache.commons.cli.ParseException;

public class ArgumentNotFoundException extends ParseException{

	public ArgumentNotFoundException(String arg) {
		super(arg+" not found.");
	}

}

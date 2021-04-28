package org.vincentyeh.IMG2PDF.commandline.action.exception;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class HelperException extends ParseException{
	public Options opt;
	public HelperException(Options opt) {
		super("help options");
		this.opt=opt;
	}

}

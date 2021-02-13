package org.vincentyeh.IMG2PDF.commandline.action.exception;

import org.apache.commons.cli.ParseException;

public class IllegalParserArgumentException extends ParseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5818347104393060334L;

	public IllegalParserArgumentException(String message) {
		super(message);
	}

}

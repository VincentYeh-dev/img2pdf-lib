package org.vincentyeh.IMG2PDF.commandline.action.exception;

public class ArgumentNotFoundException extends RuntimeException{

	public ArgumentNotFoundException(String arg) {
		super(arg+" not found.");
	}

}

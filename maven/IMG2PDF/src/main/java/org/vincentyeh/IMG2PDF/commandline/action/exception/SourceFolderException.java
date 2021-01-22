package org.vincentyeh.IMG2PDF.commandline.action.exception;

import java.io.File;

public class SourceFolderException extends RuntimeException {
	protected final int line;
	protected final File dir;
	protected final File source_file;

	public SourceFolderException(int line,File dir,File source_file,String message) {
		super(message);
		this.line=line;
		this.dir=dir;
		this.source_file=source_file;
	}

}

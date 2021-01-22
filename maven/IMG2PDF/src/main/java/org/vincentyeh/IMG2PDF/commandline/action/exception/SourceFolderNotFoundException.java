package org.vincentyeh.IMG2PDF.commandline.action.exception;

import java.io.File;

public class SourceFolderNotFoundException extends SourceFolderException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SourceFolderNotFoundException(int line, File dir,File source_file) {
		super(line, dir,source_file,String.format("File not found in %s line:%d->%s",source_file.getName(),line,dir.getAbsolutePath()));
		
	}
	
}

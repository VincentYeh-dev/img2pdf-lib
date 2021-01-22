package org.vincentyeh.IMG2PDF.commandline.action.exception;

import java.io.File;

public class SourceFolderIsFileException extends SourceFolderException {

	public SourceFolderIsFileException(int line, File dir,File source_file) {
		super(line, dir,source_file,String.format("Path in %s line:%d should be a folder.\t%s",source_file.getName(),line,dir.getAbsolutePath()));
		
	}
	
}

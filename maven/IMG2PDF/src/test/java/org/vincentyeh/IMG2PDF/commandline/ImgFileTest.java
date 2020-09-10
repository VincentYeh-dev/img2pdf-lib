package org.vincentyeh.IMG2PDF.commandline;

import java.io.File;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.file.ImgFile;

public class ImgFileTest {
	
	@Test(expected = NullPointerException.class)
	public void testCreateImageFileWithNullArguments() {
		File f=null;
		String str=null;
		new ImgFile(f);
		new ImgFile(str);
		
	}
	
	
}

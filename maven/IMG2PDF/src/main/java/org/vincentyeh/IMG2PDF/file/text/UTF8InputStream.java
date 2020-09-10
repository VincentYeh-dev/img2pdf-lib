package org.vincentyeh.IMG2PDF.file.text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UTF8InputStream  extends InputStream{
	private InputStream is;
	
	public UTF8InputStream(File file) throws IOException {
		if(UTF8NOBOMInputStream.isSameEncodingType(file)) {
			is=new UTF8NOBOMInputStream(file);
		}else if(UTF8BOMInputStream.isSameEncodingType(file)) {
			is=new UTF8BOMInputStream(file);
		}
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return is.read();
	}
	
}

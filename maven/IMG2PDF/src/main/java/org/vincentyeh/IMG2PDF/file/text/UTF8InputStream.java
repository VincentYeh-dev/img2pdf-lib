package org.vincentyeh.IMG2PDF.file.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UTF8InputStream{
	byte[] SYMBLE_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
	
	private InputStream is;
	
	public UTF8InputStream(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		byte[] symble = new byte[3];
		fis.read(symble);
		
		if (bytesCompare(symble,SYMBLE_BOM))
			this.is = fis;
		else
			this.is = new FileInputStream(file);
	}
	
	public InputStream getInputStream() throws IOException {
		return is;
	}
	public void close() throws IOException {
		is.close();
	}
	
	private boolean bytesCompare(byte[] a, byte[] b) {
		if (a.length != b.length)
			return false;

		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i])
				return false;
		}
		return true;
	}
}

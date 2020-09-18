package org.vincentyeh.IMG2PDF.file.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UTF8BOMInputStream extends InputStream {

	byte[] SYMBLE_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
	private FileInputStream fis;
	private final boolean isSameEncodingType;

	public UTF8BOMInputStream(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		byte[] symble = new byte[3];
		fis.read(symble);
		isSameEncodingType = isSameEncodingType(symble);
		if (isSameEncodingType)
			this.fis = fis;
		else
			this.fis = null;

	}

	@Override
	public int read() throws IOException {
		return fis.read();
	}

	boolean bytesCompare(byte[] a, byte[] b) {
		if (a.length != b.length)
			return false;

		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i])
				return false;
		}
		return true;
	}

	private boolean isSameEncodingType(byte[] symble) {
		return bytesCompare(symble, SYMBLE_BOM);
	}

	public static boolean isSameEncodingType(File file) throws IOException {
		UTF8BOMInputStream o=new UTF8BOMInputStream(file);
		o.close();
		return o.isSameEncodingType;
	}
}
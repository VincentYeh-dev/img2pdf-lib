package org.vincentyeh.IMG2PDF.file.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UTF8NOBOMInputStream extends InputStream {

	byte[] SYMBLE_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
	FileInputStream fis;
	final boolean isSameEncodingType;

	public UTF8NOBOMInputStream(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		byte[] symble = new byte[3];
		fis.read(symble);
		isSameEncodingType = !isSameEncodingType(symble);
		if (isSameEncodingType)
			this.fis = fis;
		else
			this.fis = null;
	}

	@Override
	public int read() throws IOException {
		return fis.read();
	}

	void bytesPrint(byte[] b) {
		for (byte a : b)
			System.out.printf("%x ", a);
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

	boolean isSameEncodingType(byte[] symble) {
		return bytesCompare(symble, SYMBLE_BOM);
	}

	public static boolean isSameEncodingType(File file) throws IOException {
		return (new UTF8NOBOMInputStream(file)).isSameEncodingType;
	}
}
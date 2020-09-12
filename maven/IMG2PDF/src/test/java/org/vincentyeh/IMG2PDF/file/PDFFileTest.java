package org.vincentyeh.IMG2PDF.file;

import org.junit.Test;

public class PDFFileTest {
	
	@Test
	public void testPDFFile() {
		PDFFile.Size size=PDFFile.Size.getSizeFromString("A0");
		System.out.println(size.getSize());
		System.out.println(size.getStrSize());
	}
}

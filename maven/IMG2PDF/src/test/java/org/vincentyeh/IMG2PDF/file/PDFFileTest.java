package org.vincentyeh.IMG2PDF.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.file.PDFFile.Align;
import org.vincentyeh.IMG2PDF.file.PDFFile.Size;
import org.vincentyeh.IMG2PDF.file.PDFFile.Size.IllegalSizeException;
import org.vincentyeh.IMG2PDF.task.Task;

public class PDFFileTest {
	@Test
	public void testAlign() {
		PDFFile.Align a=new PDFFile.Align("CENTER|CENTER");
		PDFFile.Align b=new PDFFile.Align("CENTER|LEFT");
		
		System.out.println(a);
		System.out.println(b);
	}
	@Test(expected = IllegalSizeException.class)
	public void testIllegalSize() {
		Size.getSizeFromString("AA");
	}
	
//
//	@Test
//	public void testPDFFile() throws FileNotFoundException {
//		FileFilterHelper ffh=new FileFilterHelper(FileFilterHelper.CONDITION_IS_FILE|FileFilterHelper.CONDITION_EXT_EQUALS);
//		ffh.appendExtSLT("JPG");
//		ffh.appendExtSLT("jpg");
//		ffh.appendExtSLT("PNG");
//		ffh.appendExtSLT("png");
//		
//		File[] f = (new File("test_file\\raw\\TEST\\")).listFiles(ffh);
//
//		Task task = new Task(f, "test.pdf", null, null,Sortby.NAME,Order.INCREASE,
//				(PDFFile.ALIGN_CENTER & 0xF0) | (PDFFile.ALIGN_CENTER & 0x0F), PDFFile.Size.A4);
//		
//		PDFFile pdf=new PDFFile(task);
//		
//		try {
//			pdf.process();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
}

package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileNotFoundException;

import org.jdom2.Element;
import org.junit.Test;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.file.PDFFile;
import org.vincentyeh.IMG2PDF.file.PDFFile.Align;
import org.vincentyeh.IMG2PDF.file.PDFFile.Align.IllegalAlignException;

public class TaskTest {
	@Test(expected = NullPointerException.class)
	public void testCreateTaskWithNulls1() throws Exception {
		String str = "test_file\\raw\\TEST\\";
		new Task(null, null, null, null,Sortby.NAME,Order.INCREASE,new Align("CENTER|CENTER"),PDFFile.Size.A4);
	}
	@Test(expected = NullPointerException.class)
	public void testCreateTaskWithNulls2() throws Exception {
		String str = "test_file\\raw\\TEST\\";
		File[] f=(new File(str)).listFiles();
		
		new Task(f, null, null, null, Sortby.NAME,Order.INCREASE,new Align("CENTER|CENTER"),PDFFile.Size.A4);
	}
	
	@Test
	public void testCreateTaskWithNulls3() throws Exception {
		String str = "test_file\\raw\\TEST\\";
		File[] f=(new File(str)).listFiles();
		new Task(f, "test.pdf", null, null,Sortby.NAME,Order.INCREASE,new Align("CENTER|CENTER"),PDFFile.Size.A4);
	}

	@Test
	public void testCreateTaskWith() throws Exception {
		String str = "test_file\\raw\\TEST\\";
		File[] f=(new File(str)).listFiles();
		new Task(f, "test.pdf", null, null,Sortby.NAME,Order.INCREASE,new Align("CENTER|CENTER"),PDFFile.Size.A4);
	}
	
	@Test
	public void testCreateTaskWithCorrectArgument() throws Exception {
		String str = "test_file\\raw\\TEST\\";
		File[] f=(new File(str)).listFiles();
		new Task(f, "test.pdf", null, null,
				Sortby.NAME,Order.INCREASE,
				new Align("CENTER|CENTER"),PDFFile.Size.A4);
	}
	
	@Test(expected = IllegalAlignException.class)
	public void testCreateTaskWithIllegalArgument() throws FileNotFoundException{
		String str = "test_file\\raw\\TEST\\";
		File[] f=(new File(str)).listFiles();
		new Task(f, "test.pdf", null, null,
				Sortby.NAME,Order.INCREASE,
				new Align("CENTER|AA"),PDFFile.Size.A5);
	}
	
	@Test(expected = NullPointerException.class)
	public void testCreateTaskByElement() {
		Element e=new Element("TASK");
		PDFFile.Size size=PDFFile.Size.getSizeFromString("A4");
//		e.setAttribute("destination", "A");
//		e.setAttribute("size", size.getStrSize());
//		e.setAttribute("align", 15 + "");
//		e.setAttribute("owner", "");
//		e.setAttribute("user", "");
		Task task=new Task(e);
	}
	
}

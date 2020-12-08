package org.vincentyeh.IMG2PDF.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.util.NameFormatter.ParentOverPointException;

/**
 * Test on https://regex101.com/.
 * 
 * @author VincentYeh
 */
public class NameFormatterTester {
	@Test
	public void testName() throws FileNotFoundException {
		File file = new File("src\\test\\resources\\TestFile.txt");
		assertTrue(file.exists());

		NameFormatter formatter = new NameFormatter("$NAME", file);
		assertEquals(formatter.getConverted(), "TestFile");
	}
	@Test
	public void testDate() throws FileNotFoundException {
		File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
		assertTrue(file.exists());

		NameFormatter formatter = new NameFormatter("$DATE", file);
		System.out.println(formatter.getConverted());
		
	}
	

	@Test(expected = ParentOverPointException.class)
	public void testParent() throws FileNotFoundException {
		File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
		assertTrue(file.exists());
		int i = 0;
		File buf = file.getParentFile();
		while (true) {
			NameFormatter formatter = new NameFormatter("$PARENT{" + i + "}", file);
			assertEquals(formatter.getConverted(), buf.getName());
			buf = buf.getParentFile();
			i++;
		}

//		assertEquals(formatter.getConverted(), "TestFile");
	}
}

package org.vincentyeh.IMG2PDF.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;

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

		NameFormatter formatter = new NameFormatter(file);
		assertEquals(formatter.format("$NAME"), "TestFile");
	}
	@Test
	public void testDate() throws FileNotFoundException {
		File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
		assertTrue(file.exists());

		NameFormatter formatter = new NameFormatter(file);
		System.out.println(formatter.format("$Y-$M-$D Hello$NAME $H:$N:$S"));
		
	}
	@Test
	public void testNoSense() throws FileNotFoundException {
		File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
		assertTrue(file.exists());

		NameFormatter formatter = new NameFormatter(file);
		System.out.println(formatter.format("AAAAAAAAA$@$@%#@@"));

	}

	@Test
	public void test() {
		Path p=new File("src\\test\\resources\\TestFile.txt").toPath().toAbsolutePath();
		System.out.println();
		Path root=p.getRoot();
		Path newPath=root.resolve(p.subpath(0,p.getNameCount()-0));
		System.out.println(newPath);
	}

	@Test(expected = ParentOverPointException.class)
	public void testParent() throws FileNotFoundException {
		File file = new File("src\\test\\resources\\TestFile.txt").getAbsoluteFile();
		assertTrue(file.exists());
		int i = 0;
		File buf = file.getParentFile();
		while (true) {
			String format="$PARENT{" + i + "}";
			NameFormatter formatter = new NameFormatter(file);
			String result=formatter.format(format);
			System.out.printf("%s-->%s\n",format,result);
			
			if(buf==null)break;
			String real=buf.getName();
			assertEquals(result,real);
			buf = buf.getParentFile();
			i++;
		}
	}
}

package org.vincentyeh.IMG2PDF.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.file.ImgFile;

public class ImgFileTest {

	@Test(expected = NullPointerException.class)
	public void testCreateImageFileWithNullArguments() {
		File f = null;
		String str = null;
		try {
			new ImgFile(str);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Test Result: It's stabler to remove ImgFile constructor which contain File
	 * argument. If ImgFile is created like this.-> new ImgFile(new File("")); No
	 * exception will be thrown because file.getAbsolutePath() is not empty.
	 *
	 * Improved?? yes(VincentYeh)
	 *
	 */

	@Test(expected = RuntimeException.class)
	public void testCreateImageFileWithEmptyArgument() {
		String str = "";
		try {
			new ImgFile(str);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test(expected = RuntimeException.class)
	public void testCreateImageFileWithEmptyArguments() {
		String str = "";
		try {
			new ImgFile(str, -1, -1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test(expected = FileNotFoundException.class)
	public void testCreateImageFileWithNotExistsArgument() throws FileNotFoundException {
		String str = "AA";
		new ImgFile(str);

	}

	@Test(expected = FileNotFoundException.class)
	public void testCreateImageFileWithNotExistsArguments() throws FileNotFoundException {
		String str = "AA";
		new ImgFile(str, -1, -1);

	}
	
	@Test
	public void testCreateImageFileWithIllegalArgument() throws FileNotFoundException{
		String str = "test_file\\raw\\TEST\\01.jpg";
		int i=-2;
		while(i!=-255) {
			try {
				new ImgFile(str,i,ImgFile.ORDER_INCREASE);
				assertTrue(false);
			} catch (IllegalArgumentException e) {
				
			}
			i--;
		}
		i=2;
		while(i!=255) {
			try {
				new ImgFile(str,i,ImgFile.ORDER_INCREASE);
				assertTrue(false);
			} catch (IllegalArgumentException e) {
				
			}
			i++;
		}
		i=-2;
		while(i!=-255) {
			try {
				new ImgFile(str,ImgFile.SORTBY_NAME,i);
				assertTrue(false);
			} catch (IllegalArgumentException e) {
				
			}
			i--;
		}
		i=2;
		while(i!=255) {
			try {
				new ImgFile(str,ImgFile.SORTBY_NAME,i);
				assertTrue(false);
			} catch (IllegalArgumentException e) {
				
			}
			i++;
		}
	}

	/**
	 * Test Result:
	 * order and sort in ImgFile need to be final.
	 * 
	 * Improve?		yes(VincentYeh)
	 * 
	 * Swap Constructor of ImgFile
	 * 
	 * @throws FileNotFoundException
	 */
	@Test(expected = RuntimeException.class)
	public void testCreateImageFileWithDirectoryArgument() throws FileNotFoundException {
		String str = "test_file";
		new ImgFile(str);

	}

	@Test(expected = RuntimeException.class)
	public void testCreateImageFileWithDirectoryArguments() throws FileNotFoundException {
		String str = "test_file";
		new ImgFile(str, -1, -1);

	}

	@Test
	public void testCreateImageWithArgumentsAndSort() throws FileNotFoundException {
		String str = "test_file\\raw\\TEST\\01.jpg";
		ImgFile a = new ImgFile(str, -1, -1);
		ArrayList<ImgFile> list = new ArrayList<ImgFile>();
		list.add(a);
		list.add(a);
		try {
			Collections.sort(list);
		} catch (RuntimeException e) {
			assertEquals(e.getMessage(), "Multiple files need to be sorted by sort and order arguments.");
		}
		
	}

	@Test
	public void testCreateImageWithCorrectArgumentsAndSort() throws FileNotFoundException {
		String str = "test_file\\raw\\TEST\\01.jpg";
		ImgFile a = new ImgFile(str, ImgFile.SORTBY_NAME, ImgFile.ORDER_INCREASE);
		ArrayList<ImgFile> list = new ArrayList<ImgFile>();
		list.add(a);
		list.add(a);
		Collections.sort(list);
	}
	
	@Test(expected = NullPointerException.class)
	public void rotateImgWithNullImg() {
		ImgFile.rotateImg(null, 10);
	}
	
	
	@Test
	public void rotateImg() throws IOException {
		for(int i=0;i<=360;i++) {
			String str = "test_file\\raw\\TEST\\02.jpg";
			ImgFile.rotateImg(ImageIO.read(new File(str)), i);
		}
	}

}

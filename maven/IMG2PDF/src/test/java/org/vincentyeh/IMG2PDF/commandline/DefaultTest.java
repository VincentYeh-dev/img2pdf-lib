package org.vincentyeh.IMG2PDF.commandline;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;

public class DefaultTest {
	@Test
	public void FindFileTest() {
//		normal regex for filename:[^<>:;,?"*|\/\\]+\.(png|PNG|jpg|JPG)
		
		File file =new File("pom.png");
		String regex="[^<>;,?\"*|\\/]+\\.(png|PNG|jpg|JPG)";
		System.out.println(regex);
		String a=file.getAbsolutePath();
		System.out.println(a);
		System.out.println(a.matches(regex));
		
	}
	
}

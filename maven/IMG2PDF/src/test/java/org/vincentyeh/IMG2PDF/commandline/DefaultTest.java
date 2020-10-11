package org.vincentyeh.IMG2PDF.commandline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;

public class DefaultTest {
	
	@Test
	public void RTest() {
		
		String ThisStr="AA1";
		String OStr="AA11";
		
		String noNumThisStr = ThisStr.replaceAll("[0-9]", "*");
		String noNumOStr = OStr.replaceAll("[0-9]", "*");
		System.out.println(noNumThisStr);
		System.out.println(noNumOStr);

		System.out.println();
		
		String noMulStarThisStr = noNumThisStr.replaceAll("\\*{1,}", "*");
		String noMulStarOStr = noNumOStr.replaceAll("\\*{1,}", "*");

		System.out.println(noMulStarThisStr);
		System.out.println(noMulStarOStr);
		
		if (!noMulStarThisStr.equals(noMulStarOStr)) {
			
		}else {
			ThisStr.substring(noNumThisStr.indexOf("*"),
					noNumThisStr.indexOf("*"));
		}
	}
}

package org.vincentyeh.IMG2PDF.commandline.action;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.commandline.MainProgram;

public class CreateActionTester {

	@Test
	public void testNotExist() {

		String filter = ".*\\.(png$VBARPNG$VBARjpg$VBARJPG)";
		String create_command = "create " + "-pz A4 " + "-ps NUMERTIC " + "-pa CENTER-CENTER " + "-pdi Vertical "
				+ "-par yes " + "-po INCREASE " + "-pdst " + "\"src\\test\\resources\\dirlist_NotExist.pdf\" "
				+ "-ldst " + "\"src\\test\\resources\\dirlist_NotExist.xml\"" + " import -s \""
				+ "src\\test\\resources\\dirlist_NotExist.txt" + "\" -f " + filter;

		String convert_command = "convert " + "\"src\\test\\resources\\dirlist_NotExist.xml\"";
		System.out.println(create_command);
//		System.out.println(convert_command);
		System.out.println();
		MainProgram.main(create_command);
//		MainProgram.main(convert_command);
	}

	@Test
	public void testNotAFolder() {

		String filter = ".*\\.(png$VBARPNG$VBARjpg$VBARJPG)";
		String create_command = "create " + "-pz A4 " + "-ps NUMERTIC " + "-pa CENTER-CENTER " + "-pdi Vertical "
				+ "-par yes " + "-po INCREASE " + "-pdst " + "\"src\\test\\resources\\dirlist_File.pdf\" "
				+ "-ldst " + "\"src\\test\\resources\\dirlist_File.xml\"" + " import -s \""
				+ "src\\test\\resources\\dirlist_File.txt" + "\" -f " + filter;

		String convert_command = "convert " + "\"src\\test\\resources\\dirlist_File.xml\"";
		System.out.println(create_command);
//		System.out.println(convert_command);
		System.out.println();
		MainProgram.main(create_command);
//		MainProgram.main(convert_command);
	}
}

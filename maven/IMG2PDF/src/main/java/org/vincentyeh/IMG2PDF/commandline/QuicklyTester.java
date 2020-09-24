package org.vincentyeh.IMG2PDF.commandline;

public class QuicklyTester {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String str="-sz A4 "
				+ "-s NAME "
				+ "-a CENTER|CENTER "
				+ "-odr INCREASE "
				+ "-lo test_file\\test.xml "
				+ "-d test_file\\output\\$NAME.pdf "
//				+ "-usepwd 1234AAA "
//				+ "-ownpwd 1234AAA "
				+ "test_file\\dirlist_cmyk.txt";
		String[] help=new String[] {"-h"};
		System.out.println(str);
		TaskListCreator.main(str.trim().split("\\s"));
//		TaskListCreator.main(help);
		Program.main(new String[] {"test_file\\test.xml"});
	}

}

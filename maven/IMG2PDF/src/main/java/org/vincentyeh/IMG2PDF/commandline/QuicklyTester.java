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
				+ "test_file\\dirlist.txt";
		System.out.println(str);
		TaskListCreator.main(str.trim().split("\\s"));
		Program.main(new String[] {"test_file\\test.xml"});
	}

}

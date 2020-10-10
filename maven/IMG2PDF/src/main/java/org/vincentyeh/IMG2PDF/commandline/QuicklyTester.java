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
				+ "test_file\\dirlist_cmyk.txt "
				+ "test_file\\dirlist_mul.txt";
		
//		TaskListCreator creator=new TaskListCreator(str);
//		new TaskProcessor(creator.getTaskList());

		new TaskListCreator(str);
		new TaskProcessor("test_file\\test.xml");
		
	}

}

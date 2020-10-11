package org.vincentyeh.IMG2PDF.commandline;

public class QuicklyTester {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String str="-sz A4 "
				+ "-s NUMERTIC "
				+ "-a CENTER|CENTER "
				+ "-odr INCREASE "
				+ "-lo test_file\\test.xml "
				+ "-d D:\\Project\\git\\IMG2PDF\\sample\\walk-animation\\output\\$PARENT{0}.pdf "
//				+ "-usepwd 1234AAA "
//				+ "-ownpwd 1234AAA "
//				+ "test_file\\dirlist_mul.txt "
				+ "D:\\Project\\git\\IMG2PDF\\sample\\walk-animation\\dirlist.txt";
		
//		TaskListCreator creator=new TaskListCreator(str);
//		new TaskProcessor(creator.getTaskList());

		new TaskListCreator(str);
		new TaskProcessor("test_file\\test.xml");
		
	}

}

package org.vincentyeh.IMG2PDF.sample;

import java.io.File;

import org.vincentyeh.IMG2PDF.commandline.TaskListCreator;
import org.vincentyeh.IMG2PDF.commandline.TaskProcessor;

public class WalkAnimation {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		new TaskListCreator("-h");
//		System.exit(0);
		File project_root=new File("").getAbsoluteFile().getParentFile().getParentFile();
		File sample_root=new File(project_root, "sample\\walk-animation");
		
		File taskslist_destination=new File(sample_root, "taskslist\\test.xml");
		File sources=new File(sample_root, "dirlist.txt");;
		
		String str="-sz A4 "
				+ "-s NUMERTIC "
				+ "-a CENTER|CENTER "
				+ "-odr INCREASE "
				+ "-lo "+taskslist_destination.getAbsolutePath()+" "
				+ "-d "+sample_root.getAbsolutePath()+"\\output\\$PARENT{0}.pdf "
//				+ "-usepwd 1234AAA "
//				+ "-ownpwd 1234AAA "
				+ sources.getAbsolutePath();
		new TaskListCreator(str);
		new TaskProcessor(taskslist_destination.getAbsolutePath());
		
	}

}

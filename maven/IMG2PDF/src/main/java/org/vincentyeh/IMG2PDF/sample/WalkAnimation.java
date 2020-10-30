package org.vincentyeh.IMG2PDF.sample;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.vincentyeh.IMG2PDF.commandline.TaskListCreator;
import org.vincentyeh.IMG2PDF.commandline.TaskProcessor;

public class WalkAnimation {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		File project_root=new File("").getAbsoluteFile().getParentFile().getParentFile();
		File sample_root=new File(project_root, "sample\\walk-animation");
		File taskslist_destination=new File(sample_root, "taskslist\\test.xml");
		File image_sources_dir=new File(sample_root, "image-sources").getAbsoluteFile();
		
		File sources_list=new File(sample_root, "dirlist.txt").getAbsoluteFile();
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sources_list), "UTF-8"));
		writer.write(image_sources_dir.getAbsolutePath()+"\n\n");
		writer.close();
		
		

//		new TaskListCreator("-h");
		
		String str="-sz A4 "
				+ "-s NUMERTIC "
				+ "-a CENTER|CENTER "
				+ "-odr INCREASE "
				+ "-lo "+taskslist_destination.getAbsolutePath()+" "
				+ "-d "+sample_root.getAbsolutePath()+"\\output\\$PARENT{0}.pdf "
				+ "-dd Vertical "
				+ "-rot true "
//				+ "-p pm "
//				+ "-usepwd userpwd "
//				+ "-ownpwd ownerpwd "
				+ sources_list.getAbsolutePath();
		new TaskListCreator(str);
		new TaskProcessor(taskslist_destination.getAbsolutePath());
		
	}

}

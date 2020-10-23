package org.vincentyeh.IMG2PDF.sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.parsers.ParserConfigurationException;

import org.vincentyeh.IMG2PDF.commandline.TaskListCreator;
import org.vincentyeh.IMG2PDF.commandline.TaskProcessor;
import org.xml.sax.SAXException;

public class kuma_fish_album_1 {

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		
		File project_root=new File("").getAbsoluteFile().getParentFile().getParentFile();
		File sample_root=new File(project_root, "sample\\kuma_fish_album_1");
		File taskslist_destination=new File(sample_root, "taskslist\\test.xml");
		File image_sources_dir=new File(sample_root, "image-sources").getAbsoluteFile();
		
		File sources_list=new File(sample_root, "dirlist.txt").getAbsoluteFile();
		
//		Create dirlist.txt which contain path of images directory.
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sources_list), "UTF-8"));
		writer.write(image_sources_dir.getAbsolutePath()+"\n\n");
		writer.close();
		
		String str="-sz A4 "
				+ "-s NUMERTIC "
				+ "-a CENTER|CENTER "
				+ "-odr INCREASE "
				+ "-lo "+taskslist_destination.getAbsolutePath()+" "
				+ "-d "+sample_root.getAbsolutePath()+"\\output\\$PARENT{0}.pdf "
//				+ "-usepwd 1234AAA "
//				+ "-ownpwd 1234AAA "
				+ sources_list.getAbsolutePath();
		new TaskListCreator(str);
		new TaskProcessor(taskslist_destination.getAbsolutePath());
		
	}

}

package org.vincentyeh.IMG2PDF.sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class kuma_fish_album_1 {

	public static void main(String[] args) {

		File project_root = new File("").getAbsoluteFile().getParentFile().getParentFile();
		File sample_root = new File(project_root, "sample\\kuma_fish_album_1");
		File taskslist_destination = new File(sample_root, "taskslist\\test.xml");
		taskslist_destination.delete();

		File image_sources_dir = new File(sample_root, "image-sources").getAbsoluteFile();

		File sources_list = new File(sample_root, "dirlist.txt").getAbsoluteFile();

//		Create dirlist.txt which contain path of images directory.
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sources_list), "UTF-8"));
			writer.write(image_sources_dir.getAbsolutePath() + "\n\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String create_command = "-m create "
//				
				+ "-pz A4 "
//				
				+ "-ps NUMERTIC "
//				
				+ "-pa CENTER-CENTER "
//				
				+ "-pdi Vertical "
//				
				+ "-par YES "
//				
				+ "-po INCREASE "
//				
				+ "-pupwd 1234AAA "
//				
				+ "-popwd 1234AAA "
//				
				+ "-pp 11 "
//				
				+ "-f \"glob:*.{PNG,JPG}\" "
//				
				+ "-pdst \"" + sample_root.getAbsolutePath() + "\\output\\$PARENT{0}.pdf\" "
//				
				+ "-ldst \"" + taskslist_destination.getAbsolutePath() + "\" -src \""
//				
				+ sources_list.getAbsolutePath() + "\"";

		String convert_command = "-m convert -o YES " + taskslist_destination.getAbsolutePath();
		System.out.println("create command:");
		System.out.println(create_command);

		System.out.println("convert command:");
		System.out.println(convert_command);

	}

}

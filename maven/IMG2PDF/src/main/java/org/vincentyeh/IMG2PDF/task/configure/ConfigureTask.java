package org.vincentyeh.IMG2PDF.task.configure;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;

import org.jdom2.Element;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.pdf.Align;
import org.vincentyeh.IMG2PDF.pdf.Size;
import org.vincentyeh.IMG2PDF.task.Task;

public class ConfigureTask extends Task {
	public ConfigureTask(File[] files, String destination, String own, String user, Sortby sortby, Order order, Align align,
			Size size) throws FileNotFoundException {
		super(destination, own, user, align, size);
		
		for (File file : files) {
			ImgFile img = new ImgFile(file.getAbsolutePath(), sortby, order);
			imgs.add(img);
		}
		System.out.printf("found %d files\n", imgs.size());
		System.out.print("Sortting files...");
		Collections.sort(imgs);
		System.out.print("DONE\n\n");
		// TODO Auto-generated constructor stub
	}
	
	public Element toElement() {
		Element task=new Element("TASK");
		Element xml_files = new Element("FILES");
		for (ImgFile img : imgs) {
			Element xml_file = new Element("FILE");
			xml_file.addContent(img.getAbsolutePath());
			xml_files.addContent(xml_file);
		}
		task.addContent(xml_files);
		
		task.setAttribute("destination", destination);
		task.setAttribute("size", size.getStr() + "");
		task.setAttribute("align",align.toString());
		task.setAttribute("owner", owner_pwd);
		task.setAttribute("user", user_pwd);
		return task;
	}
	
}

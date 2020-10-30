package org.vincentyeh.IMG2PDF.task.configure;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.jdom2.Element;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.pdf.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.Size;
import org.vincentyeh.IMG2PDF.task.Task;

public class ConfigureTask extends Task {
	
	private final String owner_pwd;
	private final String user_pwd;
	private final DocumentAccessPermission dap;
	public ConfigureTask(File[] files, String destination, String own, String user,DocumentAccessPermission dap, Sortby sortby, Order order,
			Align align, Size size, PageDirection defaultDirection, boolean autoRotate) throws FileNotFoundException {
		super(destination, align, size, defaultDirection, autoRotate);
		this.dap=dap;
		owner_pwd = own != null ? own : "#null";
		user_pwd = user != null ? user : "#null";

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
		task.setAttribute("default-direction",defaultDirection.toString());
		task.setAttribute("auto-rotate",String.valueOf(autoRotate));
		task.addContent(dap.toElement());
		
//		PageDirection.getDirectionFromString(element.getAttributeValue("default-direction")),
//		Boolean.valueOf(element.getAttributeValue("auto-rotate")));
		
		return task;
	}

}

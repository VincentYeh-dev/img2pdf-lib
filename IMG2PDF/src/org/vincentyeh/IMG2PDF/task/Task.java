package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.util.NameFormatter;

public class Task {

	final boolean exists;
	final boolean isfile;

	private int size, align;
	private boolean merge = true;
	private final String destination;
	private final String owner_pwd;
	private final String user_pwd;
	private File file;
	ArrayList<ImgFile> imgs;

	public Task(String str_file, String dst, String own, String user, int sortby, int order, int align, int size) throws Exception {
		this.align = align;
		this.size = size;
		file = new File(str_file);
		NameFormatter nf = new NameFormatter(dst, file);
		destination = nf.getConverted();

		exists = file.exists();
		if (!exists) {
			throw new FileNotFoundException(file.getAbsolutePath() + " not found.");
		}

		owner_pwd = own != null ? own : "#null";
		user_pwd = user != null ? user : "#null";
		isfile = file.isFile();
			if (isfile) {
				imgs=new ArrayList<ImgFile>();
				String[] buf=file.getName().split("\\.");
				FileFilterHelper ffh = createImageFilter(FileFilterHelper.CONDITION_NAME_EQUALS);
				
				ffh.appendNameSLT(buf[0]);
				ffh.appendExtSLT(buf[buf.length-1]);
				
				File[] files =file.getParentFile().listFiles(ffh);
				
				imgs.add(new ImgFile(files[0], sortby, order));
				
			} else {
				imgs = dir2imgs(file, sortby, order);
			}
			System.out.printf("\nfound %d files.\n", imgs.size());
			System.out.print("Sortting files...");
			Collections.sort(imgs);
			System.out.print("DONE\n\n");

	}

	public Task(Element xml_task) {

		destination = xml_task.getAttributeValue("destination");
		file = new File(xml_task.getChild("FILES").getAttributeValue("source"));
		exists = file.exists();
		isfile = file.isFile();

		ArrayList<Element> xml_files = new ArrayList<Element>(xml_task.getChild("FILES").getChildren("FILE"));
		align = Integer.valueOf(xml_task.getAttributeValue("align"));
		imgs = xml2imgs(xml_files);

		owner_pwd = xml_task.getAttributeValue("owner");
		user_pwd = xml_task.getAttributeValue("user");

	}

	public Element toXMLTask(){

		Element task = new Element("TASK");
		task.setAttribute("merge", merge ? "1" : "0");
		task.setAttribute("destination", destination);
		task.setAttribute("size", size + "");
		task.setAttribute("align", align + "");
		task.setAttribute("owner", owner_pwd);
		task.setAttribute("user", user_pwd);

		Element xml_files = new Element("FILES");
		xml_files.setAttribute("source", file.getAbsolutePath());
		for (int i = 0; i < imgs.size(); i++) {
			Element file = new Element("FILE");
			file.setAttribute("index", i + "");
			file.addContent(imgs.get(i).file.getAbsolutePath());
			xml_files.addContent(file);
		}

		task.addContent(xml_files);
		return task;
	}

	ArrayList<ImgFile> dir2imgs(File dir, int sortby, int order) throws Exception {
		FileFilterHelper ffh = createImageFilter(0);
		File[] files = dir.listFiles(ffh);
		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		for (File file : files) {
			ImgFile img = new ImgFile(file, sortby, order);
			imgs.add(img);
		}
		return imgs;
	}

	ArrayList<ImgFile> xml2imgs(ArrayList<Element> el_files) {
		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		for (Element el : el_files) {
			try {
				File f_img = new File(el.getValue());
				ImgFile img = new ImgFile(f_img, -1, -1);
				imgs.add(img);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return imgs;
	}


	public void setAlign(int align) {
		this.align = align;
	}

	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getAlign() {
		return align;
	}

	public String getDestination() {
		return destination;
	}

	public ArrayList<ImgFile> getImgs() {
		return imgs;
	}

	public int getSize() {
		return size;
	}

	public String getOwner_pwd() {
		return owner_pwd;
	}

	public String getUser_pwd() {
		return user_pwd;
	}

	FileFilterHelper createImageFilter(int condition) {
		FileFilterHelper ffh = new FileFilterHelper(
				condition | FileFilterHelper.CONDITION_IS_FILE | FileFilterHelper.CONDITION_EXT_EQUALS);
		ffh.appendExtSLT("JPG");
		ffh.appendExtSLT("jpg");
		ffh.appendExtSLT("PNG");
		ffh.appendExtSLT("png");
		return ffh;
	}

}

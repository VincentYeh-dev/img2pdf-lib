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
	private String error = "#null";

	public Task(String str_file, String dst, String own, String user, int sortby, int order, int align, int size) {
		this.align = align;
		this.size = size;
		file = new File(str_file);
		NameFormatter nf=new NameFormatter(dst, file);
		destination = nf.getConverted();
		
		exists = file.exists();
		if (!exists) {
			error = file.getAbsolutePath() + " not found.";
		}

		owner_pwd = own != null ? own : "#null";
		user_pwd = user != null ? user : "#null";
		isfile = file.isFile();

		try {
			imgs = dir2imgs(file, sortby, order);
			System.out.printf("\nfound %d files.\n", imgs.size());
			System.out.print("Sortting files...");
			Collections.sort(imgs);
			System.out.print("DONE\n\n");
		} catch (Exception e) {
			e.printStackTrace();
			error = e.getMessage();
		}

	}

	public boolean isError() {
		return !error.equals("#null");
	}

	public void setError(String error) {
		this.error = error;
	}

	public Task(Element el_task) {

		destination = el_task.getAttributeValue("destination");
		file = new File(el_task.getChild("FILES").getAttributeValue("source"));
		exists = file.exists();
		isfile = file.isFile();

		ArrayList<Element> files = new ArrayList<Element>(el_task.getChild("FILES").getChildren("FILE"));
		align = Integer.valueOf(el_task.getAttributeValue("align"));
		imgs = xml2imgs(files);

		owner_pwd = el_task.getAttributeValue("owner");
		user_pwd = el_task.getAttributeValue("user");

	}

	public Element toXMLTask() throws IOException {

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
		FileFilterHelper ffh = new FileFilterHelper(
				FileFilterHelper.CONDITION_IS_FILE | FileFilterHelper.CONDITION_EXT_EQUALS);
		ffh.appendExtSLT("JPG");
		ffh.appendExtSLT("jpg");
		ffh.appendExtSLT("PNG");
		ffh.appendExtSLT("png");
		File[] files = dir.listFiles(ffh);

		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		for (File file : files) {
			ImgFile img = new ImgFile(file, sortby, order, false);
			imgs.add(img);
		}
		return imgs;
	}

	ArrayList<ImgFile> xml2imgs(ArrayList<Element> el_files) {
		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		for (Element el : el_files) {
			try {
				File f_img = new File(el.getValue());
				ImgFile img = new ImgFile(f_img, -1, -1, false);
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

}

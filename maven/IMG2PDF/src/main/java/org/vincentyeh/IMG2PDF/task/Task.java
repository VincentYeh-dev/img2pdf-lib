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
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.PDFFile;
import org.vincentyeh.IMG2PDF.util.NameFormatter;

/**
 * 
 * @author VincentYeh
 *
 */
public class Task extends Element {
	private int size, align;
	private final String destination;
	private final String owner_pwd;
	private final String user_pwd;
	private ArrayList<ImgFile> imgs;

	/**
	 * 
	 * @param files       Source files.Only can handle files.
	 * @param destination The destination of PDF output
	 * @param own         owner password
	 * @param user        user password
	 * @param sortby      files will be sorted by Name or Date
	 * @param order       order by increase or decrease value
	 * @param align       Where should Images of PDF be located on PDF.
	 * @param size        Which size of pages of PDF.
	 * @throws FileNotFoundException 
	 */
	public Task(File[] files, String destination, String own, String user, int sortby, int order, int align, int size) throws FileNotFoundException{
		super("TASK");

		if (files == null)
			throw new NullPointerException("files is null.");

		if (destination == null)
			throw new NullPointerException("destination is null.");

		if (!(size >= 0x01 && size <= 0x0A))
			throw new IllegalArgumentException("Size value need to be between 0x01 and 0x0A");

		if (size != PDFFile.SIZE_DEPEND_ON_IMG)
			if (!((align & 0xF0) >> 4 > 0 && (align & 0xF0) >> 4 <= 4))
				throw new IllegalArgumentException("align value need to be between 0x01 and 0x44");
			else if (!((align & 0x0F) > 0 && (align & 0x0F) <= 4))
				throw new IllegalArgumentException("align value need to be between 0x01 and 0x44");

		this.align = align;
		this.size = size;
		this.destination = destination;

		Element xml_files = new Element("FILES");
		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		for (File file : files) {
			ImgFile img = new ImgFile(file.getAbsolutePath(), sortby, order);
			imgs.add(img);

			Element xml_file = new Element("FILE");
			xml_file.addContent(img.getAbsolutePath());
			xml_files.addContent(xml_file);
		}
		super.addContent(xml_files);

		owner_pwd = own != null ? own : "#null";
		user_pwd = user != null ? user : "#null";

		super.setAttribute("destination", destination);
		super.setAttribute("size", size + "");
		super.setAttribute("align", align + "");
		super.setAttribute("owner", owner_pwd);
		super.setAttribute("user", user_pwd);
		System.out.printf("\nfound %d files.\n", imgs.size());
		System.out.print("Sortting files...");
		Collections.sort(imgs);
		System.out.print("DONE\n\n");

	}

	public Task(Element xml_task) {
		super("TASK");
//		detach():Remove parent connection
		Element elemCopy = xml_task.clone().detach();
		List<Attribute> atr_list = elemCopy.getAttributes();

		for (Content c : elemCopy.getContent()) {
			super.addContent(c.clone().detach());
		}

		for (Attribute ar : atr_list) {
			super.setAttribute(ar.clone().detach());
		}

		destination = super.getAttributeValue("destination");
		ArrayList<Element> xml_files = new ArrayList<Element>(super.getChild("FILES").getChildren("FILE"));
		align = Integer.valueOf(super.getAttributeValue("align"));
		imgs = xml2imgs(xml_files);

		owner_pwd = super.getAttributeValue("owner");
		user_pwd = super.getAttributeValue("user");

	}

	ArrayList<ImgFile> xml2imgs(ArrayList<Element> el_files) {
		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		for (Element el : el_files) {
			try {
				File f_img = new File(el.getValue());
				ImgFile img = new ImgFile(f_img.getAbsolutePath());
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

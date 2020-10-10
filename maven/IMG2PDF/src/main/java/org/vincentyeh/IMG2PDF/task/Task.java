package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.file.PDFFile;
import org.vincentyeh.IMG2PDF.file.PDFFile.Align;
import org.vincentyeh.IMG2PDF.file.PDFFile.Size;

/**
 * <b>Task is the pre-work of the conversion</b>. All attributes of PDF file
 * will be define in this step. So the program that convert the images to PDF
 * doesn't need to do pre-work of conversion.
 * 
 * The function of Task:
 * <ol>
 * <li>define how to sort files</li>
 * <li>define password of PDF</li>
 * <li>compute the name of destination file</li>
 * <li>convert itself to XML element</li>
 * </ol>
 * 
 * @see Element
 * 
 * @author VincentYeh
 */
public class Task {
	/**
	 * 
	 */
	private final Align align;
	private final Size size;
	private final String destination;
	private final String owner_pwd;
	private final String user_pwd;
	private final ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();

	/**
	 * Create the task by arguments.
	 * 
	 * @param files       Source files.Only can handle files.
	 * @param destination The destination of PDF output
	 * @param own         owner password
	 * @param user        user password
	 * @param sortby      files will be sorted by Name or Date
	 * @param order       order by increase or decrease value
	 * @param align       Where should Images of PDF be located on PDF.
	 * @param size        Which size of pages of PDF.
	 * @throws FileNotFoundException When file is not exists.
	 */
	public Task(File[] files, String destination, String own, String user, Sortby sortby, Order order,Align align, PDFFile.Size size) throws FileNotFoundException {
		if (files == null)
			throw new NullPointerException("files is null.");

		if (destination == null)
			throw new NullPointerException("destination is null.");
		this.align=align;

		this.size = size;
		this.destination = destination;

		for (File file : files) {
			ImgFile img = new ImgFile(file.getAbsolutePath(), sortby, order);
			imgs.add(img);
		}

//		imgs need to be sort before written to element
		Collections.sort(imgs);

		owner_pwd = own != null ? own : "#null";
		user_pwd = user != null ? user : "#null";
		
		System.out.printf("found %d files\n", imgs.size());
		System.out.print("Sortting files...");

		System.out.print("DONE\n\n");

	}

	/**
	 * Create the task by XML Element.The task will inherit element attributes and
	 * contains.
	 * 
	 * @param element The XML Element That include information of Task.
	 */
	public Task(Element element) {
//		detach():Remove parent connection

		String attr_destination = element.getAttributeValue("destination");
		String attr_align = element.getAttributeValue("align");
		String attr_size = element.getAttributeValue("size");
		String attr_owner_pwd = element.getAttributeValue("owner");
		String attr_user_pwd = element.getAttributeValue("user");
		String elm_align = element.getAttributeValue("align");

		if (attr_destination == null)
			throw new NullPointerException("destination is null");
		if (attr_align == null)
			throw new NullPointerException("align is null");
		if (attr_size == null)
			throw new NullPointerException("size is null");
		if (attr_owner_pwd == null)
			throw new NullPointerException("owner_pwd is null");
		if (attr_user_pwd == null)
			throw new NullPointerException("user_pwd is null");
		List<Element> contains_files = element.getChild("FILES").getChildren("FILE");

		if (contains_files == null)
			throw new NullPointerException("files is null");

		destination = attr_destination;

		align=new Align(elm_align);
		
		size = Size.getSizeFromString(element.getAttributeValue("size"));
		owner_pwd = element.getAttributeValue("owner");
		user_pwd = element.getAttributeValue("user");

		ArrayList<Element> xml_files = new ArrayList<Element>(contains_files);
		imgs.addAll(elements2imgs(xml_files));

	}

	private ArrayList<ImgFile> elements2imgs(ArrayList<Element> el_files) {
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
	
	public Align getAlign() {
		return align;
	}

	public String getDestination() {
		return destination;
	}

	public ArrayList<ImgFile> getImgs() {
		return imgs;
	}

	public PDFFile.Size getSize() {
		return size;
	}

	public String getOwner_pwd() {
		return owner_pwd;
	}

	public String getUser_pwd() {
		return user_pwd;
	}
	
	
}

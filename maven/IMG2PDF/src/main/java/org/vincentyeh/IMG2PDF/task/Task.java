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
public class Task extends Element {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7732049453237587003L;

//	private final int align;
	private final Align align;
//	private final LeftRightAlign LRA;
//	private final TopBottomAlign TBA;

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
		super("TASK");
		if (files == null)
			throw new NullPointerException("files is null.");

		if (destination == null)
			throw new NullPointerException("destination is null.");
		this.align=align;

		this.size = size;
		this.destination = destination;

		Element xml_files = new Element("FILES");
		for (File file : files) {
			ImgFile img = new ImgFile(file.getAbsolutePath(), sortby, order);
			imgs.add(img);
		}

//		imgs need to be sort before written to element
		Collections.sort(imgs);

		for (ImgFile img : imgs) {
			Element xml_file = new Element("FILE");
			xml_file.addContent(img.getAbsolutePath());
			xml_files.addContent(xml_file);
		}

		super.addContent(xml_files);

		owner_pwd = own != null ? own : "#null";
		user_pwd = user != null ? user : "#null";

		super.setAttribute("destination", destination);
		super.setAttribute("size", size.getStr() + "");
		super.setAttribute("align",align.toString());
		super.setAttribute("owner", owner_pwd);
		super.setAttribute("user", user_pwd);
		System.out.printf("\nfound %d files.\n", imgs.size());
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
		super("TASK");
//		detach():Remove parent connection
		Element elemCopy = element.clone().detach();
		List<Attribute> atr_list = elemCopy.getAttributes();

		for (Content c : elemCopy.getContent()) {
			super.addContent(c.clone().detach());
		}

		for (Attribute ar : atr_list) {
			super.setAttribute(ar.clone().detach());
		}

		String attr_destination = super.getAttributeValue("destination");
		String attr_align = super.getAttributeValue("align");
		String attr_size = super.getAttributeValue("size");
		String attr_owner_pwd = super.getAttributeValue("owner");
		String attr_user_pwd = super.getAttributeValue("user");
		String elm_align = super.getAttributeValue("align");

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
		List<Element> contains_files = super.getChild("FILES").getChildren("FILE");

		if (contains_files == null)
			throw new NullPointerException("files is null");

		destination = attr_destination;

		align=new Align(elm_align);
		
		size = Size.getSizeFromString(super.getAttributeValue("size"));
		owner_pwd = super.getAttributeValue("owner");
		user_pwd = super.getAttributeValue("user");

		ArrayList<Element> xml_files = new ArrayList<Element>(contains_files);
		imgs.addAll(elements2imgs(xml_files));

	}

	ArrayList<ImgFile> elements2imgs(ArrayList<Element> el_files) {
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

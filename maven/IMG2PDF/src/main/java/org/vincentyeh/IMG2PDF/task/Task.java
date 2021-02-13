package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.jdom2.Element;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.pdf.document.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;

public class Task {
	private PageAlign align;
	private PageSize size;
	private String destination;
	private ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
	private PageDirection defaultDirection;
	private boolean autoRotate;

	private final DocumentAccessPermission dap;
	private final StandardProtectionPolicy spp;
	private final String owner_pwd;
	private final String user_pwd;

	public Task(String owner_pwd, String user_pwd, DocumentAccessPermission dap) {
		this.owner_pwd = owner_pwd;
		this.user_pwd = user_pwd;
		this.dap = dap;
		this.spp = createProtectionPolicy(owner_pwd, user_pwd, dap);

	}

	private Task(Object[] obj) {
		this((String) obj[0], (String) obj[1], (DocumentAccessPermission) obj[2]);
	}

	/**
	 * Initialize Task by element.
	 * This constructor is designed for parsed XML element.
	 * 
	 * @param element 
	 * @throws FileNotFoundException When source image not found.
	 * @throws UnrecognizedEnumException 
	 */
	public Task(Element element) throws FileNotFoundException, UnrecognizedEnumException {
		this(parsePermission(element.getChild("PERMISSION")));
		
		this.destination = element.getAttributeValue("destination");
		this.align = new PageAlign(element.getAttributeValue("align"));
		this.size = PageSize.getByString(element.getAttributeValue("size"));
		this.defaultDirection = PageDirection.getByString(element.getAttributeValue("default-direction"));
		this.autoRotate = Boolean.valueOf(element.getAttributeValue("auto-rotate"));
		
		List<Element> contains_files = element.getChild("FILES").getChildren("FILE");
		ArrayList<Element> xml_files = new ArrayList<Element>(contains_files);
		imgs.addAll(elements2imgs(xml_files));

	}

	
	
	
	/**
	 * To Element that contain Task attribute.
	 * @return Element
	 */
	public Element toElement() {
		Element task = new Element("TASK");
		Element xml_files = new Element("FILES");
		for (ImgFile img : imgs) {
			Element xml_file = new Element("FILE");
			xml_file.addContent(img.getAbsolutePath());
			xml_files.addContent(xml_file);
		}
		task.addContent(xml_files);

		task.setAttribute("destination", destination);
		task.setAttribute("size", size.toString() + "");
		task.setAttribute("align", align.toString());
		task.setAttribute("default-direction", defaultDirection.toString());
		task.setAttribute("auto-rotate", String.valueOf(autoRotate));

		Element permission = new Element("PERMISSION");
		permission.setAttribute("canPrint", String.valueOf(dap.canPrint()));
		permission.setAttribute("canModify", String.valueOf(dap.canModify()));
		permission.addContent(new Element("OWNER-PASSWORD").addContent(this.getOwner_pwd()));
		permission.addContent(new Element("USER-PASSWORD").addContent(this.getUser_pwd()));
		task.addContent(permission);

		return task;
	}

	private static Object[] parsePermission(Element raw) {
		DocumentAccessPermission dap = new DocumentAccessPermission();
		dap.setCanPrint(Boolean.valueOf(raw.getAttributeValue("canPrint")));
		dap.setCanModify(Boolean.valueOf(raw.getAttributeValue("canModify")));
		Element owner = raw.getChild("OWNER-PASSWORD");
		Element user = raw.getChild("USER-PASSWORD");
		return new Object[] { owner.getValue(), user.getValue(), dap };
	}

	private ArrayList<ImgFile> elements2imgs(ArrayList<Element> el_files) throws FileNotFoundException {
		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		for (Element el : el_files) {
			ImgFile img = new ImgFile(new File(el.getValue()).getAbsolutePath());
			imgs.add(img);
		}
		return imgs;
	}

	public void setSize(PageSize size) {
		this.size = size;
	}

	public void setAlign(PageAlign align) {
		this.align = align;
	}

	public void setAutoRotate(boolean autoRotate) {
		this.autoRotate = autoRotate;
	}

	public void setDefaultDirection(PageDirection defaultDirection) {
		this.defaultDirection = defaultDirection;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setImgs(ArrayList<ImgFile> imgs) {
		this.imgs = imgs;
	}

	public void getImgs(ArrayList<ImgFile> imgs) {
		this.imgs = imgs;
	}

	public ArrayList<ImgFile> getImgs() {
		return this.imgs;
	}

	public PageSize getSize() {
		return size;
	}

	public PageAlign getAlign() {
		return align;
	}

	public PageDirection getDefaultDirection() {
		return defaultDirection;
	}

	public String getDestination() {
		return destination;
	}
	public boolean getAutoRotate() {
		return autoRotate;
	}
	
	public StandardProtectionPolicy getSpp() {
		return spp;
	}

	public String getOwner_pwd() {
		return owner_pwd;
	}

	public String getUser_pwd() {
		return user_pwd;
	}

	public DocumentAccessPermission getDap() {
		return dap;
	}

	private StandardProtectionPolicy createProtectionPolicy(String owner_pwd, String user_pwd, AccessPermission ap) {

		// Define the length of the encryption key.
		// Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
		int keyLength = 128;
		StandardProtectionPolicy spp = new StandardProtectionPolicy(owner_pwd, user_pwd, ap);
		spp.setEncryptionKeyLength(keyLength);
		return spp;
	}

}

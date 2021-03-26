package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.jdom2.Element;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.vincentyeh.IMG2PDF.pdf.argument.ImgFile;
import org.vincentyeh.IMG2PDF.pdf.argument.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.argument.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.argument.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.argument.PageSize;
import org.vincentyeh.IMG2PDF.pdf.argument.doc.DocumentArgument;
import org.vincentyeh.IMG2PDF.pdf.argument.doc.PageArgument;

public class Task {
//	private final PageAlign pdf_align;
//	private final PageSize pdf_size;
//	private final String pdf_destination;
//	private final PageDirection pdf_direction;
//	private final boolean pdf_auto_rotate;

//	private final DocumentAccessPermission pdf_permission;
//	private final StandardProtectionPolicy spp;
//	private final String pdf_owner_password;
//	private final String pdf_user_password;
	private final DocumentArgument documentArgument;
	private final PageArgument pageArgument;

	private final ImgFile[] imgs;


	public Task(DocumentArgument documentArgument,PageArgument pageArgument,ImgFile[] imgs) {
//		if(configuration==null)
//			throw new IllegalArgumentException("configuration is null");
//
		if(imgs==null)
			throw new IllegalArgumentException("imgs is null");
		this.imgs=imgs;
//
//		if(configuration.get("pdf_align")==null)
//			throw new IllegalArgumentException("pdf_align==null");
//		pdf_align = (PageAlign) configuration.get("pdf_align");
//
//
//		if(configuration.get("pdf_size")==null)
//			throw new IllegalArgumentException("pdf_size==null");
//		pdf_size = (PageSize) configuration.get("pdf_size");
//
//
//		if(configuration.get("pdf_destination")==null)
//			throw new IllegalArgumentException("pdf_destination==null");
//		pdf_destination = configuration.get("pdf_destination").toString();
//
//
//		if(configuration.get("pdf_direction")==null)
//			throw new IllegalArgumentException("pdf_direction==null");
//		pdf_direction = (PageDirection) configuration.get("pdf_direction");
//
//		if(configuration.get("pdf_auto_rotate")==null)
//			throw new IllegalArgumentException("pdf_auto_rotate==null");
//		pdf_auto_rotate = (Boolean) configuration.get("pdf_auto_rotate");

		this.documentArgument=documentArgument;
		this.pageArgument=pageArgument;
//		pdf_owner_password = configuration.get("pdf_owner_password") == null ? null
//				: configuration.get("pdf_owner_password").toString();
//		pdf_user_password = configuration.get("pdf_user_password") == null ? null
//				: configuration.get("pdf_user_password").toString();
//
//		if(configuration.get("pdf_permission")==null)
//			throw new IllegalArgumentException("pdf_permission==null");
//		pdf_permission = (DocumentAccessPermission) configuration.get("pdf_permission");
//
//		spp = createProtectionPolicy(pdf_owner_password, pdf_user_password, pdf_permission);
//		imgs = (ArrayList<ImgFile>) configuration.get("imgs");
	}

//	public Task(HashMap<String, Object> configuration,ImgFile[] imgs) {
//		if(configuration==null)
//			throw new IllegalArgumentException("configuration is null");
//
//		if(imgs==null)
//			throw new IllegalArgumentException("imgs is null");
//		this.imgs=imgs;
//
//		if(configuration.get("pdf_align")==null)
//			throw new IllegalArgumentException("pdf_align==null");
//		pdf_align = (PageAlign) configuration.get("pdf_align");
//
//
//		if(configuration.get("pdf_size")==null)
//			throw new IllegalArgumentException("pdf_size==null");
//		pdf_size = (PageSize) configuration.get("pdf_size");
//
//
//		if(configuration.get("pdf_destination")==null)
//			throw new IllegalArgumentException("pdf_destination==null");
//		pdf_destination = configuration.get("pdf_destination").toString();
//
//
//		if(configuration.get("pdf_direction")==null)
//			throw new IllegalArgumentException("pdf_direction==null");
//		pdf_direction = (PageDirection) configuration.get("pdf_direction");
//
//		if(configuration.get("pdf_auto_rotate")==null)
//			throw new IllegalArgumentException("pdf_auto_rotate==null");
//		pdf_auto_rotate = (Boolean) configuration.get("pdf_auto_rotate");
//
//		pdf_owner_password = configuration.get("pdf_owner_password") == null ? null
//				: configuration.get("pdf_owner_password").toString();
//		pdf_user_password = configuration.get("pdf_user_password") == null ? null
//				: configuration.get("pdf_user_password").toString();
//
//		if(configuration.get("pdf_permission")==null)
//			throw new IllegalArgumentException("pdf_permission==null");
//		pdf_permission = (DocumentAccessPermission) configuration.get("pdf_permission");
//
//		spp = createProtectionPolicy(pdf_owner_password, pdf_user_password, pdf_permission);
////		imgs = (ArrayList<ImgFile>) configuration.get("imgs");
//	}

	/**
	 * Initialize Task by element. This constructor is designed for parsed XML
	 * element.
	 * 
	 * @param element
	 * @throws FileNotFoundException     When source image not found.
	 * @throws UnrecognizedEnumException
	 */
	public Task(Element element) throws FileNotFoundException, UnrecognizedEnumException {
		if(element==null)
			throw new NullPointerException("element is null");

//		Element raw = element.getChild("PERMISSION");
//		Element owner = raw.getChild("OWNER-PASSWORD");
//		Element user = raw.getChild("USER-PASSWORD");


//		this.pdf_align = new PageAlign(element.getAttributeValue("align"));
//		this.pdf_size = PageSize.getByString(element.getAttributeValue("size"));
//		this.pdf_destination = element.getAttributeValue("destination");
//		this.pdf_direction = PageDirection.getByString(element.getAttributeValue("default-direction"));
//		this.pdf_auto_rotate = Boolean.parseBoolean(element.getAttributeValue("auto-rotate"));

		this.documentArgument=new DocumentArgument(element.getChild("DocumentArgument"));
		this.pageArgument=new PageArgument(element.getChild("PageArgument"));

//		this.pdf_owner_password = owner.getValue();
//		this.pdf_user_password = user.getValue();
//
//		this.pdf_permission = new DocumentAccessPermission();
//		this.pdf_permission.setCanPrint(Boolean.parseBoolean(raw.getAttributeValue("canPrint")));
//		this.pdf_permission.setCanModify(Boolean.parseBoolean(raw.getAttributeValue("canModify")));
//
//		this.spp = createProtectionPolicy(pdf_owner_password, pdf_user_password, pdf_permission);

		List<Element> contains_files = element.getChild("FILES").getChildren("FILE");
		ArrayList<Element> xml_files = new ArrayList<Element>(contains_files);
		this.imgs=parseElementsToImages(xml_files);

//		this.imgs = new ArrayList<>();
//		this.imgs.addAll(elements2imgs(xml_files));

	}

	/**
	 * To Element that contain Task attribute.
	 * 
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

//		task.setAttribute("destination", pdf_destination);
//		task.setAttribute("size", pdf_size.toString() + "");
//		task.setAttribute("align", pdf_align.toString());
//		task.setAttribute("default-direction", pdf_direction.toString());
//		task.setAttribute("auto-rotate", String.valueOf(pdf_auto_rotate));

		task.addContent(documentArgument.toElement());
		task.addContent(pageArgument.toElement());

//		Element permission = new Element("PERMISSION");
//		permission.setAttribute("canPrint", String.valueOf(pdf_permission.canPrint()));
//		permission.setAttribute("canModify", String.valueOf(pdf_permission.canModify()));
//		permission.addContent(new Element("OWNER-PASSWORD").addContent(pdf_owner_password));
//		permission.addContent(new Element("USER-PASSWORD").addContent(pdf_user_password));
//		task.addContent(permission);

		return task;
	}

	private ImgFile[] parseElementsToImages(ArrayList<Element> el_files) throws FileNotFoundException {
//		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		ImgFile[] imgFiles=new ImgFile[el_files.size()];
		for(int i=0;i<imgFiles.length;i++){
			Element el=el_files.get(i);
			imgFiles[i]=new ImgFile(new File(el.getValue()).getAbsolutePath());
		}

		return imgFiles;
	}

	public ImgFile[] getImgs() {
		return this.imgs;
	}
//
//	public PageSize getPDFSize() {
//		return pdf_size;
//	}
//
//	public PageAlign getPDFAlign() {
//		return pdf_align;
//	}
//
//	public PageDirection getPDFDirection() {
//		return pdf_direction;
//	}
//
//	public String getPDFDestination() {
//		return pdf_destination;
//	}
//
//	public boolean getPDFAutoRotate() {
//		return pdf_auto_rotate;
//	}

	public DocumentArgument getDocumentArgument() {
		return documentArgument;
	}

	public PageArgument getPageArgument() {
		return pageArgument;
	}


	//
//
//	private StandardProtectionPolicy createProtectionPolicy(String owner_pwd, String user_pwd, AccessPermission ap) {
//
//		// Define the length of the encryption key.
//		// Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
//		int keyLength = 128;
//		StandardProtectionPolicy spp = new StandardProtectionPolicy(owner_pwd, user_pwd, ap);
//		spp.setEncryptionKeyLength(keyLength);
//		return spp;
//	}

}

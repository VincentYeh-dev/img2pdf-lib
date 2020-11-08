package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.pdf.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.Size;

public class UniTask extends DefaultTask {
	
	public UniTask(String owner_pwd, String user_pwd, DocumentAccessPermission dap) {
		super(owner_pwd, user_pwd, dap);
	}
	
	public UniTask(Element element) throws FileNotFoundException {
		this(element.getAttributeValue("owner"), element.getAttributeValue("user"),parseDAP(element));
		super.destination=element.getAttributeValue("destination");
		super.align=new Align(element.getAttributeValue("align"));
		super.size=Size.getSizeFromString(element.getAttributeValue("size"));
		super.defaultDirection=PageDirection.getDirectionFromString(element.getAttributeValue("default-direction"));
		super.autoRotate=Boolean.valueOf(element.getAttributeValue("auto-rotate"));
		
		
		List<Element> contains_files = element.getChild("FILES").getChildren("FILE");
		ArrayList<Element> xml_files = new ArrayList<Element>(contains_files);
		imgs.addAll(elements2imgs(xml_files));

	}

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
		task.setAttribute("size", size.getStr() + "");
		task.setAttribute("align", align.toString());
		task.setAttribute("owner", super.getOwner_pwd());
		task.setAttribute("user", super.getUser_pwd());
		task.setAttribute("default-direction", defaultDirection.toString());
		task.setAttribute("auto-rotate", String.valueOf(autoRotate));
		task.addContent(dap.toElement());
		
		return task;
	}
	
	private static DocumentAccessPermission parseDAP(Element raw) {
		Element pms=raw.getChild("PERMISSION");
		
		DocumentAccessPermission dap=new DocumentAccessPermission();
		dap.setCanPrint(Boolean.valueOf(pms.getAttributeValue("canPrint")));
		dap.setCanModify(Boolean.valueOf(pms.getAttributeValue("canModify")));
		
		return dap;
	}
	
	private ArrayList<ImgFile> elements2imgs(ArrayList<Element> el_files) throws FileNotFoundException {
		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		for (Element el : el_files) {
			ImgFile img = new ImgFile(new File(el.getValue()).getAbsolutePath());
			imgs.add(img);
		}
		return imgs;
	}
	

	public void setAlign(Align align) {
		super.align = align;
	}
	public void setAutoRotate(boolean autoRotate) {
		super.autoRotate = autoRotate;
	}
	public void setDefaultDirection(PageDirection defaultDirection) {
		super.defaultDirection = defaultDirection;
	}
	public void setDestination(String destination) {
		super.destination = destination;
	}
	public void setImgs(ArrayList<ImgFile> imgs) {
		super.imgs = imgs;
	}
	
	
}

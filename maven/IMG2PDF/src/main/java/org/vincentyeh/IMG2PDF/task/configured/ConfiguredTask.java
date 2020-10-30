package org.vincentyeh.IMG2PDF.task.configured;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.jdom2.Element;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.pdf.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.Size;
import org.vincentyeh.IMG2PDF.task.Task;

public class ConfiguredTask extends Task {
	private final StandardProtectionPolicy spp;
	
	public ConfiguredTask(Element element) throws FileNotFoundException {
		super(element.getAttributeValue("destination"), new Align(element.getAttributeValue("align")),
				Size.getSizeFromString(element.getAttributeValue("size")),
				PageDirection.getDirectionFromString(element.getAttributeValue("default-direction")),
				Boolean.valueOf(element.getAttributeValue("auto-rotate")));
		
		
		this.spp=createProtectionPolicy(element.getAttributeValue("owner"), element.getAttributeValue("user"),parseDAP(element));
		
		
		List<Element> contains_files = element.getChild("FILES").getChildren("FILE");
		ArrayList<Element> xml_files = new ArrayList<Element>(contains_files);
		imgs.addAll(elements2imgs(xml_files));

	}
	private DocumentAccessPermission parseDAP(Element raw) {
		DocumentAccessPermission dap=new DocumentAccessPermission();
		dap.setCanPrint(Boolean.valueOf(raw.getAttributeValue("canPrint")));
		dap.setCanModify(Boolean.valueOf(raw.getAttributeValue("canModify")));
		
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

	public ArrayList<ImgFile> getImgs() {
		return imgs;
	}
	
	private StandardProtectionPolicy createProtectionPolicy(String owner_pwd, String user_pwd, AccessPermission ap) {
		owner_pwd = owner_pwd.replace("#null", "");
		user_pwd = user_pwd.replace("#null", "");
		// Define the length of the encryption key.
		// Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
		int keyLength = 128;
		StandardProtectionPolicy spp = new StandardProtectionPolicy(owner_pwd, user_pwd, ap);
		spp.setEncryptionKeyLength(keyLength);
//		spp.setPermissions(ap);
		return spp;
	}
	
	public StandardProtectionPolicy getSpp() {
		return spp;
	}
}

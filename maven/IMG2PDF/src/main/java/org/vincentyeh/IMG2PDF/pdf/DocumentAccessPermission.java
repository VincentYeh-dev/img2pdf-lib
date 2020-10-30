package org.vincentyeh.IMG2PDF.pdf;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.jdom2.Element;

public class DocumentAccessPermission extends AccessPermission {

	public Element toElement() {
		Element permission = new Element("PERMISSION");
		permission.setAttribute("canPrint", String.valueOf(this.canPrint()));
		permission.setAttribute("canModify", String.valueOf(this.canModify()));
		return permission;
	}

}
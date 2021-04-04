package org.vincentyeh.IMG2PDF.pdf.doc;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;

public class DocumentAccessPermission extends AccessPermission{
	
	public DocumentAccessPermission() {
		
	}

	public DocumentAccessPermission(String str) {
		char[] permissions=str.toCharArray();
		setCanPrint(permissions[0]!='0');
		setCanModify(permissions[0]!='0');
	}

	@Override
	public String toString() {
		return (canPrint()?"1":"0")+ (canModify()?"1":"0");
	}
}
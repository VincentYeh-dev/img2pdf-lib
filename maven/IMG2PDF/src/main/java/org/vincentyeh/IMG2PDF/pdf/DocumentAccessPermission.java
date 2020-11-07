package org.vincentyeh.IMG2PDF.pdf;

import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.jdom2.Element;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

public class DocumentAccessPermission extends AccessPermission implements ArgumentType<DocumentAccessPermission> {
	
	public DocumentAccessPermission() {

	}

	public DocumentAccessPermission(String str) {
		String[] permission=str.split(".");
		setCanPrint(str.contains("p"));
		setCanModify(str.contains("m"));
	}

	@Override
	public DocumentAccessPermission convert(ArgumentParser parser, Argument arg, String value)
			throws ArgumentParserException {
		// TODO Auto-generated method stub
		return new DocumentAccessPermission(value);
	}

	public Element toElement() {
		Element permission = new Element("PERMISSION");
		permission.setAttribute("canPrint", String.valueOf(this.canPrint()));
		permission.setAttribute("canModify", String.valueOf(this.canModify()));
		return permission;
	}


}
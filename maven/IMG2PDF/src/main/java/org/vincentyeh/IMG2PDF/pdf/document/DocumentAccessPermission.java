package org.vincentyeh.IMG2PDF.pdf.document;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

public class DocumentAccessPermission extends AccessPermission implements ArgumentType<DocumentAccessPermission> {
	
	public DocumentAccessPermission() {
		
	}

	public DocumentAccessPermission(String str) {
		char[] permissions=str.toCharArray();
		setCanPrint(permissions[0]!='0');
		setCanModify(permissions[0]!='0');
	}

	@Override
	public DocumentAccessPermission convert(ArgumentParser parser, Argument arg, String value)
			throws ArgumentParserException {
		// TODO Auto-generated method stub
		return new DocumentAccessPermission(value);
	}



}
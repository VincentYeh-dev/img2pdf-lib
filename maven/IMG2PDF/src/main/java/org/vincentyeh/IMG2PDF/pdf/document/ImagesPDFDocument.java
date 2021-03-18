package org.vincentyeh.IMG2PDF.pdf.document;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.vincentyeh.IMG2PDF.commandline.Configuration;

/**
 * PDF document that contain destination.
 * @author VincentYeh
 */
public class ImagesPDFDocument extends PDDocument {
	private File destination;
	public void setDestination(String destination) {
		setDestination(new File(destination));
	}
	
	public void setDestination(File destination) {
		this.destination = destination;
	}
	
	public void save() throws IOException {
		if(destination.getParentFile().mkdirs()) {
			System.out.printf(Configuration.getResString("info_required_folder_created")+"\n", destination.getParent());
		}
		
		super.save(this.destination);
	}
	
	public File getDestination() {
		return destination;
	}
}

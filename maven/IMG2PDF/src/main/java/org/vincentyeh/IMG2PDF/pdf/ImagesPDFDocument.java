package org.vincentyeh.IMG2PDF.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.Size;

public class ImagesPDFDocument extends PDDocument {
	private final Size size;
	private final Align align;
	private File destination;
	public ImagesPDFDocument(Size size, Align align,StandardProtectionPolicy ssp) throws IOException {
		this(size,align);
		if(ssp!=null)
			protect(ssp);
	}
	
	public ImagesPDFDocument(Size size, Align align){
		this.size = size;
		this.align = align;
	}
	public void setDestination(String destination) {
		setDestination(new File(destination));
	}
	
	public void setDestination(File destination) {
		this.destination = destination;
	}
	
	public void save() throws IOException {
		// TODO Auto-generated method stub
		super.save(this.destination);
	}
	
	public Size getSize() {
		return size;
	}
	public Align getAlign() {
		return align;
	}
	public File getDestination() {
		return destination;
	}
}

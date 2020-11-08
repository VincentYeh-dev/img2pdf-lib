package org.vincentyeh.IMG2PDF.pdf.document;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;

public class ImagesPDFDocument extends PDDocument {
	private final PageSize size;
	private final PageAlign align;
	private File destination;
	public ImagesPDFDocument(PageSize size, PageAlign align,StandardProtectionPolicy ssp) throws IOException {
		this(size,align);
		if(ssp!=null)
			protect(ssp);
	}
	
	public ImagesPDFDocument(PageSize size, PageAlign align){
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
	
	public PageSize getSize() {
		return size;
	}
	public PageAlign getAlign() {
		return align;
	}
	public File getDestination() {
		return destination;
	}
}

package org.vincentyeh.IMG2PDF.pdf;

import java.io.File;
import java.util.ArrayList;

import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.Size;

//public class PDFArguments {
//	
//	private Align align;
//	private Size size;
//	private String destination;
//	private String owner_pwd;
//	private String user_pwd;
//	private ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
//	private PageDirection defaultDirection;
//	private boolean autoRotate;
//
//	public PDFArguments(File[] files, String destination, String own, String user,DocumentAccessPermission dap, Sortby sortby, Order order,
//			Align align, Size size, PageDirection defaultDirection, boolean autoRotate){
//		if (destination == null)
//			throw new NullPointerException("destination is null.");
//		this.align = align;
//		this.size = size;
//		this.destination = destination;
//		this.defaultDirection = defaultDirection;
//		this.autoRotate = autoRotate;
//	}
//
//}

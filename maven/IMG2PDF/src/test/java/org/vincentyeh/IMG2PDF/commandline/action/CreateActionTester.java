package org.vincentyeh.IMG2PDF.commandline.action;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.commandline.action.exception.ArgumentNotFoundException;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.pdf.document.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;

import net.sourceforge.argparse4j.inf.Namespace;

public class CreateActionTester {
//	
//	@Test
//	public void test_source_folder_not_exists() {
//		HashMap<String,Object> attrs=new HashMap<>();
//		attrs.put("action",new CreateAction());
//		attrs.put("pdf_align",new PageAlign("CENTER-CENTER"));
//		attrs.put("pdf_size",PageSize.A4);
//		attrs.put("pdf_direction",PageDirection.Vertical);
//		attrs.put("pdf_auto_rotate",true);
//		attrs.put("pdf_sortby",Sortby.NAME);
//		attrs.put("pdf_order",Order.INCREASE);
////		attrs.put("pdf_owner_password",null);
////		attrs.put("pdf_user_password",null);
//		attrs.put("pdf_permission",new DocumentAccessPermission());
//		attrs.put("pdf_destination","");
//		attrs.put("list_destination","src\\test\\resources\\test.xml");
//		attrs.put("filter",new FileFilterHelper("[^\\.]*\\.(png|PNG|jpg|JPG)"));
////		attrs.put("list_destination","");
////		attrs.put("list_destination","C:\\test.xml");
//		ArrayList<String> sources=new ArrayList<>();
//		sources.add("src\\test\\resources\\dirlist_NotExist.txt");
//		attrs.put("source",sources);
//		Namespace ns=new Namespace(attrs);
//		CreateAction aa=(CreateAction)ns.get("action");
//		aa.setupByNamespace(ns);
//		
//		try {
//			aa.start();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void test_source_folder_is_file() {
//		HashMap<String,Object> attrs=new HashMap<>();
//		attrs.put("action",new CreateAction());
//		attrs.put("pdf_align",new PageAlign("CENTER-CENTER"));
//		attrs.put("pdf_size",PageSize.A4);
//		attrs.put("pdf_direction",PageDirection.Vertical);
//		attrs.put("pdf_auto_rotate",true);
//		attrs.put("pdf_sortby",Sortby.NAME);
//		attrs.put("pdf_order",Order.INCREASE);
////		attrs.put("pdf_owner_password",null);
////		attrs.put("pdf_user_password",null);
//		attrs.put("pdf_permission",new DocumentAccessPermission());
//		attrs.put("pdf_destination","");
//		attrs.put("list_destination","src\\test\\resources\\test.xml");
//		attrs.put("filter",new FileFilterHelper("[^\\.]*\\.(png|PNG|jpg|JPG)"));
////		attrs.put("list_destination","");
////		attrs.put("list_destination","C:\\test.xml");
//		ArrayList<String> sources=new ArrayList<>();
//		sources.add("src\\test\\resources\\dirlist_File.txt");
//		attrs.put("source",sources);
//		Namespace ns=new Namespace(attrs);
//		CreateAction aa=(CreateAction)ns.get("action");
//		aa.setupByNamespace(ns);
//		
//		try {
//			aa.start();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}

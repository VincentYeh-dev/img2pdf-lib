package org.vincentyeh.IMG2PDF.commandline.action;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.vincentyeh.IMG2PDF.commandline.action.exception.ArgumentNotFoundException;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.pdf.document.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;

import net.sourceforge.argparse4j.inf.Namespace;

public class ImportActionTester {

//	@Test
//	public void testNotExist() {
//
//		String filter = ".*\\.(png$VBARPNG$VBARjpg$VBARJPG)";
//		String create_command = "create " + "-pz A4 " + "-ps NUMERTIC " + "-pa CENTER-CENTER " + "-pdi Vertical "
//				+ "-par yes " + "-po INCREASE " + "-pdst " + "\"src\\test\\resources\\dirlist_NotExist.pdf\" "
//				+ "-ldst " + "\"src\\test\\resources\\dirlist_NotExist.xml\"" + " import -s \""
//				+ "src\\test\\resources\\dirlist_NotExist.txt" + "\" -f " + filter;
//
//		String convert_command = "convert " + "\"src\\test\\resources\\dirlist_NotExist.xml\"";
//		System.out.println(create_command);
////		System.out.println(convert_command);
//		System.out.println();
//		MainProgram.main(create_command);
////		MainProgram.main(convert_command);
//	}
//
//	@Test
//	public void testNotAFolder() {
//
//		String filter = ".*\\.(png$VBARPNG$VBARjpg$VBARJPG)";
//		String create_command = "create " + "-pz A4 " + "-ps NUMERTIC " + "-pa CENTER-CENTER " + "-pdi Vertical "
//				+ "-par yes " + "-po INCREASE " + "-pdst " + "\"src\\test\\resources\\dirlist_File.pdf\" "
//				+ "-ldst " + "\"src\\test\\resources\\dirlist_File.xml\"" + " import -s \""
//				+ "src\\test\\resources\\dirlist_File.txt" + "\" -f " + filter;
//
//		String convert_command = "convert " + "\"src\\test\\resources\\dirlist_File.xml\"";
//		System.out.println(create_command);
////		System.out.println(convert_command);
//		System.out.println();
//		MainProgram.main(create_command);
//		MainProgram.main(convert_command);
//	}
	
	@Test
	public void test_setupByNamespace() {
		HashMap<String,Object> attrs=new HashMap<>();
		attrs.put("action",new ImportAction());
		attrs.put("pdf_align",new PageAlign("CENTER-CENTER"));
		attrs.put("pdf_size",PageSize.A4);
		attrs.put("pdf_direction",PageDirection.Vertical);
		attrs.put("pdf_auto_rotate",true);
		attrs.put("pdf_sortby",Sortby.NAME);
		attrs.put("pdf_order",Order.INCREASE);
//		attrs.put("pdf_owner_password",null);
//		attrs.put("pdf_user_password",null);
		attrs.put("pdf_permission",new DocumentAccessPermission());
		attrs.put("pdf_destination","");
		attrs.put("list_destination","src\\test\\resources\\test.xml");
//		attrs.put("list_destination","");
//		attrs.put("list_destination","C:\\test.xml");
		ArrayList<String> sources=new ArrayList<>();
		sources.add("src\\test\\resources\\dirlist_NotExist.txt");
		attrs.put("source",sources);
		Namespace ns=new Namespace(attrs);
		CreateAction aa=(ImportAction)ns.get("action");
		aa.setupByNamespace(ns);
		
		try {
			aa.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

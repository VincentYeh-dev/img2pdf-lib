package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.file.text.UTF8InputStream;
import org.vincentyeh.IMG2PDF.pdf.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.Size;
import org.vincentyeh.IMG2PDF.task.configure.ConfigureTask;
import org.vincentyeh.IMG2PDF.task.configure.ConfigureTaskList;
import org.vincentyeh.IMG2PDF.util.NameFormatter;

import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.inf.Namespace;

public class ImportAction extends CreateAction {

	@Arg(dest = "source")
	protected ArrayList<String> sources;

	public ImportAction() {

	}

//	public ImportAction(Namespace ns) {
//		super.pdf_size = (Size) ns.get("pdf_size");
//		super.pdf_align = (Align) ns.get("pdf_align");
//		super.pdf_direction = (PageDirection) ns.get("pdf_direction");
//		super.pdf_auto_rotate = ns.getBoolean("pdf_auto_rotate");
//		super.pdf_sortby = (Sortby) ns.get("pdf_sortby");
//		super.pdf_order = (Order) ns.get("pdf_order");
//		super.pdf_owner_password = ns.getString("pdf_owner_password");
//		super.pdf_user_password = ns.getString("pdf_user_password");
//		super.pdf_permission = (DocumentAccessPermission) ns.get("pdf_permission");
//		super.pdf_destination = ns.getString("pdf_destination");
//		super.list_destination = ns.getString("list_destination");
//		this.sources = (ArrayList<String>) ns.get("source");
//	}


	@Override
	public void start() {
		try {
			createFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void setupByNamespace(Namespace ns) {
		super.pdf_size = (Size) ns.get("pdf_size");
		super.pdf_align = (Align) ns.get("pdf_align");
		super.pdf_direction = (PageDirection) ns.get("pdf_direction");
		super.pdf_auto_rotate = ns.getBoolean("pdf_auto_rotate");
		super.pdf_sortby = (Sortby) ns.get("pdf_sortby");
		super.pdf_order = (Order) ns.get("pdf_order");
		super.pdf_owner_password = ns.getString("pdf_owner_password");
		super.pdf_user_password = ns.getString("pdf_user_password");
		super.pdf_permission = (DocumentAccessPermission) ns.get("pdf_permission");
		super.pdf_destination = ns.getString("pdf_destination");
		super.list_destination = ns.getString("list_destination");
		this.sources = (ArrayList<String>) ns.get("source");
	}

	public void createFile() throws IOException {
		ConfigureTaskList tasks = new ConfigureTaskList();

		for (String source : sources) {
			tasks.addAll(importTasksFromTXT(new File(source)));
		}
		tasks.toXMLFile(new File(list_destination));
	}

	private ConfigureTaskList importTasksFromTXT(File file) throws IOException {
		UTF8InputStream uis = new UTF8InputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(uis.getInputStream(), "UTF-8"));
		ConfigureTaskList tasks = new ConfigureTaskList();
		String buf = "";
		while (buf != null) {
			buf = reader.readLine();
			if (buf != null && !buf.isEmpty()) {
//					trim():	private invisible character pass to the constructor 
//					of File and make file not exists.
				System.out.println(buf);
				File dir = new File(buf);
				if (!dir.exists())
					throw new FileNotFoundException(dir.getName() + " not found.");

				if (!dir.isDirectory())
					throw new RuntimeException(dir.getName() + " is not the directory.");

				NameFormatter nf = new NameFormatter(pdf_destination, dir);
				FileFilterHelper ffh = createImageFilter(0);
				ConfigureTask task = new ConfigureTask(dir.listFiles(ffh), nf.getConverted(), pdf_owner_password,
						pdf_user_password, pdf_permission, pdf_sortby, pdf_order, pdf_align, pdf_size, pdf_direction,
						pdf_auto_rotate);
				tasks.add(task);
			}
		}
		uis.close();
		reader.close();
		return tasks;
	}

	private FileFilterHelper createImageFilter(int condition) {
		FileFilterHelper ffh = new FileFilterHelper(
				condition | FileFilterHelper.CONDITION_IS_FILE | FileFilterHelper.CONDITION_EXT_EQUALS);
		ffh.appendExtSLT("JPG");
		ffh.appendExtSLT("jpg");
		ffh.appendExtSLT("PNG");
		ffh.appendExtSLT("png");
		return ffh;
	}
}
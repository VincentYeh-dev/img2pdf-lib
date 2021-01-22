package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.PatternSyntaxException;

import org.vincentyeh.IMG2PDF.commandline.action.exception.SourceFolderException;
import org.vincentyeh.IMG2PDF.commandline.action.exception.SourceFolderIsFileException;
import org.vincentyeh.IMG2PDF.commandline.action.exception.SourceFolderNotFoundException;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.file.text.UTF8InputStream;
import org.vincentyeh.IMG2PDF.pdf.document.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.NameFormatter;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public abstract class CreateAction extends AbstractAction {

	protected PageSize pdf_size;
	protected PageAlign pdf_align;
	protected PageDirection pdf_direction;
	protected boolean pdf_auto_rotate;
	protected Sortby pdf_sortby;
	protected Order pdf_order;
	protected String pdf_owner_password;
	protected String pdf_user_password;
	protected DocumentAccessPermission pdf_permission;
	protected String pdf_destination;
	protected String list_destination;

	@Override
	public void setupByNamespace(Namespace ns) {

		pdf_size = (PageSize) ns.get("pdf_size");
		pdf_align = (PageAlign) ns.get("pdf_align");
		pdf_direction = (PageDirection) ns.get("pdf_direction");
		pdf_auto_rotate = ns.getBoolean("pdf_auto_rotate");
		pdf_sortby = (Sortby) ns.get("pdf_sortby");
		pdf_order = (Order) ns.get("pdf_order");
		pdf_owner_password = ns.getString("pdf_owner_password");
		pdf_user_password = ns.getString("pdf_user_password");
		pdf_permission = (DocumentAccessPermission) ns.get("pdf_permission");
		pdf_destination = ns.getString("pdf_destination");
		list_destination = ns.getString("list_destination");
	}

	public static void setupParser(Subparsers subparsers) {
		Subparser parser = subparsers.addParser("create").help(lagug_resource.getString("help_create"));

		parser.addArgument("-pz", "--pdf_size").required(true).type(PageSize.class)
				.help(lagug_resource.getString("help_create_pdf_size"));
		parser.addArgument("-pa", "--pdf_align").type(new PageAlign("CENTER-CENTER")).setDefault(new PageAlign("CENTER-CENTER"))
				.metavar("TopBottom|LeftRight").help(lagug_resource.getString("help_create_pdf_align"));

		parser.addArgument("-pdi", "--pdf_direction").type(PageDirection.class)
				.help(lagug_resource.getString("help_create_pdf_direction"));

		parser.addArgument("-par", "--pdf_auto_rotate").setDefault(Boolean.TRUE)
				.type(Arguments.booleanType("yes", "no")).help(lagug_resource.getString("help_create_pdf_auto_rotate"));

		parser.addArgument("-ps", "--pdf_sortby").type(Sortby.class)
				.help(lagug_resource.getString("help_create_pdf_sortby"));

		parser.addArgument("-po", "--pdf_order").type(Order.class)
				.help(lagug_resource.getString("help_create_pdf_order"));

		parser.addArgument("-popwd", "--pdf_owner_password").type(String.class).metavar("ownerpassword")
				.help(lagug_resource.getString("help_create_pdf_owner_password"));
		parser.addArgument("-pupwd", "--pdf_user_password").type(String.class).metavar("userpassword")
				.help(lagug_resource.getString("help_create_pdf_user_password"));
		parser.addArgument("-pp", "--pdf_permission").type(new DocumentAccessPermission())
				.setDefault(new DocumentAccessPermission())
				.help(lagug_resource.getString("help_create_pdf_permission"));

		parser.addArgument("-pdst", "--pdf_destination").type(String.class).metavar("destination")
				.help(lagug_resource.getString("help_create_pdf_destination"));

		parser.addArgument("-ldst", "--list_destination").type(String.class).metavar("destination")
				.help(lagug_resource.getString("help_create_list_destination"));

		Subparsers innerSubparsers = parser.addSubparsers();
		ImportAction.setupParser(innerSubparsers);
		AddAction.setupParser(innerSubparsers);

	}

	protected TaskList importTasksFromTXT(File file, FileFilterHelper filter) throws PatternSyntaxException,SourceFolderException,IOException {
		if(!file.exists())
			new FileNotFoundException(file.getName()+" not found.");
		
		UTF8InputStream uis = new UTF8InputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(uis.getInputStream(), "UTF-8"));
		TaskList tasks = new TaskList();
		
		String format="### Document setting ###\nAlign:%s\nSize:%s\nDefault direction:%s\nAuto Rotate:%s\n###END###\n\n";
		System.out.printf(format,pdf_align.toString(),pdf_size.toString(),pdf_direction.toString(),pdf_auto_rotate);
		
		int line_counter=0;
		String buf = "";
		while (buf != null) {
			buf = reader.readLine();
			line_counter++;
			if (buf != null && !buf.isEmpty()) {
				File dir = new File(buf);
				
				if (!dir.exists())
					throw new SourceFolderNotFoundException(line_counter,dir,file);

				if (dir.isFile())
					throw new SourceFolderIsFileException(line_counter,dir,file);
				
				tasks.add(parse2Task(dir,filter));
				
				System.out.println("[imported] " + dir.getName());
			}
		}
		uis.close();
		reader.close();
		return tasks;
	}
	
	private Task parse2Task(File source_directory, FileFilterHelper filter) throws FileNotFoundException {
		Task task = new Task(pdf_owner_password, pdf_user_password, pdf_permission);

		NameFormatter nf = new NameFormatter(source_directory);
		String real_dest = nf.format(pdf_destination);
		task.setDestination(real_dest);
		task.setAlign(pdf_align);
		task.setSize(pdf_size);
		task.setDefaultDirection(pdf_direction);
		task.setAutoRotate(pdf_auto_rotate);
		
		ArrayList<ImgFile> imgs=importImagesFile(source_directory, filter);
		Collections.sort(imgs);
		task.setImgs(imgs);
		return task;
	}
	
	private ArrayList<ImgFile> importImagesFile(File source_directory, FileFilterHelper filter) throws FileNotFoundException{
		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		for (File f : source_directory.listFiles(filter)) {
			ImgFile img = new ImgFile(f.getAbsolutePath(), pdf_sortby, pdf_order);
			imgs.add(img);
		}
		return imgs;
	}
}
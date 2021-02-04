package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.vincentyeh.IMG2PDF.commandline.action.exception.ArgumentNotFoundException;
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

public class CreateAction extends AbstractAction {

	protected final PageSize pdf_size;
	protected final PageAlign pdf_align;
	protected final PageDirection pdf_direction;
	protected final boolean pdf_auto_rotate;
	protected final Sortby pdf_sortby;
	protected final Order pdf_order;
	protected final String pdf_owner_password;
	protected final String pdf_user_password;
	protected final DocumentAccessPermission pdf_permission;
	protected final String pdf_destination;
	protected final String list_destination;

	protected final File[] sources;
	protected final FileFilterHelper filter;

	public CreateAction(CommandLine cmd) throws FileNotFoundException {
		Properties pdf_prop = cmd.getOptionProperties("P");

		pdf_size = PageSize.getSizeFromString(pdf_prop.getProperty("size"));
		if (pdf_size == null)
			throw new ArgumentNotFoundException("size");

		pdf_align = new PageAlign(pdf_prop.getProperty("align"));

		if (pdf_align == null)
			throw new ArgumentNotFoundException("align");

		pdf_direction = PageDirection.getDirectionFromString(pdf_prop.getProperty("direction"));
		if (pdf_direction == null)
			throw new ArgumentNotFoundException("direction");

		pdf_auto_rotate = pdf_prop.getProperty("direction").equals("YES");

		pdf_sortby = Sortby.getByStr(pdf_prop.getProperty("sortby"));
		if (pdf_sortby == null)
			throw new ArgumentNotFoundException("sortby");

		pdf_order = Order.getByStr("order");
		if (pdf_order == null)
			throw new ArgumentNotFoundException("order");

		pdf_owner_password = pdf_prop.getProperty("owner_password");
		pdf_user_password = pdf_prop.getProperty("user_password");

		pdf_permission = new DocumentAccessPermission(pdf_prop.getProperty("permission"));

		if (pdf_permission == null)
			throw new ArgumentNotFoundException("permission");

		pdf_destination = pdf_prop.getProperty("destination");

		if (pdf_destination == null)
			throw new ArgumentNotFoundException("destination");

		list_destination = cmd.getOptionValue("list_destination");

		if (list_destination == null)
			throw new ArgumentNotFoundException("list_destination");

		this.filter = new FileFilterHelper(cmd.getOptionValue("filter"));

		if (filter == null)
			throw new ArgumentNotFoundException("filter");

		String[] str_sources = cmd.getOptionValues("source");

		if (str_sources == null)
			throw new ArgumentNotFoundException("sources");
		
		sources = new File[str_sources.length];
		for (int i = 0; i < sources.length; i++) {
			System.out.println("sources checking....");
			sources[i] = new File(str_sources[i]);

			if (!sources[i].exists()) {
				System.err.println("File not found:" + sources[i].getAbsolutePath());
				continue;
			} else if (sources[i].isFile()) {
				System.err.println("Path should be a folder:" + sources[i].getAbsolutePath());
				continue;
			} else {
				System.err.println("[Verified] " + sources[i].getAbsolutePath());
			}

		}
	}

//	@Override
//	public void setupByNamespace(Namespace ns) {
//		pdf_size = (PageSize) ns.get("pdf_size");
//		if (pdf_size == null)
//			throw new ArgumentNotFoundException("pdf_size");
//		pdf_align = (PageAlign) ns.get("pdf_align");
//		if (pdf_align == null)
//			throw new ArgumentNotFoundException("pdf_align");
//		pdf_direction = (PageDirection) ns.get("pdf_direction");
//		if (pdf_direction == null)
//			throw new ArgumentNotFoundException("pdf_direction");
//		pdf_auto_rotate = ns.getBoolean("pdf_auto_rotate");
//
//		pdf_sortby = (Sortby) ns.get("pdf_sortby");
//		if (pdf_sortby == null)
//			throw new ArgumentNotFoundException("pdf_sortby");
//		pdf_order = (Order) ns.get("pdf_order");
//		if (pdf_order == null)
//			throw new ArgumentNotFoundException("pdf_order");
//		pdf_owner_password = ns.getString("pdf_owner_password");
//
//		pdf_user_password = ns.getString("pdf_user_password");
//
//		pdf_permission = (DocumentAccessPermission) ns.get("pdf_permission");
//
//		if (pdf_permission == null)
//			throw new ArgumentNotFoundException("pdf_permission");
//
//		pdf_destination = ns.getString("pdf_destination");
//
//		if (pdf_destination == null)
//			throw new ArgumentNotFoundException("pdf_destination");
//		list_destination = ns.getString("list_destination");
//		if (list_destination == null)
//			throw new ArgumentNotFoundException("list_destination");
//
//		this.filter = (FileFilterHelper) ns.get("filter");
//
//		if (filter == null)
//			throw new ArgumentNotFoundException("filter");
//
//		this.sources = ns.getList("source");
//
//		if (sources == null)
//			throw new ArgumentNotFoundException("sources");
//
//	}

//	public static void setupParser(Subparsers subparsers) {
//		Subparser parser = subparsers.addParser("create").help(lagug_resource.getString("help_create"));
//		parser.setDefault("action", new CreateAction());
//		parser.addArgument("-pz", "--pdf_size").required(true).type(PageSize.class)
//				.help(lagug_resource.getString("help_create_pdf_size"));
//		parser.addArgument("-pa", "--pdf_align").required(false).type(new PageAlign("CENTER-CENTER"))
//				.setDefault(new PageAlign("CENTER-CENTER")).metavar("TopBottom|LeftRight")
//				.help(lagug_resource.getString("help_create_pdf_align"));
//
//		parser.addArgument("-pdi", "--pdf_direction").required(false).type(PageDirection.class)
//				.setDefault(PageDirection.Vertical).help(lagug_resource.getString("help_create_pdf_direction"));
//
//		parser.addArgument("-par", "--pdf_auto_rotate").required(false).type(Arguments.booleanType("yes", "no"))
//				.setDefault(Boolean.TRUE).help(lagug_resource.getString("help_create_pdf_auto_rotate"));
//
//		parser.addArgument("-ps", "--pdf_sortby").required(false).type(Sortby.class).setDefault(Sortby.NAME)
//				.help(lagug_resource.getString("help_create_pdf_sortby"));
//
//		parser.addArgument("-po", "--pdf_order").required(false).type(Order.class).setDefault(Order.INCREASE)
//				.help(lagug_resource.getString("help_create_pdf_order"));
//
//		parser.addArgument("-popwd", "--pdf_owner_password").required(false).type(String.class).metavar("ownerpassword")
//				.help(lagug_resource.getString("help_create_pdf_owner_password"));
//		parser.addArgument("-pupwd", "--pdf_user_password").required(false).type(String.class).metavar("userpassword")
//				.help(lagug_resource.getString("help_create_pdf_user_password"));
//
//		parser.addArgument("-pp", "--pdf_permission").required(false).type(new DocumentAccessPermission())
//				.setDefault(new DocumentAccessPermission())
//				.help(lagug_resource.getString("help_create_pdf_permission"));
//
//		parser.addArgument("-pdst", "--pdf_destination").required(true).type(String.class).metavar("destination")
//				.help(lagug_resource.getString("help_create_pdf_destination"));
//
//		parser.addArgument("-ldst", "--list_destination").required(true).type(String.class).metavar("destination")
//				.help(lagug_resource.getString("help_create_list_destination"));
//
//		parser.addArgument("-f", "--filter").required(false).type(new FileFilterHelper(""))
//				.setDefault(new FileFilterHelper("[^\\.]*\\.(png|PNG|jpg|JPG)"))
//				.help(lagug_resource.getString("help_import_filter"));
//
//		parser.addArgument("source").type(String.class).nargs("*").help(lagug_resource.getString("help_import_source"));
//
//	}

	protected TaskList importTasksFromTXT(File file, FileFilterHelper filter) throws IOException {
		if (!file.exists())
			new FileNotFoundException(file.getName() + " not found.");

		UTF8InputStream uis = new UTF8InputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(uis.getInputStream(), StandardCharsets.UTF_8));

		TaskList tasks = new TaskList();
		String format = "### Document setting ###\nAlign:%s\nSize:%s\nDefault direction:%s\nAuto Rotate:%s\n###END###\n";
		System.out.printf(format, pdf_align.toString(), pdf_size.toString(), pdf_direction.toString(), pdf_auto_rotate);

		System.out.println("Import tasks from list:");
		int line_counter = 0;
		String buf = "";
		while (buf != null) {
			buf = reader.readLine();
			line_counter++;
			if (buf != null && !buf.isEmpty()) {
				File dir = new File(buf);
				try {
					if (!dir.exists())
						throw new SourceFolderNotFoundException(line_counter, dir, file);

					if (dir.isFile())
						throw new SourceFolderIsFileException(line_counter, dir, file);

					tasks.add(parse2Task(dir, filter));
					System.out.println("\t[imported] " + dir.getAbsolutePath());
				} catch (SourceFolderException e) {
					System.err.println(e.getMessage());
				}
			}
		}

		try {
			if (uis != null)
				uis.close();
		} catch (IOException ignore) {
		}

		try {
			if (reader != null)
				reader.close();
		} catch (IOException ignore) {
		}

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

		ArrayList<ImgFile> imgs = importImagesFile(source_directory, filter);
		Collections.sort(imgs);
		task.setImgs(imgs);
		return task;
	}

	private ArrayList<ImgFile> importImagesFile(File source_directory, FileFilterHelper filter)
			throws FileNotFoundException {
		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		for (File f : source_directory.listFiles(filter)) {
			ImgFile img = new ImgFile(f.getAbsolutePath(), pdf_sortby, pdf_order);
			imgs.add(img);
		}
		return imgs;
	}

	@Override
	public void start() throws Exception {
		super.start();
		File dst = new File(list_destination);

		TaskList tasks = dst.exists() ? new TaskList(dst) : new TaskList();

		for (File source : sources) {

			try {
				tasks.addAll(importTasksFromTXT(source, filter));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			tasks.toXMLFile(dst);
			System.out.println("\nWrite task list to " + dst.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Unable to create task list file->" + e.getMessage());
		}

		super.done();
	}

	@Override
	public void setupByNamespace(Namespace ns) {
		// TODO Auto-generated method stub

	}
}
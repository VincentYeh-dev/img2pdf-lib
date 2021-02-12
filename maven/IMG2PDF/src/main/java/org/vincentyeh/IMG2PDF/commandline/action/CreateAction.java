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
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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

	private static final String DEFV_PDF_AUTO_ROTATE = "YES";

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

	public CreateAction(String[] args) throws ParseException {
		this((new DefaultParser()).parse(setupOptions(), args));
	}

	public CreateAction(CommandLine cmd) {
		if (cmd.hasOption("pdf_size"))
			pdf_size = PageSize.getSizeFromString(cmd.getOptionValue("pdf_size"));
		else
			throw new ArgumentNotFoundException("pdf_size");

		if (cmd.hasOption("pdf_align"))
			pdf_align = new PageAlign(cmd.getOptionValue("pdf_align"));
		else
			throw new ArgumentNotFoundException("pdf_align");

		if (cmd.hasOption("pdf_direction"))
			pdf_direction = PageDirection.getDirectionFromString(cmd.getOptionValue("pdf_direction"));
		else
			throw new ArgumentNotFoundException("pdf_direction");

		if (cmd.hasOption("pdf_auto_rotate"))
			pdf_auto_rotate = cmd.getOptionValue("pdf_auto_rotate").equals("YES");
		else
			throw new ArgumentNotFoundException("pdf_auto_rotate");

		if (cmd.hasOption("pdf_sortby"))
			pdf_sortby = Sortby.getByString(cmd.getOptionValue("pdf_sortby"));
		else
			throw new ArgumentNotFoundException("pdf_sortby");

		if (cmd.hasOption("pdf_order"))
			pdf_order = Order.getByString(cmd.getOptionValue("pdf_order"));
		else
			throw new ArgumentNotFoundException("pdf_order");

		pdf_owner_password = cmd.getOptionValue("pdf_owner_password");
		pdf_user_password = cmd.getOptionValue("pdf_user_password");

		if (cmd.hasOption("pdf_permission"))
			pdf_permission = new DocumentAccessPermission(cmd.getOptionValue("pdf_permission"));
		else
			throw new ArgumentNotFoundException("pdf_permission");

		if (cmd.hasOption("pdf_destination"))
			pdf_destination = cmd.getOptionValue("pdf_destination");
		else
			throw new ArgumentNotFoundException("pdf_destination");

		if (cmd.hasOption("list_destination"))
			list_destination = cmd.getOptionValue("list_destination");
		else
			throw new ArgumentNotFoundException("list_destination");

		if (cmd.hasOption("filter"))
			this.filter = new FileFilterHelper(cmd.getOptionValue("filter"));
		else
			throw new ArgumentNotFoundException("filter");

		String[] str_sources = null;

		if (cmd.hasOption("source"))
			str_sources = cmd.getOptionValues("source");
		else
			throw new ArgumentNotFoundException("sources");

		sources = new File[str_sources.length];
		for (int i = 0; i < sources.length; i++) {
			System.out.println("sources checking....");
			sources[i] = new File(str_sources[i]);

			if (!sources[i].exists()) {
				System.err.println("File not found:" + sources[i].getAbsolutePath());
				continue;
			} else if (sources[i].isDirectory()) {
				System.err.println("Path should be a file:" + sources[i].getAbsolutePath());
				continue;
			} else {
				System.out.println("[Verified] " + sources[i].getAbsolutePath());
			}
		}
		System.out.println("CHECK DONE.");
	}

	public static Options setupOptions() {
		Options options = new Options();
		Option opt_pdf_size = createArgOption("pz", "pdf_size", "help_create_pdf_size");
		Option opt_pdf_align = createArgOption("pa", "pdf_align", "help_create_pdf_align");
		Option opt_pdf_direction = createArgOption("pdi", "pdf_direction", "help_create_pdf_direction");
		Option opt_pdf_auto_rotate = createArgOption("par", "pdf_auto_rotate", "help_create_pdf_auto_rotate");
		Option opt_pdf_sortby = createArgOption("ps", "pdf_sortby", "help_create_pdf_sortby");
		Option opt_pdf_order = createArgOption("po", "pdf_order", "help_create_pdf_order");
		Option opt_pdf_owner_password = createArgOption("popwd", "pdf_owner_password",
				"help_create_pdf_owner_password");
		Option opt_pdf_user_password = createArgOption("pupwd", "pdf_user_password", "help_create_pdf_user_password");
		Option opt_pdf_permission = createArgOption("pp", "pdf_permission", "help_create_pdf_permission");
		Option opt_pdf_destination = createArgOption("pdst", "pdf_destination", "help_create_pdf_destination");
		Option opt_filter = createArgOption("f", "filter", "help_import_filter");
		Option opt_sources = createArgOption("src", "source", "help_import_source");
		Option opt_list_destination = createArgOption("ldst", "list_destination", "help_create_list_destination");

		options.addOption(opt_pdf_size);
		options.addOption(opt_pdf_align);
		options.addOption(opt_pdf_direction);
		options.addOption(opt_pdf_auto_rotate);
		options.addOption(opt_pdf_sortby);
		options.addOption(opt_pdf_order);
		options.addOption(opt_pdf_owner_password);
		options.addOption(opt_pdf_user_password);
		options.addOption(opt_pdf_permission);
		options.addOption(opt_pdf_destination);
		options.addOption(opt_filter);
		options.addOption(opt_sources);
		options.addOption(opt_list_destination);

		Option opt_mode = new Option("m", "mode", true, "mode");
		options.addOption(opt_mode);

		return options;
//
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

	}

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

}
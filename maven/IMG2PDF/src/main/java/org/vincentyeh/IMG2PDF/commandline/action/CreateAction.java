package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.vincentyeh.IMG2PDF.commandline.action.exception.ArgumentNotFoundException;
import org.vincentyeh.IMG2PDF.commandline.action.exception.HelperException;
import org.vincentyeh.IMG2PDF.commandline.action.exception.IllegalParserArgumentException;
import org.vincentyeh.IMG2PDF.commandline.action.exception.SourceFolderException;
import org.vincentyeh.IMG2PDF.commandline.action.exception.SourceFolderIsFileException;
import org.vincentyeh.IMG2PDF.commandline.action.exception.SourceFolderNotFoundException;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sequence;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.file.text.UTF8InputStream;
import org.vincentyeh.IMG2PDF.pdf.document.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.NameFormatter;

public class CreateAction extends AbstractAction {

	private static final String DEF_PDF_SIZE = "A4";
	private static final String DEF_PDF_ALIGN = "CENTER-CENTER";
	private static final String DEF_PDF_DIRECTION = "Vertical";
	private static final String DEFV_PDF_SORTBY = "NAME";
	private static final String DEFV_PDF_ORDER = "INCREASE";
	private static final String DEFV_PDF_AUTO_ROTATE = "NO";
	private static final String DEFV_PDF_FILTER = "[^\\.]*\\.(png|PNG|jpg|JPG)";

	protected final PageSize pdf_size;
	protected final PageAlign pdf_align;
	protected final PageDirection pdf_direction;
	protected final boolean pdf_auto_rotate;
	protected final Sortby pdf_sortby;
	protected final Sequence pdf_sequence;
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

	public CreateAction(CommandLine cmd)
			throws HelperException, ArgumentNotFoundException, IllegalParserArgumentException {
		if (cmd.hasOption("-h"))
			throw new HelperException(setupOptions());
		
		try {
			pdf_size = PageSize.getByString(cmd.getOptionValue("pdf_size", DEF_PDF_SIZE));
		} catch (IllegalArgumentException e) {
			throw new IllegalParserArgumentException(e.getMessage());
		}
		
		if (pdf_size == null)
			throw new ArgumentNotFoundException("pdf_size");

		pdf_align = new PageAlign(cmd.getOptionValue("pdf_align", DEF_PDF_ALIGN));

		if (pdf_align == null)
			throw new ArgumentNotFoundException("pdf_align");

		pdf_direction = PageDirection.getByString(cmd.getOptionValue("pdf_direction", DEF_PDF_DIRECTION));

		if (pdf_direction == null)
			throw new ArgumentNotFoundException("pdf_direction");

		pdf_auto_rotate = cmd.getOptionValue("pdf_auto_rotate", DEFV_PDF_AUTO_ROTATE).equals("YES");

		
		
		pdf_sortby = Sortby.getByString(cmd.getOptionValue("pdf_sortby", DEFV_PDF_SORTBY));
		if (pdf_sortby == null)
			throw new ArgumentNotFoundException("pdf_sortby");

		pdf_sequence = Sequence.getByString(cmd.getOptionValue("pdf_order", DEFV_PDF_ORDER));
		if (pdf_sequence == null)
			throw new ArgumentNotFoundException("pdf_order");

		pdf_owner_password = cmd.getOptionValue("pdf_owner_password");
		pdf_user_password = cmd.getOptionValue("pdf_user_password");

		pdf_permission = new DocumentAccessPermission(cmd.getOptionValue("pdf_permission", "11"));
		if (pdf_permission == null)
			throw new ArgumentNotFoundException("pdf_permission");

		pdf_destination = cmd.getOptionValue("pdf_destination");
		if (pdf_destination == null)
			throw new ArgumentNotFoundException("pdf_destination");

		list_destination = cmd.getOptionValue("list_destination");
		if (list_destination == null)
			throw new ArgumentNotFoundException("list_destination");

		filter = new FileFilterHelper(cmd.getOptionValue("filter", DEFV_PDF_FILTER));
		if (filter == null)
			throw new ArgumentNotFoundException("filter");

		String[] str_sources = cmd.getOptionValues("source");
		if (str_sources == null) {
			str_sources = new String[0];
		}

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
		Option opt_help = createOption("h", "help", "help_create_pdf_size");
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
//		opt_sources.setRequired(true);

		Option opt_list_destination = createArgOption("ldst", "list_destination", "help_create_list_destination");
//		opt_list_destination.setRequired(true);

		options.addOption(opt_help);
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
			ImgFile img = new ImgFile(f.getAbsolutePath(), pdf_sortby, pdf_sequence);
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
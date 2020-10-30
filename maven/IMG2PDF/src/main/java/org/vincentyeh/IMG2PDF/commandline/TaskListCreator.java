package org.vincentyeh.IMG2PDF.commandline;

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

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * 
 * 
 * @author VincentYeh
 */
public class TaskListCreator {

	private static class TaskArgument {
		final Align align;
		final Sortby sortby;
		final Order order;
		final Size size;
		final String dst;
		final String owner_pwd;
		final String user_pwd;
		final ArrayList<String> sources;
		final String list_output;
		final PageDirection defaultDirection;
		final boolean autoRotate;
		final DocumentAccessPermission dap;
		
		public TaskArgument(ArgumentContainer opt) {
			sources = opt.source;
			list_output = opt.list_output.get(0);
			sortby = opt.sortby;
			order = opt.order;
			align = new Align(opt.align.get(0));
			size = opt.size;
			dst = opt.destination.get(0);
			defaultDirection = opt.defaultDirection;
			dap=parsePermissionString(opt.permission);
			autoRotate = opt.autoRotate;
			owner_pwd = opt.owner_password == null ? null : opt.owner_password.get(0);
			user_pwd = opt.user_password == null ? null : opt.user_password.get(0);
		}

		private static class ArgumentContainer {

			@Arg(dest = "sortby")
			public Sortby sortby;

			@Arg(dest = "order")
			public Order order;

			@Arg(dest = "size")
			public Size size;

			@Arg(dest = "owner_password")
			public ArrayList<String> owner_password;

			@Arg(dest = "user_password")
			public ArrayList<String> user_password;

			@Arg(dest = "align")
			public ArrayList<String> align;

			@Arg(dest = "destination")
			public ArrayList<String> destination;

			@Arg(dest = "list_output")
			public ArrayList<String> list_output;

			@Arg(dest = "source")
			public ArrayList<String> source;
			
			@Arg(dest = "permission")
			public ArrayList<String> permission;

			@Arg(dest = "direction")
			public PageDirection defaultDirection;

			@Arg(dest = "auto_rotate")
			public boolean autoRotate;
			

		}
		private DocumentAccessPermission parsePermissionString(ArrayList<String> str) {
			DocumentAccessPermission dap=new DocumentAccessPermission();
			if(str==null)return dap;
			dap.setCanPrint(str.get(0).contains("p"));
			dap.setCanModify(str.get(0).contains("m"));
			return dap;
		}
	}

	private ConfigureTaskList tasks = new ConfigureTaskList();

	public TaskListCreator(String args) throws IOException {
		this(args.trim().split("\\s"));
	}

	TaskListCreator(String[] args) throws IOException {
		ArgumentParser parser = createArgParser();
		TaskArgument taskArgs = Arg2Values(parser, args);
		for (String source : taskArgs.sources) {
			tasks.addAll(importTasksFromTXT(new File(source), taskArgs));
		}

		tasks.toXMLFile(new File(taskArgs.list_output));

	}

	public ConfigureTaskList getTaskList() {
		return tasks;
	}

	/**
	 * Import multiple path of each directory,and convert it to task.
	 * 
	 * @param file Source file.
	 * @return The List of Task.
	 * @throws IOException None description.
	 */
	private ConfigureTaskList importTasksFromTXT(File file, TaskArgument arguments) throws IOException {

		UTF8InputStream uis = new UTF8InputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(uis.getInputStream(), "UTF-8"));
		ConfigureTaskList tasks = new ConfigureTaskList();
		String buf = "";
		while (buf != null) {
			buf = reader.readLine();
			if (buf != null && !buf.isEmpty()) {
//				trim():	private invisible character pass to the constructor 
//				of File and make file not exists.
				System.out.println(buf);
				File dir = new File(buf);
				if (!dir.exists())
					throw new FileNotFoundException(dir.getName() + " not found.");

				if (!dir.isDirectory())
					throw new RuntimeException(dir.getName() + " is not the directory.");

				NameFormatter nf = new NameFormatter(arguments.dst, dir);
				FileFilterHelper ffh = createImageFilter(0);
				ConfigureTask task = new ConfigureTask(dir.listFiles(ffh), nf.getConverted(), arguments.owner_pwd,
						arguments.user_pwd,arguments.dap, arguments.sortby, arguments.order, arguments.align, arguments.size,
						arguments.defaultDirection, arguments.autoRotate);
				tasks.add(task);
			}
		}
		uis.close();
		reader.close();
		return tasks;
	}

	private TaskArgument Arg2Values(ArgumentParser parser, String[] args) {
		TaskArgument.ArgumentContainer opt = new TaskArgument.ArgumentContainer();

		try {
			parser.parseArgs(args, opt);

		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
		TaskArgument arguments = new TaskArgument(opt);

		System.out.printf("source:%s\n", arguments.sources);
		System.out.printf("output:%s\n", arguments.list_output);
		System.out.printf("\tsortby:%s\n", arguments.sortby.getStr());
		System.out.printf("\torder:%s\n", arguments.order.getStr());
		System.out.printf("\talign:%s\n", opt.align.get(0));
		System.out.printf("\tsize:%s\n", arguments.size.getStr());
		System.out.printf("\tdestination:%s\n", arguments.dst);
		System.out.printf("\towner password:%s\n", arguments.owner_pwd);
		System.out.printf("\tuser password:%s\n", arguments.user_pwd);
		System.out.printf("---------------------\n");
		return arguments;
	}

	private ArgumentParser createArgParser() {

		ArgumentParser parser = ArgumentParsers.newFor("TASKCREATOR").build().defaultHelp(true)
				.description("Create PDF Task");
		parser.version("");

		parser.addArgument("-s", "--sortby").type(Sortby.class).help("Merge all image files in Folder");

		parser.addArgument("-dd", "--direction").type(PageDirection.class).help("Direction of each page");

		parser.addArgument("-rot", "--auto_rotate").type(Boolean.class).help("auto rotate each page.");

		parser.addArgument("-odr", "--order").type(Order.class)
				.help("order by increasing(0,1,2,3) or decreasing(3,2,1,0) value");

		parser.addArgument("-sz", "--size").type(Size.class)
				.help("PDF each page size.\ntype DEPEND to set each page size depend on each image size");

		parser.addArgument("-ownpwd", "--owner_password").metavar("ownerpassword").nargs(1).help("PDF owner password");
		parser.addArgument("-usepwd", "--user_password").metavar("userpassword").nargs(1).help("PDF user password");

		parser.addArgument("-a", "--align").metavar("TopBottom|LeftRight").nargs(1).help("alignment of page of PDF.");
		parser.addArgument("-d", "--destination").metavar("destination").nargs(1).help("destination of converted file");
		parser.addArgument("-lo", "--list_output").metavar("destination").nargs(1).help("Output task list(*.XML)");
		parser.addArgument("-p", "--permission").nargs(1).help("permission of document.");

		parser.addArgument("source").nargs("*").help("File to convert");
		return parser;
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

package org.vincentyeh.IMG2PDF.commandline;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.file.PDFFile;
import org.vincentyeh.IMG2PDF.file.PDFFile.Align;
import org.vincentyeh.IMG2PDF.file.PDFFile.Size;
import org.vincentyeh.IMG2PDF.file.text.UTF8InputStream;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.NameFormatter;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * 
 * 
 * @author VincentYeh
 */
public class TaskListCreator {
	private Align align;
	private Sortby sortby;
	private Order order;
	private Size size;
	private String dst;
	private String owner_pwd;
	private String user_pwd;
	private ArrayList<String> lists;
	private String list_output = null;
	TaskList tasks=new TaskList();
	
	TaskListCreator(String args) throws IOException {
		this(args.trim().split("\\s"));
	}

	TaskListCreator(String[] args) throws IOException {
		ArgumentParser parser = createArgParser();
		Arg2Values(parser, args);
		
		for (String list : lists) {
			tasks.addAll(importTasksFromTXT(new File(list)));
		}
		
		tasks.toXMLFile(new File(list_output));

	}

	public static void main(String[] args) throws Exception {
		new TaskListCreator(args);
	}

	/**
	 * Import multiple path of each directory,and convert it to task.
	 * 
	 * @param file Source file.
	 * @return The List of Task.
	 * @throws IOException None description.
	 */
	private TaskList importTasksFromTXT(File file) throws IOException {

		UTF8InputStream uis = new UTF8InputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(uis, "UTF-8"));
		TaskList tasks = new TaskList();
		String buf = "";
		while (buf != null) {
			buf = reader.readLine();
			if (buf != null && !buf.isEmpty()) {
//				trim():	private invisible character pass to the constructor 
//				of File and make file not exists.
				System.out.println(buf);
				File dir = new File(buf);

				NameFormatter nf = new NameFormatter(dst, dir);
				FileFilterHelper ffh = createImageFilter(0);
				Task task = new Task(dir.listFiles(ffh), nf.getConverted(), owner_pwd, user_pwd, sortby, order, align,
						size);
				tasks.add(task);

			}
		}
		reader.close();
		return tasks;
	}

	private void Arg2Values(ArgumentParser parser, String[] args) {
		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
		lists = new ArrayList<String>(ns.<String>getList("source"));
		size = PDFFile.Size.getSizeFromString(ns.getString("size"));
		dst = ns.<String>getList("destination").get(0);
		list_output = ns.<String>getList("list_output").get(0);

		if (ns.getString("sortby") == null) {
			System.err.println("sort by rule need to provide");
			System.exit(0);
		}

		sortby = Sortby.getByStr(ns.getString("sortby"));
		order = Order.getByStr(ns.getString("order"));
		String str_align = ns.<String>getList("align").get(0);

		if (ns.<String>getList("owner_password") != null)
			owner_pwd = ns.<String>getList("owner_password").get(0);
		else
			owner_pwd = null;

		if (ns.<String>getList("user_password") != null)
			user_pwd = ns.<String>getList("user_password").get(0);
		else
			user_pwd = null;

		align = new Align(str_align);

		for (String source : lists) {
			System.out.printf("source:%s\n", source);
		}
		System.out.printf("output:%s\n", list_output);
		System.out.printf("\talign:%s\n", str_align);
		System.out.printf("\tsize:%s\n", size.getStr());
		System.out.printf("\tdestination:%s\n", dst);
		System.out.printf("\towner password:%s\n", owner_pwd);
		System.out.printf("\tuser password:%s\n", user_pwd);
		System.out.printf("---------------------\n");

	}

	private ArgumentParser createArgParser() {

		ArgumentParser parser = ArgumentParsers.newFor("TASKCREATOR").build().defaultHelp(true)
				.description("Create PDF Task");
		parser.version("");
		parser.addArgument("-s", "--sortby").choices(Sortby.valuesStr()).help("Merge all image files in Folder");

		parser.addArgument("-odr", "--order").choices(Order.valuesStr())
				.help("order by increasing(0,1,2,3) or decreasing(3,2,1,0) value");

		parser.addArgument("-sz", "--size").choices(Size.valuesStr())
				.help("PDF each page size.\ntype DEPEND to set each page size depend on each image size");

		parser.addArgument("-ownpwd", "--owner_password").metavar("ownerpassword").nargs(1).help("PDF owner password");
		parser.addArgument("-usepwd", "--user_password").metavar("userpassword").nargs(1).help("PDF user password");

		parser.addArgument("-a", "--align").metavar("TopBottom|LeftRight").nargs(1).help("alignment of page of PDF.");
		parser.addArgument("-d", "--destination").metavar("destination").nargs(1).help("destination of converted file");
		parser.addArgument("-lo", "--list_output").metavar("destination").nargs(1).help("Output task list(*.XML)");
		parser.addArgument("source").nargs("*").help("File to convert");
		return parser;
	}

	FileFilterHelper createImageFilter(int condition) {
		FileFilterHelper ffh = new FileFilterHelper(
				condition | FileFilterHelper.CONDITION_IS_FILE | FileFilterHelper.CONDITION_EXT_EQUALS);
		ffh.appendExtSLT("JPG");
		ffh.appendExtSLT("jpg");
		ffh.appendExtSLT("PNG");
		ffh.appendExtSLT("png");
		return ffh;
	}
}

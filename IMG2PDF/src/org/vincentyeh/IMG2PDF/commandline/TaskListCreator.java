package org.vincentyeh.IMG2PDF.commandline;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.PDFFile;
import org.vincentyeh.IMG2PDF.file.text.UTF8InputStream;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.NameFormatter;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class TaskListCreator {
	static int sortby, order, size, align;
	static boolean merge;
	static String dst;
	static String owner_pwd;
	static String user_pwd;
	static ArrayList<String> lists;
	static String list_output = null;

	public static void main(String[] args) throws Exception {
		ArgumentParser parser = createArgParser();
		Arg2Values(parser, args);
		for (String list : lists) {
			TaskList tasks = importTasksFromTXT(new File(list));
			tasks.toXMLFile(new File(list_output));
		}

	}

	static TaskList importTasksFromTXT(File file) throws Exception {
		UTF8InputStream uis = new UTF8InputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(uis, "UTF-8"));
		TaskList tasks = new TaskList();
		String buf = "";
		while (buf != null) {
			buf = reader.readLine();
			if (buf != null && !buf.isEmpty()) {
				File dir=new File(buf);
				NameFormatter nf = new NameFormatter(dst,dir);
				FileFilterHelper ffh = createImageFilter(0);
				Task task = new Task(dir.listFiles(ffh),nf.getConverted(), 
						owner_pwd, user_pwd, sortby, order, align, size);
				tasks.add(task);

			}
		}
		reader.close();
		return tasks;
	}

	public static void Arg2Values(ArgumentParser parser, String[] args) {
		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
		lists = new ArrayList<String>(ns.<String>getList("source"));

		merge = ns.getString("merge").equals("yes");
		String str_size = ns.getString("size");
		size = PDFFile.sizeTranslator(str_size);
		dst = ns.<String>getList("destination").get(0);
		list_output = ns.<String>getList("list_output").get(0);

		if (ns.getString("sortby") == null) {
			System.err.println("sort by rule need to provide");
			System.exit(0);
		}

		switch (ns.getString("sortby")) {
		case "name":
			sortby = ImgFile.SORTBY_NAME;
			break;
		case "date":
			sortby = ImgFile.SORTBY_DATE;
			break;
		default:
			System.err.println("sort by rule need to provide");
			sortby = -1;
			System.exit(0);
			break;
		}

		switch (ns.getString("order")) {
		case "ics":
			order = ImgFile.ORDER_INCREASE;
			break;
		case "dcs":
			order = ImgFile.ORDER_DECREASE;
			break;
		default:
			System.err.println("order by rule need to provide");
			order = -1;
			System.exit(0);
			break;
		}

		if (ns.<String>getList("owner_password") != null)
			owner_pwd = ns.<String>getList("owner_password").get(0);
		else
			owner_pwd = null;

		if (ns.<String>getList("user_password") != null)
			user_pwd = ns.<String>getList("user_password").get(0);
		else
			user_pwd = null;
//		align = (PDFFile.ALIGN_CENTER & 0xf0) | (PDFFile.ALIGN_CENTER & 0x0f);
		align = PDFFile.ALIGN_FILL;

		System.out.printf("merge:%s\n", merge ? "yes" : "no");
		System.out.printf("size:%s\n", str_size);
		System.out.printf("destination:%s\n", dst);
		System.out.printf("owner password:%s\n", owner_pwd);
		System.out.printf("user password:%s\n", user_pwd);

	}

	static ArgumentParser createArgParser() {
		ArgumentParser parser = ArgumentParsers.newFor("TASKCREATOR").build().defaultHelp(true)
				.description("Create PDF Task");

		parser.addArgument("-m", "--merge").choices("yes", "no").setDefault("no")
				.help("Merge all image files in Folder");

		parser.addArgument("-s", "--sortby").choices("name", "date").help("Merge all image files in Folder");

		parser.addArgument("-odr", "--order").choices("ics", "dcs")
				.help("order by increasing(0,1,2,3) or decreasing(3,2,1,0) value");

		parser.addArgument("-sz", "--size")
				.choices("A0", "A1", "A2", "A3", "A4", "A5", "A6", "LEGAL", "LETTER", "DEPEND")
				.help("PDF each page size.\ntype DEPEND to set each page size depend on each image size");

		parser.addArgument("-ownpwd", "--owner_password").nargs(1).help("PDF owner password");
		parser.addArgument("-usepwd", "--user_password").nargs(1).help("PDF user password");
		parser.addArgument("-d", "--destination").nargs(1).help("Destination of converted file");
		parser.addArgument("-lo", "--list_output").nargs(1).help("Output task list(*.XML)");
		parser.addArgument("source").nargs("*").help("File to convert");
		return parser;
	}

	static FileFilterHelper createImageFilter(int condition) {
		FileFilterHelper ffh = new FileFilterHelper(
				condition | FileFilterHelper.CONDITION_IS_FILE | FileFilterHelper.CONDITION_EXT_EQUALS);
		ffh.appendExtSLT("JPG");
		ffh.appendExtSLT("jpg");
		ffh.appendExtSLT("PNG");
		ffh.appendExtSLT("png");
		return ffh;
	}
}

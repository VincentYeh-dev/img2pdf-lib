package org.vincentyeh.IMG2PDF.commandline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.vincentyeh.IMG2PDF.commandline.action.Action;
import org.vincentyeh.IMG2PDF.commandline.action.ImportAction;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.pdf.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.Size;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class MainProgram {

	public static void main(String[] args) throws IOException {

		File project_root = new File("").getAbsoluteFile().getParentFile().getParentFile();
		File sample_root = new File(project_root, "sample\\walk-animation");
		File taskslist_destination = new File(sample_root, "taskslist\\test.xml");
		File image_sources_dir = new File(sample_root, "image-sources").getAbsoluteFile();

		File sources_list = new File(sample_root, "dirlist.txt").getAbsoluteFile();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sources_list), "UTF-8"));
		writer.write(image_sources_dir.getAbsolutePath() + "\n\n");
		writer.close();

//		new TaskListCreator("-h");

		String str = "create " + "-pz A4 " + "-ps NUMERTIC " + "-pa CENTER|CENTER " + "-pdi Vertical " + "-par yes "
				+ "-po INCREASE " + "-pupwd 1234AAA " + "-popwd 1234AAA " + "-pp 11 " + "-pdst "
				+ sample_root.getAbsolutePath() + "\\output\\$PARENT{0}.pdf " + "-pdi Vertical " + "-ldst "
				+ taskslist_destination.getAbsolutePath() + " " + "import -s " + sources_list.getAbsolutePath();
		ArgumentParser parser = ArgumentParsers.newFor("prog").build();
		setupCreateParser(parser.addSubparsers().help("sub-command help"));

		Namespace ns = null;
		try {
			ns = parser.parseArgs(str.split("\\s"));
			Action action = (Action) ns.get("action");
			action.setupByNamespace(ns);
			action.start();

		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}

	}

	/**
	 * Function of "create parser"
	 * <ul>
	 * <li>import</li>
	 * <li>add</li>
	 * </ul>
	 * 
	 * @param parser
	 */
	static void setupCreateParser(Subparsers subparsers) {
		Subparser create_parser = subparsers.addParser("create").help("Type \"create -h\" to get more help.");
		create_parser.addArgument("-pz", "--pdf_size").required(true).type(Size.class)
				.help("PDF each page size.\ntype DEPEND to set each page size depend on each image size");
		create_parser.addArgument("-pa", "--pdf_align").type(Align.class).setDefault(new Align("CENTER|CENTER"))
				.metavar("TopBottom|LeftRight").help("alignment of page of PDF.");

		create_parser.addArgument("-pdi", "--pdf_direction").type(PageDirection.class).help("Direction of each page");

		create_parser.addArgument("-par", "--pdf_auto_rotate").setDefault(Boolean.TRUE)
				.type(Arguments.booleanType("yes", "no")).help("auto rotate each page.");

		create_parser.addArgument("-ps", "--pdf_sortby").type(Sortby.class).help("Merge all image files in Folder");

		create_parser.addArgument("-po", "--pdf_order").type(Order.class)
				.help("order by increasing(0,1,2,3) or decreasing(3,2,1,0) value");

		create_parser.addArgument("-popwd", "--pdf_owner_password").type(String.class).metavar("ownerpassword")
				.help("PDF owner password");
		create_parser.addArgument("-pupwd", "--pdf_user_password").type(String.class).metavar("userpassword")
				.help("PDF user password");
		create_parser.addArgument("-pp", "--pdf_permission").type(DocumentAccessPermission.class)
				.setDefault(new DocumentAccessPermission()).help("permission of document.");

		create_parser.addArgument("-pdst", "--pdf_destination").type(String.class).metavar("destination")
				.help("destination of converted file");

		create_parser.addArgument("-ldst", "--list_destination").type(String.class).metavar("destination")
				.help("Output task list(*.XML)");

		setupImportParser(create_parser.addSubparsers());

	}

	static void setupImportParser(Subparsers subparsers) {
		Subparser import_parser = subparsers.addParser("import").help("Type \"create -h\" to get more help.");
		import_parser.setDefault("action", new ImportAction());
		import_parser.addArgument("-s", "--source").nargs("*");
	}

}

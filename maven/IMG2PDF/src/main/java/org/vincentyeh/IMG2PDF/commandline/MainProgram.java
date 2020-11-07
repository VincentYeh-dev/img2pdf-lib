package org.vincentyeh.IMG2PDF.commandline;

import java.io.File;
import java.util.ArrayList;

import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.pdf.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.Size;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class MainProgram {
	private static class Argsss {

// 		for PDF----START---------------------
		@Arg(dest = "pdf_size")
		public Size size;

		@Arg(dest = "pdf_align")
		public String align;

		@Arg(dest = "pdf_direction")
		public PageDirection defaultDirection;

		@Arg(dest = "pdf_auto_rotate")
		public boolean autoRotate;

		@Arg(dest = "pdf_sortby")
		public Sortby sortby;

		@Arg(dest = "pdf_order")
		public Order order;

		@Arg(dest = "pdf_destination")
		public String destination;

		@Arg(dest = "pdf_owner_password")
		public String owner_password;

		@Arg(dest = "pdf_user_password")
		public String user_password;

		@Arg(dest = "pdf_permission")
		public String permission;

// 		for PDF----END---------------------

		@Arg(dest = "list_output")
		public String list_output;

		@Arg(dest = "source")
		public ArrayList<File> source;

	}

	public static void main(String[] args) {

//		String str = "create import "
//				+ "-t test_file\\club_dirlist.txt " 
//				+ "-pz A4 " 
//				+ "-ps NUMERTIC " 
//				+ "-pa CENTER|CENTER " 
//				+ "-pdi Vertical " 
//				+ "-par true"
//				+ "-po INCREASE " 
//				+ "-pdest D:\\$PARENT{0}.pdf " 
//				+ "-pupwd 1234AAA " 
//				+ "-popwd 1234AAA " 
//				+ "-pp pm "
//				+ "-lo test_file\\test.xml" ;
		String str = "create -pz A4 -pa CENTER|LEFT import -s test_file\\club_dirlist.txt";
		System.out.println(str);
		ArgumentParser parser = ArgumentParsers.newFor("prog").build();
		setupCreateParser(parser);

		try {
			System.out.println(parser.parseArgs(str.split("\\s")));
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
	static void setupCreateParser(ArgumentParser parser) {
		Subparsers subparsers = parser.addSubparsers().help("sub-command help");
		Subparser create_parser = subparsers.addParser("create").help("Type \"create -h\" to get more help.");

		create_parser.addArgument("-pz", "--pdf_size").required(true).type(Size.class)
				.help("PDF each page size.\ntype DEPEND to set each page size depend on each image size");
		create_parser.addArgument("-pa", "--pdf_align").type(Align.class).setDefault(new Align("CENTER|CENTER")).metavar("TopBottom|LeftRight")
				.help("alignment of page of PDF.");

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
		create_parser.addArgument("-pp", "--pdf_permission").type(DocumentAccessPermission.class).setDefault(new DocumentAccessPermission()).help("permission of document.");

		create_parser.addArgument("-pdst", "--pdf_destination").metavar("destination")
				.help("destination of converted file");

		create_parser.addArgument("-ldst", "--list_destination").metavar("destination").nargs(1)
				.help("Output task list(*.XML)");

		setupImportParser(create_parser);
		setupAddParser(create_parser);
	}

	static void setupImportParser(Subparser createparser) {
		Subparsers subparsers = createparser.addSubparsers();
		Subparser import_parser = subparsers.addParser("import").help("Type \"create -h\" to get more help.");
		import_parser.addArgument("-s", "--source");
	}

	static void setupAddParser(Subparser createparser) {
		Subparsers subparsers = createparser.addSubparsers();
		Subparser import_parser = subparsers.addParser("add").help("Type \"add -h\" to get more help.");

	}

}

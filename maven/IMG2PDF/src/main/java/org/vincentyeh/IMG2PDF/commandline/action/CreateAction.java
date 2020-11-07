package org.vincentyeh.IMG2PDF.commandline.action;

import java.util.ArrayList;

import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.pdf.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.Size;

import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public abstract class CreateAction implements Action{
		
		@Arg(dest = "pdf_size")
		protected Size pdf_size;

		@Arg(dest = "pdf_align")
		protected Align pdf_align;

		@Arg(dest = "pdf_direction")
		protected PageDirection pdf_direction;

		@Arg(dest = "pdf_auto_rotate")
		protected boolean pdf_auto_rotate;

		@Arg(dest = "pdf_sortby")
		protected Sortby pdf_sortby;

		@Arg(dest = "pdf_order")
		protected Order pdf_order;

		@Arg(dest = "pdf_owner_password")
		protected String pdf_owner_password;

		@Arg(dest = "pdf_user_password")
		protected String pdf_user_password;

		@Arg(dest = "pdf_permission")
		protected DocumentAccessPermission pdf_permission;

		@Arg(dest = "pdf_destination")
		protected String pdf_destination;

		@Arg(dest = "list_destination")
		protected String list_destination;
		
		@Override
		public void setupByNamespace(Namespace ns) {
			pdf_size = (Size) ns.get("pdf_size");
			pdf_align = (Align) ns.get("pdf_align");
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

			ImportAction.setupParser(create_parser.addSubparsers());

		}
		
	}
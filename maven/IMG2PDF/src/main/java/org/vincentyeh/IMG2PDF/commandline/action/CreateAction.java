package org.vincentyeh.IMG2PDF.commandline.action;

import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.pdf.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.Size;

import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.inf.Namespace;

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
		
	}
package org.vincentyeh.IMG2PDF.file;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.util.ImageProcess;

/**
 * 
 * The implement of conversion.It is the core class of conversion.
 * @author VincentYeh
 *
 */
public class PDFFile {
	public static final int ALIGN_RIGHT = 0x01;
	public static final int ALIGN_LEFT = 0x02;
	public static final int ALIGN_CENTER = 0x33;
	public static final int ALIGN_TOP = 0x10;
	public static final int ALIGN_BOTTOM = 0x20;
	public static final int ALIGN_FILL = 0x44;

//	public static final int SIZE_A0 = 0x01;
//	public static final int SIZE_A1 = 0x02;
//	public static final int SIZE_A2 = 0x03;
//	public static final int SIZE_A3 = 0x04;
//	public static final int SIZE_A4 = 0x05;
//	public static final int SIZE_A5 = 0x06;
//	public static final int SIZE_A6 = 0x07;
//	public static final int SIZE_LEGAL = 0x08;
//	public static final int SIZE_LETTER = 0x09;
//	public static final int SIZE_DEPEND_ON_IMG = 0x0A;

	private StandardProtectionPolicy spp = null;
	private final PDDocument doc;
	private final Task task;

	public PDFFile(Task task){
		if(task==null)
			throw new NullPointerException("task is null.");
		
		this.task = task;
		doc = new PDDocument();
		
		String owner_pwd = task.getOwner_pwd();
		String user_pwd = task.getUser_pwd();
		AccessPermission ap = new AccessPermission();

		if (owner_pwd.equals("#null") && !user_pwd.equals("#null"))
			setProtect("", user_pwd, ap);
		else if (user_pwd.equals("#null") && !owner_pwd.equals("#null"))
			setProtect(owner_pwd, "", ap);
		else if (!user_pwd.equals("#null") && !owner_pwd.equals("#null")) {
			setProtect(owner_pwd, user_pwd, ap);
		}

	}

	public void process() throws IOException {
		System.out.printf("Destination:%s\n\n", task.getDestination());
		ArrayList<ImgFile> imgs = task.getImgs();

		System.out.print("Start convert process..\n\n");
		int all = imgs.size();
		double perImg = (10. / all);
		double progress = 0;

		System.out.print("0%[");
		for (int i = 0; i < imgs.size(); i++) {
			progress += perImg;
			while (progress >= 1) {
				System.out.print("=");
				progress -= 1;
			}
			ImageProcess ip = new ImageProcess(imgs.get(i));

			doc.addPage(createImgPage(ip.read()));

		}
		System.out.print("]%100\n\n\n");

		if (spp != null) {
			System.out.println("Save with password");
			doc.protect(spp);
		}

		doc.save(task.getDestination());
		doc.close();
	}

	PDPage createImgPage(BufferedImage img) throws IOException {
		PDPage page = null;
		float img_width = img.getWidth();
		float img_height = img.getHeight();
//		float img_size_ratio = img_height / img_width;
		float position_x = 0;
		float position_y = 0;
		float out_width = 0, out_height = 0;
		Size size = task.getSize();

		if (size == Size.DEPEND_ON_IMG) {

			page = new PDPage(new PDRectangle(img_width, img_height));
			out_width = img_width;
			out_height = img_height;

		} else if (size != Size.DEPEND_ON_IMG) {
			page = new PDPage(size.getPdrectangle());
			img = imgRotate(img, page);
			img_width = img.getWidth();
			img_height = img.getHeight();
//			img_size_ratio = img_height / img_width;

			float[] received = null;
			if (page.getRotation() == 270) {
				received = rotate_position_compute(img, page);

			} else if (page.getRotation() == 0) {
				received = none_rotate_position_compute(img, page);
			}

			position_x = received[0];
			position_y = received[1];
			out_width = received[2];
			out_height = received[3];

		}

		PDImageXObject pdImageXObject = LosslessFactory.createFromImage(doc, img);

		PDPageContentStream contentStream = new PDPageContentStream(doc, page);

		contentStream.drawImage(pdImageXObject, position_x, position_y, out_width, out_height);
		contentStream.close();
		return page;
	}

	float[] none_rotate_position_compute(BufferedImage img, PDPage page) {
		float position_x = 0, position_y = 0;
		float out_width = 0, out_height = 0;
		float page_height = page.getBBox().getHeight();
		float page_width = page.getBBox().getWidth();
//		float page_size_ratio = page_height / page_width;

		float img_width = img.getWidth();
		float img_height = img.getHeight();
		float img_size_ratio = img_height / img_width;
//	-----
//position_x=> |   |
//	*----
//	 ^^^^^
//	 position_y

		if (task.getAlign() == ALIGN_FILL) {
			position_x = position_y = 0;
			out_height = page_height;
			out_width = page_width;
		} else if ((1 / img_size_ratio) * page_height <= page_width) {
			out_height = page_height;
			out_width = (img_width / img_height) * out_height;

			float x_space = page_width - out_width;
			int rl = task.getAlign() & 0x0F;

			switch (rl) {
			case ALIGN_LEFT:
				position_x = 0;
				break;
			case ALIGN_RIGHT:
				position_x = x_space;
				break;
			case ALIGN_CENTER & 0x0F:
				position_x = x_space / 2;
				break;

			}
		} else if (img_size_ratio * page_width <= page_height) {
			out_width = page_width;
			out_height = (img_height / img_width) * out_width;

			float y_space = page_height - out_height;

			int tb = task.getAlign() & 0xF0;
			switch (tb) {
			case ALIGN_BOTTOM:
				position_y = 0;
				break;
			case ALIGN_TOP:
				position_y = y_space;
				break;
			case ALIGN_CENTER & 0xF0:
				position_y = y_space / 2;
				break;

			}

		}

		float[] ret = { position_x, position_y, out_width, out_height };
		return ret;

	}

	float[] rotate_position_compute(BufferedImage img, PDPage page) {
		float position_x = 0, position_y = 0;
		float out_width = 0, out_height = 0;
		float r_page_width = page.getBBox().getHeight();
		float r_page_height = page.getBBox().getWidth();
//		float r_page_size_ratio = r_page_height / r_page_width;

		float img_width = img.getWidth();
		float img_height = img.getHeight();
//		float img_size_ratio = img_height / img_width;

		float r_img_width = img_height;
		float r_img_height = img_width;
		float r_img_size_ratio = r_img_height / r_img_width;

		float r_out_width = 0;
		float r_out_height = 0;
//	
//	--------
//	|      |<=position_x
//	-------*
//	   ^^^^^
//	   position_y

		if (task.getAlign() == ALIGN_FILL) {
			position_x = position_y = 0;
			r_out_height = r_page_height;
			r_out_width = r_page_width;
		} else if ((1 / r_img_size_ratio) * r_page_height <= r_page_width) {
			r_out_height = r_page_height;
			r_out_width = (r_img_width / r_img_height) * r_out_height;

			float x_space = r_page_width - r_out_width;

			int lr = task.getAlign() & 0x0F;

			switch (lr) {
			case ALIGN_LEFT:
				position_x = x_space;
				break;
			case ALIGN_RIGHT:
				position_x = 0;
				break;
			case ALIGN_CENTER & 0x0F:
				position_x = x_space / 2;
				break;

			}
		} else if (r_img_size_ratio * r_page_width <= r_page_height) {
			r_out_width = r_page_width;
			r_out_height = (r_img_height / r_img_width) * r_out_width;

			float y_space = r_page_height - r_out_height;

			int tb = task.getAlign() & 0xF0;

			switch (tb) {
			case ALIGN_BOTTOM:
				position_y = 0;
				break;
			case ALIGN_TOP:
				position_y = y_space;
				break;
			case ALIGN_CENTER & 0xF0:
				position_y = y_space / 2;
				break;
			}

		}

		out_width = r_out_height;
		out_height = r_out_width;
		float buf_y = position_y;

		position_y = position_x;
		position_x = buf_y;
		float[] ret = { position_x, position_y, out_width, out_height };
		return ret;
	}

	BufferedImage imgRotate(BufferedImage img, PDPage page) {

		float img_width = img.getWidth();
		float img_height = img.getHeight();
		float img_size_ratio = img_height / img_width;

		if (img_size_ratio > 1) {
			page.setRotation(0);
			return img;
		} else if (img_size_ratio == 1) {
			page.setRotation(0);
			return img;
		} else {
			page.setRotation(360 - 90);
			return ImgFile.rotateImg(img, 90);
		}

	}

	public void setProtect(String owner_pwd, String user_pwd, AccessPermission ap) {
		// Define the length of the encryption key.
		// Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
		int keyLength = 128;
		// Disable printing, everything else is allowed
		ap.setCanPrint(false);

		// Owner password (to open the file with all permissions) is "12345"
		// User password (to open the file but with restricted permissions, is empty
		// here)
		StandardProtectionPolicy spp = new StandardProtectionPolicy(owner_pwd, user_pwd, ap);
		spp.setEncryptionKeyLength(keyLength);
		spp.setPermissions(ap);
		setProtect(spp);
	}

	public void setProtect(StandardProtectionPolicy spp) {
		this.spp = spp;
	}
	public enum Size {
		A0("A0", PDRectangle.A0), A1("A1", PDRectangle.A1), A2("A2", PDRectangle.A2), A3("A3", PDRectangle.A3),
		A4("A4", PDRectangle.A4), A5("A5", PDRectangle.A5), A6("A6", PDRectangle.A6), LEGAL("LEGAL", PDRectangle.LEGAL),
		LETTER("LETTER", PDRectangle.LETTER), DEPEND_ON_IMG("DEPEND", null);

		private final String str;
		private final PDRectangle pdrectangle;

		Size(String str, PDRectangle pdrectangle) {
			this.str = str;
			this.pdrectangle = pdrectangle;
		}


		public static Size getSizeFromString(String str) {
			switch (str) {
			case "A0":
				return A0;
			case "A1":
				return A1;
			case "A2":
				return A2;
			case "A3":
				return A3;
			case "A4":
				return A4;
			case "A5":
				return A5;
			case "A6":
				return A6;
			case "LEGAL":
				return LEGAL;
			case "LETTER":
				return LETTER;
			case "DEPEND":
				return DEPEND_ON_IMG;
			default:
				throw new RuntimeException();
			}
		}
//
//		public int getSize() {
//			return num;
//		}

		public String getStrSize() {
			return str;
		}

		public PDRectangle getPdrectangle() {
			return pdrectangle;
		}
	}


}

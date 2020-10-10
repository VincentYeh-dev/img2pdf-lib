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
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.PDFFile.Align.LeftRightAlign;
import org.vincentyeh.IMG2PDF.file.PDFFile.Align.TopBottomAlign;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.util.ImageProcess;

/**
 * The implementation of Task.It is the core class of conversion.
 * 
 * @author VincentYeh
 *
 */
public class PDFFile {

	private StandardProtectionPolicy spp = null;
	private final PDDocument doc;

	/**
	 * Task that need to be process. It will be set on constructor.
	 */
	private final Task task;

	/**
	 * <b>The calculation of diff</b>
	 * <p>
	 * diff=abs((image height/image width)-(page height/page width))
	 * </p>
	 * 
	 * <b>Feature</b>
	 * <p>
	 * max_diff is the variable that can be set to prevent raw image over-deformed.
	 * </p>
	 * <p>
	 * The default value is less than 0.It do nothing when you don't set it to the
	 * value that more than 0.
	 * </p>
	 * <p>
	 * If you do that before execution of process() method,the program will throw a
	 * Exception that warn a user the sub is out of range when the diff larger than
	 * max_diff.
	 * </p>
	 * 
	 */
	private float max_diff = -1f;

	/**
	 * Create PDFFile with Task
	 * 
	 * @param task The task to do on PDFFile.
	 */
	public PDFFile(Task task) {
		if (task == null)
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

	/**
	 * Start the conversion of PDF.
	 * 
	 * @throws IOException When creating the image page.
	 */
	public void process() throws IOException {
		boolean isProtected = spp != null;
		System.out.printf("Destination:%s\n", task.getDestination());

		ArrayList<ImgFile> imgs = task.getImgs();

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
			try {
				ImageProcess ip = new ImageProcess(imgs.get(i));
				doc.addPage(createImgPage(ip.read()));
			} catch (IOException e) {
				doc.close();
				System.out.print("FAIL]");
				throw e;
			}

		}
		System.out.print("]%100");

		if (isProtected) {
			doc.protect(spp);
			System.out.print(" *");
		}
		System.out.println("\n\n");
		doc.save(task.getDestination());
		doc.close();
	}

	/**
	 * Draw Image to Page
	 * 
	 * @param img The image written to the page
	 * @return The page contain image
	 * @throws IOException Failure of drawing image to page
	 */
	PDPage createImgPage(BufferedImage img) throws IOException {
		PDPage page = null;
		float img_width = img.getWidth();
		float img_height = img.getHeight();

		float position_x = 0, position_y = 0;
		float out_width = 0, out_height = 0;
		Size size = task.getSize();

		if (size == Size.DEPEND_ON_IMG) {

			page = new PDPage(new PDRectangle(img_width, img_height));
			out_width = img_width;
			out_height = img_height;

		} else {
			page = new PDPage(size.getPdrectangle());

			PDRectangle rec = page.getBBox();
			float page_height = rec.getHeight();
			float page_width = rec.getWidth();

			int angle = 90;
			boolean isRotated = false;
			if ((img_height / img_width) >= 1) {
				page.setRotation(0);
			} else {
				page.setRotation(angle);
				img = ImgFile.rotateImg(img, -1 * angle);
				isRotated = true;
			}

			img_width = img.getWidth();
			img_height = img.getHeight();

			float[] f_size = sizeCalculate(img_height, img_width, page_height, page_width);
			out_height = f_size[0];
			out_width = f_size[1];

			float[] f_position = positionCalculate(isRotated, out_height, out_width, page_height, page_width);
			position_y = f_position[0];
			position_x = f_position[1];

		}

		PDImageXObject pdImageXObject = LosslessFactory.createFromImage(doc, img);

		PDPageContentStream contentStream = new PDPageContentStream(doc, page);

		contentStream.drawImage(pdImageXObject, position_x, position_y, out_width, out_height);
		contentStream.close();
		return page;
	}

	/**
	 * Compute the position of image by align value.
	 * 
	 * @param raw  The image that put into this page.
	 * @param page The Page that contain image.
	 * @return
	 *         <ol>
	 *         <li>x</li>
	 *         <li>y</li>
	 *         <li>image width</li>
	 *         <li>image height</li>
	 *         </ol>
	 */
	float[] positionCalculate(boolean isRotated, float img_height, float img_width, float page_height,
			float page_width) {

		float position_x = 0, position_y = 0;

		LeftRightAlign LRA = task.getAlign().getLRA();
		TopBottomAlign TBA = task.getAlign().getTBA();

		float x_space = page_width - img_width;
		float y_space = page_height - img_height;

		if (y_space == 0) {
			if (!isRotated) {
				switch (LRA) {
				case LEFT:
					position_x = 0;
					break;
				case RIGHT:
					position_x = x_space;
					break;
				case CENTER:
					position_x = x_space / 2;
					break;
				case FILL:
					break;
				default:
					break;
				}
			} else {
				switch (TBA) {
				case TOP:
					position_x = 0;
					break;
				case BOTTOM:
					position_x = x_space;
					break;
				case CENTER:
					position_x = x_space / 2;
					break;
				case FILL:
					break;
				default:
					break;

				}
			}
		} else if (x_space == 0) {

			if (!isRotated) {
				switch (TBA) {
				case BOTTOM:
					position_y = 0;
					break;
				case TOP:
					position_y = y_space;
					break;
				case CENTER:
					position_y = y_space / 2;
					break;
				case FILL:
					break;
				default:
					break;

				}
			} else {
				switch (LRA) {
				case LEFT:
					position_y = 0;
					break;
				case RIGHT:
					position_y = y_space;
					break;
				case CENTER:
					position_y = y_space / 2;
					break;
				case FILL:
					break;
				default:
					break;
				}
			}

		}
		return new float[] { position_y, position_x };
	}

	private float[] sizeCalculate(float img_height, float img_width, float page_height, float page_width) {

		float out_width = (img_width / img_height) * page_height;
		float out_height = (img_height / img_width) * page_width;
		if (out_height <= page_height) {
//			width fill
			out_width = page_width;
//			out_height = (img_height / img_width) * page_width;
		} else if (out_width <= page_width) {
//			height fill
			out_height = page_height;
//			out_width = (img_width / img_height) * page_height;
		}

		return new float[] { out_height, out_width };
	}

	/**
	 * Set password to the PDFFile.
	 * 
	 * @param owner_pwd Owner password
	 * @param user_pwd  User password
	 * @param ap        Access Permission
	 */
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

	/**
	 * Set the Protection of PDF File
	 * 
	 * @param spp StandardProtectionPolicy
	 */

	public void setProtect(StandardProtectionPolicy spp) {
		this.spp = spp;
	}

	/**
	 * set the limitation of image resizing
	 * 
	 * @param max_diff the max value to limit diff
	 */
	public void setMaxDiff(float max_diff) {
		this.max_diff = max_diff;
	}

	/**
	 * Size is the variable that define size of pages of PDFFile. It only can be
	 * create by using getSizeFromString(str) or directly specify enum
	 * 
	 * @author VincetYeh
	 */
	public enum Size {
		A0("A0", PDRectangle.A0), A1("A1", PDRectangle.A1), A2("A2", PDRectangle.A2), A3("A3", PDRectangle.A3),
		A4("A4", PDRectangle.A4), A5("A5", PDRectangle.A5), A6("A6", PDRectangle.A6), LEGAL("LEGAL", PDRectangle.LEGAL),
		LETTER("LETTER", PDRectangle.LETTER), DEPEND_ON_IMG("DEPEND", null);

		/**
		 * This is the String constant of Size.
		 */
		private final String str;

		/**
		 * This is the constant used to create the PDF.
		 */
		private final PDRectangle pdrectangle;

		Size(String str, PDRectangle pdrectangle) {
			this.str = str;
			this.pdrectangle = pdrectangle;
		}

		/**
		 * Create Size by String
		 * 
		 * @param str The String contain definition of Size.
		 * @return Size
		 */
		public static Size getSizeFromString(String str) throws IllegalSizeException {
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
				throw new IllegalSizeException(str);
			}
		}

		public String getStr() {
			return str;
		}

		public PDRectangle getPdrectangle() {
			return pdrectangle;
		}

		/**
		 * List all item of enum in String.
		 * 
		 * @return The array of enum in String.
		 */
		public static String[] valuesStr() {
			Size[] size_list = Size.values();
			String[] str_list = new String[size_list.length];
			for (int i = 0; i < str_list.length; i++) {
				str_list[i] = size_list[i].getStr();
			}
			return str_list;
		}

		public static class IllegalSizeException extends IllegalArgumentException {

			/**
			 * 
			 */
			private static final long serialVersionUID = 182897419820418934L;

			public IllegalSizeException(String str) {
				super(str + " isn't a type of size.");
				// TODO Auto-generated constructor stub
			}

		}

	}

	/**
	 * The class which define Alignment of page of PDF
	 * 
	 * @author vincent
	 */
	public static class Align {
		private final LeftRightAlign LRA;
		private final TopBottomAlign TBA;

		/**
		 * Create Align by enums.
		 * 
		 * @param LRA Left Right Align
		 * @param TBA Top Bottom Align
		 */
		public Align(LeftRightAlign LRA, TopBottomAlign TBA) {
			this.LRA = LRA;
			this.TBA = TBA;
		}

		/**
		 * Create Align by String. The str must be "TopBottomAlign|LeftRightAlign"
		 * format.
		 * 
		 * @param str Alignment
		 */
		public Align(String str) throws IllegalAlignException {
			String[] LR_TB_A = str.split("\\|");
			try {
				TBA = TopBottomAlign.getByStr(LR_TB_A[0]);
				LRA = LeftRightAlign.getByStr(LR_TB_A[1]);
			} catch (IllegalArgumentException e) {
				throw new IllegalAlignException(e.getMessage());
			}

		}

		public LeftRightAlign getLRA() {
			return LRA;
		}

		public TopBottomAlign getTBA() {
			return TBA;
		}

		@Override
		public String toString() {
			return String.format("%s|%s", TBA.getStr(), LRA.getStr());
		}

		public static class IllegalAlignException extends IllegalArgumentException {

			/**
			 * 
			 */
			private static final long serialVersionUID = 182897419820418934L;

			public IllegalAlignException(String str) {
				super(str + " isn't a type of align.");
				// TODO Auto-generated constructor stub
			}

		}

		public enum LeftRightAlign {
			RIGHT("RIGHT"), LEFT("LEFT"), CENTER("CENTER"), FILL("FILL");
			private String str;

			private LeftRightAlign(String str) {
				this.str = str;
			}

			public static LeftRightAlign getByStr(String str) throws IllegalArgumentException {
				switch (str) {
				case "RIGHT":
					return RIGHT;
				case "LEFT":
					return LEFT;
				case "CENTER":
					return CENTER;
				case "FILL":
					return FILL;
				default:
					throw new IllegalArgumentException(str);
				}
			}

			public String getStr() {
				return str;
			}

			public static String[] valuesStr() {
				Order[] list = Order.values();
				String[] str_list = new String[list.length];
				for (int i = 0; i < str_list.length; i++) {
					str_list[i] = list[i].getStr();
				}
				return str_list;
			}

		}

		public enum TopBottomAlign {
			TOP("TOP"), BOTTOM("BOTTOM"), CENTER("CENTER"), FILL("FILL");
			private String str;

			private TopBottomAlign(String str) {
				this.str = str;
			}

			public static TopBottomAlign getByStr(String str) throws IllegalArgumentException {
				switch (str) {
				case "TOP":
					return TOP;
				case "BOTTOM":
					return BOTTOM;
				case "CENTER":
					return CENTER;
				case "FILL":
					return FILL;
				default:
					throw new IllegalArgumentException(str);
				}
			}

			public String getStr() {
				return str;
			}

			public static String[] valuesStr() {
				Order[] list = Order.values();
				String[] str_list = new String[list.length];
				for (int i = 0; i < str_list.length; i++) {
					str_list[i] = list[i].getStr();
				}
				return str_list;
			}
		}
	}

}

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
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.util.ImageProcess;

/**
 * 
 * The implementation of Task.It is the core class of conversion.
 * 
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

	private StandardProtectionPolicy spp = null;
	private final PDDocument doc;
	
	
	/**
	 * Task that need to be process.
	 * It will be set on constructor.
	 */
	private final Task task;
	
	/**
	 * <h3>The calculation of diff</h3>
	 * diff=abs((image height/image width)-(page height/page width))
	 * 
	 * <h3>Feature</h3>
	 * <b>max_diff is the variable that can be set to prevent raw image over-deformed.</b><br \>
	 * The default value is <b>less than 0</b>.It do nothing when you don't set it to the value that more than 0<br \>
	 * <br \>If you do that before execution of process() method,<br \>the program will throw a Exception that warn a user the sub is out of range <b>when the diff>max_diff</b>.
	 *<br \>	
	 */
	private float max_diff=-1f;
	
	/**
	 * Create PDFFile with Task
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

	/**
	 * @param img The image written to the page
	 * @return The page contain image
	 * @throws IOException
	 */
	PDPage createImgPage(BufferedImage img) throws IOException {
		PDPage page = null;
		float img_width = img.getWidth();
		float img_height = img.getHeight();
		float position_x = 0,position_y = 0;
		float out_width = 0, out_height = 0;
		Size size = task.getSize();

		if (size == Size.DEPEND_ON_IMG) {

			page = new PDPage(new PDRectangle(img_width, img_height));
			out_width = img_width;
			out_height = img_height;

		} else if (size != Size.DEPEND_ON_IMG) {
			page = new PDPage(size.getPdrectangle());
			img_width = img.getWidth();
			img_height = img.getHeight();
			float img_size_ratio = img_height / img_width;
			int angle = 90;
			if (img_size_ratio >= 1) {
				page.setRotation(0);
			} else {
				page.setRotation(angle);
				img = ImgFile.rotateImg(img, -1 * angle);
			}
			
			float[] received = position_compute(img, page);
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
	
	/**
	 * Compute the position of image by align value;
	 * 
	 * @param raw The image that put into this page.
	 * @param page The Page that contain image.
	 * @return <ol><li>x</li><li>y</li><li>image width</li><li>image height</li></ol> 
	 */
	float[] position_compute(BufferedImage raw, PDPage page) {
		PDRectangle rec = page.getBBox();
		float real_page_width = rec.getWidth();
		float real_page_height = rec.getHeight();
		float page_size_ratio=real_page_height/real_page_width;
		
		float img_width = raw.getWidth();
		float img_height = raw.getHeight();
		float img_size_ratio = img_height / img_width;
		
		float position_x = 0, position_y = 0;
		float out_width = 0, out_height = 0;

		int lr = task.getAlign() & 0x0F;
		int tb = task.getAlign() & 0xF0;
		boolean isRotated=Math.abs(Math.sin(Math.toRadians(page.getRotation()))) == 1;
		
		if (task.getAlign() == ALIGN_FILL) {
			position_x = position_y = 0;
			float sub=Math.abs(img_size_ratio-page_size_ratio);
			if(max_diff>=0&&sub>max_diff) {
				throw new RuntimeException("sub is out of range.");
			}
			
			out_height = real_page_height;
			out_width = real_page_width;
		} else if ((1 / img_size_ratio) * real_page_height <= real_page_width) {
			out_height = real_page_height;
			out_width = (img_width / img_height) * out_height;

			float x_space = real_page_width - out_width;
			
			if (!isRotated) {
				switch (lr) {
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
			} else {
				switch (tb) {
				case ALIGN_BOTTOM:
					position_x = x_space;
					break;
				case ALIGN_TOP:
					position_x = 0;
					break;
				case ALIGN_CENTER & 0xF0:
					position_x = x_space / 2;
					break;

				}
			}
		} else if (img_size_ratio * real_page_width <= real_page_height) {
			out_width = real_page_width;
			out_height = (img_height / img_width) * out_width;

			float y_space = real_page_height - out_height;

			if (!isRotated) {
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
			} else {
				switch (lr) {
				case ALIGN_LEFT:
					position_y = 0;
					break;
				case ALIGN_RIGHT:
					position_y = y_space;
					break;
				case ALIGN_CENTER & 0x0F:
					position_y = y_space / 2;
					break;
				}
			}

		}

		float[] ret = { position_x, position_y, out_width, out_height };
		return ret;
	}

	/**
	 * Set password to the PDFFile.
	 * 
	 * @param owner_pwd Owner password
	 * @param user_pwd  User password
	 * @param ap		Access Permission
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

	public void setProtect(StandardProtectionPolicy spp) {
		this.spp = spp;
	}
	public void setMaxDiff(float max_diff) {
		this.max_diff = max_diff;
	}
	
	/**
	 * Size is the variable that define size of pages of PDFFile. 
	 * 
	 * @author vincent
	 */
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

		public String getStr() {
			return str;
		}

		public PDRectangle getPdrectangle() {
			return pdrectangle;
		}
		
		public static String[] valuesStr() {
			Size[] size_list=Size.values();
			String[] str_list=new String[size_list.length];
			for(int i=0;i<str_list.length;i++) {
				str_list[i]=size_list[i].getStr();
			}
			return str_list;
		}
		
	}

}

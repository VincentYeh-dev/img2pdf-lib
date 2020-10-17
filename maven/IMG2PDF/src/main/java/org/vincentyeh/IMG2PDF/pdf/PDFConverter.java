package org.vincentyeh.IMG2PDF.pdf;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.task.configured.ConfiguredTask;
import org.vincentyeh.IMG2PDF.util.ImageProcess;

/**
 * The implementation of Task.It is the core class of conversion.
 * 
 * @author VincentYeh
 *
 */
public class PDFConverter implements Callable<ImagesPDFDocument> {

	private final ImagesPDFDocument doc;

	/**
	 * Task that need to be process. It will be set on constructor.
	 */
//	private final ConfiguredTask task;
	private ArrayList<ImgFile> imgs;

	/**
	 * Create PDFFile with Task
	 * 
	 * @param task The task to do on PDFFile.
	 * @throws IOException
	 */
	public PDFConverter(ConfiguredTask task) throws IOException {
		if (task == null)
			throw new NullPointerException("task is null.");
		AccessPermission ap = new AccessPermission();
		doc = new ImagesPDFDocument(task.getSize(), task.getAlign());
		doc.protect(createProtectionPolicy(task.getOwner_pwd(), task.getUser_pwd(), ap));
		doc.setDestination(task.getDestination());
		imgs = task.getImgs();
	}

	/**
	 * Start the conversion of PDF.
	 * 
	 * @throws IOException When creating the image page.
	 */
	@Override
	public ImagesPDFDocument call() throws Exception {
		System.out.printf("Destination:%s\n", doc.getDestination());
		int all = imgs.size();
		double perImg = (10. / all);
		double progress = 0;

		System.out.print("0%[");
		for (int i = 0; i < all; i++) {
			progress += perImg;
			while (progress >= 1) {
				System.out.print("=");
				progress -= 1;
			}
			try {
				ImageProcess ip = new ImageProcess(imgs.get(i));
//				doc.addImgPage(ip.read());
				doc.addPage(createImgPage(ip.read()));
			} catch (Exception e) {
				doc.close();
				System.out.print("FAIL]");
				throw e;
			}

		}
		System.out.print("]%100");

		System.out.println("\n\n");
		return doc;
	}

	/**
	 * Draw Image to Page
	 * 
	 * @param img The image written to the page
	 * @return The page contain image
	 * @throws IOException Failure of drawing image to page
	 */
	ImagePage createImgPage(BufferedImage img) throws Exception {
		Size size = doc.getSize();
		ImagePage imgpage = null;
		if (size == Size.DEPEND_ON_IMG) {
			imgpage = new ImagePage(doc.getAlign(), img.getHeight(), img.getWidth());
		} else {
			imgpage = new ImagePage(doc.getAlign(), doc.getSize());
		}
		imgpage.setAutoRotate(true);
		imgpage.setImage(img);
		imgpage.drawImageToPage(doc);
		return imgpage;
	}

	/**
	 * Set password to the PDFFile.
	 * 
	 * @param owner_pwd Owner password
	 * @param user_pwd  User password
	 * @param ap        Access Permission
	 */
	private StandardProtectionPolicy createProtectionPolicy(String owner_pwd, String user_pwd, AccessPermission ap) {
		owner_pwd = owner_pwd.replace("#null", "");
		user_pwd = user_pwd.replace("#null", "");
		// Define the length of the encryption key.
		// Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
		int keyLength = 128;
		StandardProtectionPolicy spp = new StandardProtectionPolicy(owner_pwd, user_pwd, ap);
		spp.setEncryptionKeyLength(keyLength);
		spp.setPermissions(ap);
		return spp;
	}

}

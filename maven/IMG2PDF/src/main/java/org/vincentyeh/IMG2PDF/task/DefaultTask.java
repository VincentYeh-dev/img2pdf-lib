package org.vincentyeh.IMG2PDF.task;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.pdf.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.Size;

public class DefaultTask {
	protected Align align;
	protected Size size;
	protected String destination;
	protected ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
	protected PageDirection defaultDirection;
	protected boolean autoRotate;
	protected final DocumentAccessPermission dap;

	private final StandardProtectionPolicy spp;
	private final String owner_pwd;
	private final String user_pwd;

	/**
	 * Create the task by arguments.
	 * 
	 * @param files       Source files.Only can handle files.
	 * @param destination The destination of PDF output
	 * @param own         owner password
	 * @param user        user password
	 * @param sortby      files will be sorted by Name or Date
	 * @param order       order by increase or decrease value
	 * @param align       Where should Images of PDF be located on PDF.
	 * @param size        Which size of pages of PDF.
	 * @throws FileNotFoundException When file is not exists.
	 */
//	public DefaultTask(String destination, Align align, Size size, PageDirection defaultDirection, boolean autoRotate)
//			throws FileNotFoundException {
//		if (destination == null)
//			throw new NullPointerException("destination is null.");
//		this.align = align;
//		this.size = size;
//		this.destination = destination;
//		this.defaultDirection = defaultDirection;
//		this.autoRotate = autoRotate;
//		this.spp = null;
//		
//		this.owner_pwd = owner_pwd;
//		this.user_pwd = user_pwd;
//
//	}

	public DefaultTask(String owner_pwd, String user_pwd, DocumentAccessPermission dap) {

		this.owner_pwd = owner_pwd != null ? owner_pwd : "#null";
		this.user_pwd = user_pwd != null ? user_pwd : "#null";

		this.dap = dap;
		this.spp = createProtectionPolicy(owner_pwd, user_pwd, dap);

	}

	public Align getAlign() {
		return align;
	}

	public Size getSize() {
		return size;
	}

	public PageDirection getDefaultDirection() {
		return defaultDirection;
	}

	public String getDestination() {
		return destination;
	}

	public ArrayList<ImgFile> getImgs() {
		return imgs;
	}

	public StandardProtectionPolicy getSpp() {
		return spp;
	}

	public String getOwner_pwd() {
		return owner_pwd;
	}

	public String getUser_pwd() {
		return user_pwd;
	}

	public DocumentAccessPermission getDap() {
		return dap;
	}

	private StandardProtectionPolicy createProtectionPolicy(String owner_pwd, String user_pwd, AccessPermission ap) {
		owner_pwd = owner_pwd.replace("#null", "");
		user_pwd = user_pwd.replace("#null", "");
		// Define the length of the encryption key.
		// Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
		int keyLength = 128;
		StandardProtectionPolicy spp = new StandardProtectionPolicy(owner_pwd, user_pwd, ap);
		spp.setEncryptionKeyLength(keyLength);
//		spp.setPermissions(ap);
		return spp;
	}

}

package org.vincentyeh.IMG2PDF.pdf.converter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.pdf.document.ImagesPDFDocument;
import org.vincentyeh.IMG2PDF.pdf.page.ImagePage;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.util.ImageProcess;

/**
 * The implementation of Task.It is the core class of conversion.
 * 
 * @author VincentYeh
 *
 */
public class PDFConverter implements Callable<ImagesPDFDocument> {

	protected final ImagesPDFDocument doc;
	protected boolean isProtectedByPwd;
	private ConversionListener listener;
	private final Task task;

	/**
	 * Create PDFFile with Task
	 * 
	 * @param task The task to do on PDFFile.
	 * @throws IOException
	 */
	public PDFConverter(Task task) throws IOException {
		if (task == null)
			throw new NullPointerException("task is null.");
		this.task=task;
		doc = new ImagesPDFDocument(task.getSize(), task.getAlign());
		doc.protect(task.getSpp());
		doc.setDestination(task.getDestination());

//		not included in task------------------------------------

//		------------------------------------
	}

	/**
	 * Start the conversion of PDF.
	 * 
	 * @throws IOException When creating the image page.
	 */
	@Override
	public ImagesPDFDocument call() throws Exception {
//		before();
		ArrayList<ImgFile> imgs = task.getImgs();
		if (listener != null)
			listener.onConversionPreparing(task);
		for (int i = 0; i < imgs.size(); i++) {
			if (listener != null)
			listener.onConverting(i);
			try {
				ImageProcess ip = new ImageProcess(imgs.get(i));
				doc.addPage(createImgPage(ip.read()));
			} catch (Exception e) {
				if (listener != null)
					listener.onConversionFail(i, e);
				doc.close();
				throw e;
			}
		}

		if (listener != null)
			listener.onConversionComplete();
		return doc;
	}

	/**
	 * Draw Image to Page
	 * 
	 * @param img The image written to the page
	 * @return The page contain image
	 * @throws IOException Failure of drawing image to page
	 */
	protected ImagePage createImgPage(BufferedImage img) throws Exception {
		PageSize size = doc.getSize();
		ImagePage imgpage = null;
		if (size == PageSize.DEPEND_ON_IMG) {
			imgpage = new ImagePage(doc.getAlign(), img);
		} else {
			imgpage = new ImagePage(doc.getAlign(), doc.getSize(), task.getAutoRotate(), task.getDefaultDirection(),
					img);
		}
		imgpage.drawImageToPage(doc);
		return imgpage;
	}

	public void setListener(ConversionListener listener) {
		this.listener = listener;
	}

}

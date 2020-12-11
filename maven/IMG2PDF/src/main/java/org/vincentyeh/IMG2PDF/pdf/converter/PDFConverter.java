package org.vincentyeh.IMG2PDF.pdf.converter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.pdf.document.ImagesPDFDocument;
import org.vincentyeh.IMG2PDF.pdf.page.ImagePage;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.task.Task;

/**
 * The core of this program. At first,this class will be initialized by task.
 * When call() being called by other program,this program will start conversion
 * and finally return ImagesPDFDocument.
 * 
 * @author VincentYeh
 */
public class PDFConverter implements Callable<ImagesPDFDocument> {

	private final ImagesPDFDocument doc;
//	private boolean isProtectedByPwd;
	private ConversionListener listener;
	private final Task task;

	/**
	 * Setup the pdf converter.
	 * 
	 * @param task
	 * @throws IOException
	 */
	public PDFConverter(Task task) throws IOException {
		if (task == null)
			throw new NullPointerException("task is null.");
		this.task = task;
		doc = new ImagesPDFDocument();
		doc.protect(task.getSpp());
		doc.setDestination(task.getDestination());
	}

	/**
	 * Start conversion process.
	 * If listener isn't null,the return value will be null and 
	 * call onImageReadFail or onConversionFail.
	 * 
	 * If listener is null,call() will throw the exception.
	 * 
	 */
	@Override
	public ImagesPDFDocument call() throws Exception {
		ArrayList<ImgFile> imgs = task.getImgs();
		if (listener != null)
			listener.onConversionPreparing(task);

		for (int i = 0; i < imgs.size(); i++) {
			if (listener != null)
				listener.onConverting(i);

			BufferedImage image = null;
			try {
				image = ImageIO.read(imgs.get(i));
				
//				if(1==1)
//					throw new IOException("Image not support");
				
				
			} catch (IOException e) {
				closeDocument();
				if (listener != null) {
					listener.onImageReadFail(i, e);
					return null;
				} else {
					throw e;
				}
			}

			try {
				doc.addPage(createImgPage(image));
				
//				if(1==1)
//					throw new IOException("Conversion fail");
				
			} catch (Exception e) {
				closeDocument();
				if (listener != null) {
					listener.onConversionFail(i, e);
					return null;
				} else {
					throw e;
				}
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
	 * @throws Exception
	 */
	private ImagePage createImgPage(BufferedImage img) throws Exception  {
		PageSize size = task.getSize();
		ImagePage imgpage = null;
		if (size == PageSize.DEPEND_ON_IMG) {
			imgpage = new ImagePage(task.getAlign(), img);
		} else {
			imgpage = new ImagePage(task.getAlign(), task.getSize(), task.getAutoRotate(), task.getDefaultDirection(),
					img);
		}
		imgpage.drawImageToPage(doc);
		return imgpage;
	}

	/**
	 * Close PDF Document.
	 */
	private void closeDocument() {
		try {
			if (doc != null)
				doc.close();
		} catch (IOException ignore) {
			ignore.printStackTrace();
		}
	}

	/**
	 * Setup the listener.
	 * 
	 * @param listener ConversionListener
	 */
	public void setListener(ConversionListener listener) {
		this.listener = listener;
	}

}

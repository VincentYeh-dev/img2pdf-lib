package org.vincentyeh.IMG2PDF.pdf.converter;

import java.io.IOException;

import org.vincentyeh.IMG2PDF.task.Task;


/**
 * This interface can listen the event when conversion started.
 * @author vincent
 */
public interface ConversionListener {
	
	/**
	 * When the task is just loaded to the converter.
	 * @param task imported Task
	 */
	public void onConversionPreparing(Task task);
	
	/**
	 * When the single image written to a page.
	 * @param index the index of image files
	 */
	public void onConverting(int index);
	
	/**
	 * When the conversion is done.
	 */
	public void onConversionComplete();
	
	/**
	 * When image can't be read by program.
	 * @param index the index of image files
	 * @param e exception of reading image
	 */
	public void onImageReadFail(int index,IOException e);
	/**
	 * When conversion fail.
	 * @param index
	 * @param e exception
	 */
	public void onConversionFail(int index,Exception e);
}

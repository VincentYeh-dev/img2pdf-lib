package org.vincentyeh.IMG2PDF.converter.listener;

import java.io.File;

import org.vincentyeh.IMG2PDF.task.Task;


/**
 * This interface can listen the event when conversion started.
 * @author vincent
 */
public interface ConversionInfoListener {
	
	/**
	 * When the task is just loaded to the converter.
	 * @param task imported Task
	 */
	void onConversionPreparing(Task task);
	
	/**
	 * When the single image written to a page.
	 * @param index the index of image files
	 */
	void onConverting(int index, File file);
	
	/**
	 * When the conversion is done.
	 */
	void onConversionComplete(File dst);

}

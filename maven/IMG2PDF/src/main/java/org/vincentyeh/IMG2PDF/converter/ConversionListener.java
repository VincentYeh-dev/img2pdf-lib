package org.vincentyeh.IMG2PDF.converter;

import java.io.File;
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
	
	/**
	 * When image can't be read by program.
	 * @param index the index of image files
	 * @param e exception of reading image
	 */
	void onImageReadFail(int index,File image,IOException e);
	/**
	 * When conversion fail.
	 * @param index
	 * @param e exception
	 */
	void onConversionFail(int index,Exception e);

	void onFileAlreadyExists(File file);
}

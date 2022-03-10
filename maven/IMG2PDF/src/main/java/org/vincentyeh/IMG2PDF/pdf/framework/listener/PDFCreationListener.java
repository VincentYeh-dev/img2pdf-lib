package org.vincentyeh.IMG2PDF.pdf.framework.listener;

import org.vincentyeh.IMG2PDF.task.framework.Task;

import java.io.File;


/**
 * This interface can listen the event when conversion started.
 * @author vincent
 */
public interface PDFCreationListener {

	void initializing(Task task);
	void onConversionComplete();

	void onSaved(File destination);

	void onPageAppended(int index);

	void onFinally();
}

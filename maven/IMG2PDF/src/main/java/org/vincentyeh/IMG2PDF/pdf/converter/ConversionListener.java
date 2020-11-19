package org.vincentyeh.IMG2PDF.pdf.converter;

import org.vincentyeh.IMG2PDF.task.Task;

public interface ConversionListener {
	
	public void onConversionPreparing(Task task);
	public void onConverting(int index)throws Exception;
	public void onConversionComplete();
	public void onConversionFail(int index,Exception e);
}

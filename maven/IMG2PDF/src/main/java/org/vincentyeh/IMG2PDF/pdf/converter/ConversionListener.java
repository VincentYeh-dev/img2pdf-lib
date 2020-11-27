package org.vincentyeh.IMG2PDF.pdf.converter;

import java.io.IOException;

import org.vincentyeh.IMG2PDF.task.Task;

public interface ConversionListener {
	
	public void onConversionPreparing(Task task);
	public void onConverting(int index);
	public void onConversionComplete();
	
	public void onImageReadFail(int index,IOException e);
	public void onConversionFail(int index,Exception e);
}

package org.vincentyeh.IMG2PDF.pdf;

import java.io.IOException;

import org.vincentyeh.IMG2PDF.task.configured.ConfiguredTask;
import org.vincentyeh.IMG2PDF.util.ImageProcess;

public class CustomPDFConveter extends PDFConverter {
	private final int size_of_imgs;
	private final double perImg;
	private double progress = 0;

	public CustomPDFConveter(ConfiguredTask task) throws IOException {
		super(task);
		size_of_imgs = imgs.size();
		perImg = (10. / size_of_imgs);
	}
	
	@Override
	public void before() {
		System.out.printf("Destination:%s\n", doc.getDestination().getName());
		System.out.print("0%[");
	}

	@Override
	public void convert(int index) throws Exception {
		progress += perImg;
		while (progress >= 1) {
			System.out.print("=");
			progress -= 1;
		}
	}

	@Override
	public void done() {
		System.out.print("]%100");
		if(isProtectedByPwd)
			System.out.print(" *");
		System.out.println("\n\n");
	}

	@Override
	public void fail(int index, Exception e) {
		System.out.print("FAIL]");
	}

}

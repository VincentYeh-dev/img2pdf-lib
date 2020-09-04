package org.vincentyeh.IMG2PDF.util;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.test.common.CMYK;

/**
 * 
 * From Dsmart
 * https://www.iteye.com/blog/dsmart-30buy-1226969
 * 
 * edit by VincentYeh
 */

public class ColorConvertion {

	public static void main(String[] args) throws IOException {
		String filename = "test_file\\CMYK\\01.jpg";

		ColorConvertion cm = new ColorConvertion();
		cm.readImage(filename);
	}
	
	public void readImage(String filename) throws IOException {

		File file = new File(filename);
		ImageReader reader=findReader(file);
		
		String format = reader.getFormatName();
		BufferedImage image;
		System.out.println(format);
		if ("JPEG".equalsIgnoreCase(format) || "JPG".equalsIgnoreCase(format)) {

			try {
//				try to reading file(including Color Convertion)
				image = reader.read(0); // RGB

			} catch (IIOException e) {
//				Read Raster(no color convertion)
				
				
				System.err.println(e.getMessage());
				Raster raster = reader.readRaster(0, null);// CMYK
				image = YCCK2RGB(raster);
//				image=null;
				
			}

			image.getGraphics().drawImage(image, 0, 0, null);
			String newfilename = filename.substring(0, filename.lastIndexOf(".")) + "_rgb"
					+ filename.substring(filename.lastIndexOf("."));
			File newFile = new File(newfilename);
			ImageIO.write(image,"jpg",newFile);
			
		}

	}
	
	ImageReader findReader(File file) throws IOException {
		ImageInputStream input = ImageIO.createImageInputStream(file);
		Iterator readers = ImageIO.getImageReaders(input);
		if (readers == null || !readers.hasNext()) {
			throw new RuntimeException("1 No ImageReaders found");
		}

		ImageReader reader = (ImageReader) readers.next();
		reader.setInput(input);
		return reader;
	}

	private BufferedImage YCCK2RGB(Raster raster) {
		int w = raster.getWidth();
		int h = raster.getHeight();
		byte[] rgb = new byte[w * h * 3];

		// YCCK to RGB
		float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
		float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
		float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
		float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);

		for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3) {
			float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i], cr = 255 - Cr[i];

			double val = y + 1.402 * (cr - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

			val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

			val = y + 1.772 * (cb - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
		}

		raster = Raster.createInterleavedRaster(new DataBufferByte(rgb, rgb.length), w, h, w * 3, 3,
				new int[] { 0, 1, 2 }, null);

		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		return new BufferedImage(cm, (WritableRaster) raster, true, null);
	}

	
}

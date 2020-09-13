package org.vincentyeh.IMG2PDF.file;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The ImgFile is a comparable subclass of File.It's can be compared by filename and the last time the file was modified. 
 * 
 * @author VincentYeh
 *
 */
public class ImgFile extends File implements Comparable<File> {
	public static final int ORDER_NONE=-1;
	public static final int ORDER_INCREASE=0;
	public static final int ORDER_DECREASE=1;
	public static final int SORTBY_NONE=-1;
	public static final int SORTBY_NAME=0;
	public static final int SORTBY_DATE=1;
	private final int sortby,order;
	
	
	/**
	 * Create the ImgFile that no need to sort.
	 * @param pathname the path of image file 
	 * @throws FileNotFoundException when file is not exists
	 */
	public ImgFile(String pathname) throws FileNotFoundException {
		this(pathname,ORDER_NONE,SORTBY_NONE);
	}

	/**
	 * Create the ImgFile that need to sort.
	 * 
	 * @param pathname the path of image file 
	 * @param sortby sort by name etc..
	 * @param order
	 * @throws FileNotFoundException when file is not exists
	 */
	public ImgFile(String pathname, int sortby, int order) throws FileNotFoundException{
		super(pathname);
		if(pathname.isEmpty())throw new RuntimeException("Path name is empty.");
		if(!exists())throw new FileNotFoundException(pathname+" not found.");
		if (isDirectory())
			throw new RuntimeException(this.getAbsolutePath() + " is a directory");
		
		if(order<-1||order>1)
			throw new IllegalArgumentException("order value need to be between -1 and 1");
		if(sortby<-1||sortby>1)
			throw new IllegalArgumentException("sortby value need to be between -1 and 1");
		
		
		this.sortby=sortby;
		this.order=order;
		
	}
	
	@Override
	public int compareTo(File o) {
		switch(sortby) {
		case SORTBY_NAME:
			if(order==ORDER_INCREASE)
				return this.getName().compareTo(o.getName());
			else if(order==ORDER_DECREASE)
				return o.getName().compareTo(this.getName());
		case SORTBY_DATE:
			if(order==ORDER_INCREASE)
				return (this.lastModified()-o.lastModified()>0)?1:-1;
			
			else if(order==ORDER_DECREASE)
				return (this.lastModified()-o.lastModified()>0)?-1:1;
		default:
				throw new RuntimeException("Multiple files need to be sorted by sort and order arguments.");
		}
		
	}
	public int getOrder() {
		return order;
	}
	public int getSortby() {
		return sortby;
	}
	
	public static BufferedImage rotateImg(BufferedImage raw, int rotate_angle) {
		final double rads = Math.toRadians(rotate_angle);
		final double sin = Math.abs(Math.sin(rads));
		final double cos = Math.abs(Math.cos(rads));
		final int w = (int) Math.floor(raw.getWidth() * cos + raw.getHeight() * sin);
		final int h = (int) Math.floor(raw.getHeight() * cos + raw.getWidth() * sin);
		final BufferedImage rotatedImage = new BufferedImage(w, h, raw.getType());
		final AffineTransform at = new AffineTransform();
		at.translate(w / 2, h / 2);
		at.rotate(rads, 0, 0);
		at.translate(-raw.getWidth() / 2, -raw.getHeight() / 2);
		AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		rotateOp.filter(raw, rotatedImage);
		return rotatedImage;
	}
//	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
//	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
//	    BufferedImage dimg = new BufferedImage(newW, newH,img.getType());
//
//	    Graphics2D g2d = dimg.createGraphics();
//	    g2d.drawImage(tmp, 0, 0, null);
//	    g2d.dispose();
//
//	    return dimg;
//	}  
}

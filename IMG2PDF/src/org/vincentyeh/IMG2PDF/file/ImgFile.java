package org.vincentyeh.IMG2PDF.file;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImgFile extends File implements Comparable<File> {
	public static final int ORDER_INCREASE=0;
	public static final int ORDER_DECREASE=1;
	public static final int SORTBY_NAME=2;
	public static final int SORTBY_DATE=3;
	private int sortby,order;
	
	public ImgFile(String pathname) {
		super(pathname);
		sortby=order=-1;
	}
	public ImgFile(File file) {
		this(file.getAbsolutePath());
	}	

	public ImgFile(String pathname, int sortby, int order) throws IOException {
		this(pathname);
		this.sortby = sortby;
		this.order = order;

		if (this.isDirectory()) {
			throw new RuntimeException(this.getAbsolutePath() + " is a Folder");
		}
	}
	public ImgFile(File file, int sortby, int order) throws IOException {
		this(file.getAbsolutePath(),sortby,order);
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
				throw new RuntimeException("Files in folder need to be ordered.");
		}
		
	}

//	public ImgFile(File raw) throws IOException {
//		if(raw.isDirectory()) {
//			throw new RuntimeException(raw.getAbsolutePath()+" is a Folder");
//		}
//		file=raw;
//		nameWithExtension =file.getName();
//		name = nameWithExtension.split("\\.")[0];
//		extension = nameWithExtension.split("\\.")[1];
//	}
//	public ImgFile(File raw,int sortby,int order) throws IOException {
//		this(raw);
//		this.sortby=sortby;
//		this.order=order;
//	}
//	public File toFile() {
//		return file;
//	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public void setSortby(int sortby) {
		this.sortby = sortby;
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
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH,img.getType());

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
}

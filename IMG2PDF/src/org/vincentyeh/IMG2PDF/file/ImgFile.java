package org.vincentyeh.IMG2PDF.file;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImgFile implements Comparable<ImgFile> {

	public final File file;
	final protected String nameWithExtension,name;
	protected String extension="";
	public static final int ORDER_INCREASE=0;
	public static final int ORDER_DECREASE=1;
	public static final int SORTBY_NAME=2;
	public static final int SORTBY_DATE=3;
	private int sortby,order;
	@Override
	public int compareTo(ImgFile o) {
		
		switch(sortby) {
		case SORTBY_NAME:
			if(order==ORDER_INCREASE)
				return this.file.getName().compareTo(o.file.getName());
			else if(order==ORDER_DECREASE)
				return o.file.getName().compareTo(this.file.getName());
		case SORTBY_DATE:
			if(order==ORDER_INCREASE)
				return (this.file.lastModified()-o.file.lastModified()>0)?1:-1;
			
			else if(order==ORDER_DECREASE)
				return (this.file.lastModified()-o.file.lastModified()>0)?-1:1;
		default:
				throw new RuntimeException("Files in folder need to be ordered.");
		}
		
	}

	public ImgFile(File raw,boolean readImg) throws IOException {
		if(raw.isDirectory()) {
			throw new RuntimeException(raw.getAbsolutePath()+" is a Folder");
		}
		file=raw;
		nameWithExtension =file.getName();
		name = nameWithExtension.split("\\.")[0];
		extension = nameWithExtension.split("\\.")[1];
	}
	public ImgFile(File raw,int sortby,int order,boolean readImg) throws IOException {
		this(raw,readImg);
		this.sortby=sortby;
		this.order=order;
	}

	public String getName() {
		return name;
	}

	public String getExtension() {
		return extension;
	}

	public String getNameWithExtension() {
		return nameWithExtension;
	}
	public File toFile() {
		return file;
	}
	private BufferedImage toBufferedImage() throws IOException {
		return ImageIO.read(this.file);
	}
	
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
}

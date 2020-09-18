package org.vincentyeh.IMG2PDF.file;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * The ImgFile is a comparable subclass of File.It's can be compared by filename
 * and the last time the file was modified.
 * 
 * @author VincentYeh
 *
 */
public class ImgFile extends File implements Comparable<File> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6910177315066351680L;

//	public static final int ORDER_NONE=-1;
//	public static final int ORDER_INCREASE=0;
//	public static final int ORDER_DECREASE=1;
//	
//	public static final int SORTBY_NONE=-1;
//	public static final int SORTBY_NAME=0;
//	public static final int SORTBY_DATE=1;
	private final Sortby sortby;
	private final Order order;

	/**
	 * Create the ImgFile that no need to sort.
	 * 
	 * @param pathname the path of image file
	 * @throws FileNotFoundException when file is not exists
	 */
	public ImgFile(String pathname) throws FileNotFoundException {
		this(pathname, Sortby.NONE, Order.NONE);
	}

	/**
	 * Create the ImgFile that need to sort.
	 * 
	 * @param pathname the path of image file
	 * @param sortby sortby
	 * @param order order
	 * @throws FileNotFoundException when file is not exists
	 */
	public ImgFile(String pathname, Sortby sortby, Order order) throws FileNotFoundException {
		super(pathname);
		if (pathname.isEmpty())
			throw new RuntimeException("Path name is empty.");
		if (!exists())
			throw new FileNotFoundException(pathname + " not found.");
		if (isDirectory())
			throw new RuntimeException(this.getAbsolutePath() + " is a directory");
//		
//		if(order<-1||order>1)
//			throw new IllegalArgumentException("order value need to be between -1 and 1");
//		if(sortby<-1||sortby>1)
//			throw new IllegalArgumentException("sortby value need to be between -1 and 1");
//		

		this.sortby = sortby;
		this.order = order;

	}

	@Override
	public int compareTo(File o) {
		switch (sortby) {
		case NAME:
			if (order == Order.INCREASE)
				return this.getName().compareTo(o.getName());
			else if (order == Order.DECREASE)
				return o.getName().compareTo(this.getName());
		case DATE:
			if (order == Order.INCREASE)
				return (this.lastModified() - o.lastModified() > 0) ? 1 : -1;

			else if (order == Order.DECREASE)
				return (this.lastModified() - o.lastModified() > 0) ? -1 : 1;
		default:
			throw new RuntimeException("Multiple files need to be sorted by sort and order arguments.");
		}

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

	public enum Order {
		NONE("NONE"), INCREASE("INCREASE"), DECREASE("DECREASE");
		private String str;

		private Order(String str) {
			this.str = str;
		}

		public static Order getByStr(String str) {
			switch (str) {
			case "NONE":
				return NONE;
			case "INCREASE":
				return INCREASE;
			case "DECREASE":
				return DECREASE;
			default:
				throw new RuntimeException("Order is invalid");
			}
		}

		public String getStr() {
			return str;
		}

		public static String[] valuesStr() {
			Order[] order_list = Order.values();
			String[] str_list = new String[order_list.length];
			for (int i = 0; i < str_list.length; i++) {
				str_list[i] = order_list[i].getStr();
			}
			return str_list;
		}
	}

	public enum Sortby {
		NONE("NONE"), NAME("NAME"), DATE("DATE");
		private String str;

		private Sortby(String str) {
			this.str = str;
		}

		public static Sortby getByStr(String str) {
			switch (str) {
			case "NONE":
				return NONE;
			case "DATE":
				return DATE;
			case "NAME":
				return NAME;
			default:
				throw new RuntimeException("Sortby is invalid");
			}
		}

		public static String[] valuesStr() {
			Sortby[] sortby_list = Sortby.values();
			String[] str_list = new String[sortby_list.length];
			for (int i = 0; i < str_list.length; i++) {
				str_list[i] = sortby_list[i].getStr();
			}
			return str_list;
		}

		public String getStr() {
			return str;
		}
	}
}

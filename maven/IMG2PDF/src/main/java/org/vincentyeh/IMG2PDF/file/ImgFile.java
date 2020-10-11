package org.vincentyeh.IMG2PDF.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * @param sortby   sortby
	 * @param order    order
	 * @throws FileNotFoundException when file is not exists
	 */
	public ImgFile(String pathname, Sortby sortby, Order order) throws FileNotFoundException {
		super(pathname);
		if (pathname.isEmpty())
			throw new RuntimeException("Path name is empty.");
		if (!exists())
			throw new FileNotFoundException(pathname + " not found.");
		if (isDirectory())
			throw new FileIsDirectoryException(this);

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
		case NUMERTIC:
			if (order == Order.INCREASE)
				return compareTo(this.getName(), o.getName());

			else if (order == Order.DECREASE)
				return compareTo(o.getName(), this.getName());

			
		default:
			throw new RuntimeException("Multiple files need to be sorted by sort and order arguments.");
		}

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
		NONE("NONE"), NAME("NAME"), DATE("DATE"), NUMERTIC("NUMERTIC");
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

	public static class FileIsDirectoryException extends FileNotImageException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public FileIsDirectoryException(File f) {
			super(f.getName() + " is a directory.");
		}

	}

	public static class FileNotImageException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public FileNotImageException(String str) {
			super(str);
		}

		public FileNotImageException(File f) {
			super(f.getName() + " is not a image file.");
		}

	}

	int compareTo(String ThisStr, String OStr) {
		String noNumThis = ThisStr.replaceAll("[0-9]{1,}", "*");
		String noNumO = OStr.replaceAll("[0-9]{1,}", "*");

		if (noNumThis.equals(noNumO)) {
			ArrayList<Integer> a = getNum(ThisStr);
			ArrayList<Integer> b = getNum(OStr);
			for (int i = 0; i < a.size(); i++) {
				int r = a.get(i) - b.get(i);
				if (r != 0) {
					return r;
				}
			}
			return 0;
		} else {
			return ThisStr.compareTo(OStr);
		}
	}

	ArrayList<Integer> getNum(String str) {
		String s[] = str.split("[^0-9]{1,}");
		ArrayList<Integer> buf = new ArrayList<Integer>();
		for (int i = 0; i < s.length; i++) {
			if (!s[i].isEmpty()) {
				buf.add(Integer.valueOf(s[i]));
			}
		}
		return buf;
	}
}

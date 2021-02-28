package org.vincentyeh.IMG2PDF.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign.VerticalAlign;

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
	private final Sequence sequence;

	/**
	 * Create the ImgFile that no need to sort.
	 * 
	 * @param pathname the path of image file
	 * @throws FileNotFoundException when file is not exists
	 */
	public ImgFile(String pathname) throws FileNotFoundException {
		this(pathname, Sortby.NONE, Sequence.NONE);
	}

	/**
	 * Create the ImgFile that need to sort.
	 * 
	 * @param pathname the path of image file
	 * @param sortby   sortby
	 * @param sequence sequence
	 * @throws FileNotFoundException when file is not exists
	 */
	public ImgFile(String pathname, Sortby sortby, Sequence sequence) throws FileNotFoundException {
		super(pathname);
		if (pathname.isEmpty())
			throw new RuntimeException("Path name is empty.");
		if (!exists())
			throw new FileNotFoundException(pathname + " not found.");
		if (isDirectory())
			throw new FileIsDirectoryException(this);

		this.sortby = sortby;
		this.sequence = sequence;

	}

	@Override
	public int compareTo(File o) {
		switch (sortby) {
		case NAME:
			if (sequence == Sequence.INCREASE)
				return this.getName().compareTo(o.getName());
			else if (sequence == Sequence.DECREASE)
				return o.getName().compareTo(this.getName());
		case DATE:
			if (sequence == Sequence.INCREASE)
				return (this.lastModified() - o.lastModified() > 0) ? 1 : -1;

			else if (sequence == Sequence.DECREASE)
				return (this.lastModified() - o.lastModified() > 0) ? -1 : 1;
		case NUMERTIC:
			if (sequence == Sequence.INCREASE)
				return compareTo(this.getName(), o.getName());

			else if (sequence == Sequence.DECREASE)
				return compareTo(o.getName(), this.getName());

		default:
			throw new RuntimeException("Multiple files need to be sorted by sort and sequence arguments.");
		}

	}

	public enum Sequence {
		NONE, INCREASE, DECREASE;

		public static Sequence getByString(String str) throws UnrecognizedEnumException {
			Sequence[] sequences = Sequence.values();
			for (Sequence sequence : sequences) {
				if (sequence.toString().equals(str))
					return sequence;
			}
			throw new UnrecognizedEnumException(str, Sequence.class);
		}

	}

	public enum Sortby {
		NONE, NAME, DATE, NUMERTIC;

		public static Sortby getByString(String str) throws UnrecognizedEnumException {
			Sortby[] sortbys = Sortby.values();
			for (Sortby sortby : sortbys) {
				if (sortby.toString().equals(str))
					return sortby;
			}
			throw new UnrecognizedEnumException(str, Sortby.class);
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

	private int compareTo(String ThisStr, String OStr) {
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

	private ArrayList<Integer> getNum(String str) {
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

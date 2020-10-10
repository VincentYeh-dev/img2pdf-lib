package org.vincentyeh.IMG2PDF.task.formatted;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom2.Element;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.ImgFile.Order;
import org.vincentyeh.IMG2PDF.file.ImgFile.Sortby;
import org.vincentyeh.IMG2PDF.file.PDFFile.Align;
import org.vincentyeh.IMG2PDF.file.PDFFile.Align.IllegalAlignException;
import org.vincentyeh.IMG2PDF.file.PDFFile.Size;
import org.vincentyeh.IMG2PDF.file.PDFFile.Size.IllegalSizeException;
import org.vincentyeh.IMG2PDF.task.Task;

public class FormattedTask extends Task {
	protected final ArrayList<File> imgs = new ArrayList<File>();

	public FormattedTask(Element element) throws FileNotFoundException {
		super(element.getAttributeValue("destination"), element.getAttributeValue("owner"),
				element.getAttributeValue("user"), new Align(element.getAttributeValue("align")),
				Size.getSizeFromString(element.getAttributeValue("size")));

		List<Element> contains_files = element.getChild("FILES").getChildren("FILE");
		ArrayList<Element> xml_files = new ArrayList<Element>(contains_files);
		imgs.addAll(elements2imgs(xml_files));

	}

	private ArrayList<File> elements2imgs(ArrayList<Element> el_files) {
		ArrayList<File> imgs = new ArrayList<File>();
		for (Element el : el_files) {
			imgs.add(new File(el.getValue()));
		}
		return imgs;
	}

	public ArrayList<File> getImgs() {
		return imgs;
	}
}

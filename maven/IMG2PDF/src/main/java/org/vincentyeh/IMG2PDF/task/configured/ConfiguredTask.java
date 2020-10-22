package org.vincentyeh.IMG2PDF.task.configured;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.pdf.page.Align;
import org.vincentyeh.IMG2PDF.pdf.page.Size;
import org.vincentyeh.IMG2PDF.task.Task;

public class ConfiguredTask extends Task {
	public ConfiguredTask(Element element) throws FileNotFoundException {
		super(element.getAttributeValue("destination"), element.getAttributeValue("owner"),
				element.getAttributeValue("user"), new Align(element.getAttributeValue("align")),
				Size.getSizeFromString(element.getAttributeValue("size")));

		List<Element> contains_files = element.getChild("FILES").getChildren("FILE");
		ArrayList<Element> xml_files = new ArrayList<Element>(contains_files);
		imgs.addAll(elements2imgs(xml_files));

	}

	private ArrayList<ImgFile> elements2imgs(ArrayList<Element> el_files) throws FileNotFoundException {
		ArrayList<ImgFile> imgs = new ArrayList<ImgFile>();
		for (Element el : el_files) {
			ImgFile img = new ImgFile(new File(el.getValue()).getAbsolutePath());
			imgs.add(img);
		}
		return imgs;
	}

	public ArrayList<ImgFile> getImgs() {
		return imgs;
	}
}

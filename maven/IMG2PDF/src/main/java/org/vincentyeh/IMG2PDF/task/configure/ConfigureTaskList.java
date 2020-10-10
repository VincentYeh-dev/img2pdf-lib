package org.vincentyeh.IMG2PDF.task.configure;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class ConfigureTaskList extends ArrayList<ConfigureTask> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Write XML Element to the file.
	 * 
	 * @param file destination of output XML file.
	 * @throws IOException Exception of writing PDF
	 */
	public void toXMLFile(File file) throws IOException {
		Document doc = new Document();
		Element root = toElement();
		doc.setRootElement(root);
		// Create the XML
		XMLOutputter outter = new XMLOutputter();
		outter.setFormat(Format.getPrettyFormat());
		outter.output(doc, new FileWriter(file));
	}

	public Element toElement() {
		Element root = new Element("TASKLIST");
		for (ConfigureTask task : this) {
			root.addContent(task.toElement());
		}
		return root;
	}
}

package org.vincentyeh.IMG2PDF.task.setup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

public class SetupTaskList extends ArrayList<SetupTask> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Create the empty collection of Task.
	 */
	public SetupTaskList() {

	}
	
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
		for (SetupTask task : this) {
			root.addContent(task.toElement());
		}
		return root;
	}
}

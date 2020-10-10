package org.vincentyeh.IMG2PDF.task.formatted;

import java.io.File;
import java.io.FileNotFoundException;
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

public class FormattedTaskList extends ArrayList<FormattedTask> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public FormattedTaskList(String filepath) throws ParserConfigurationException, SAXException, IOException {
		this(getDOMParsedDocument(filepath));
	}

	public FormattedTaskList(Document doc) throws FileNotFoundException {
		this(doc.getRootElement());
	}
	
	public FormattedTaskList(Element root) throws FileNotFoundException {
		ArrayList<Element> importedTaskList = new ArrayList<Element>(root.getChildren("TASK"));
		for (Element task : importedTaskList) {
			this.add(new FormattedTask(task));
		}
	}
	
	/**
	 * create Document from file
	 * 
	 * @param filepath path of xml file
	 * @return Document
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static Document getDOMParsedDocument(final String filepath)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// If want to make namespace aware.
		// factory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		org.w3c.dom.Document w3cDocument = documentBuilder.parse(filepath);
		return new DOMBuilder().build(w3cDocument);
	}
}



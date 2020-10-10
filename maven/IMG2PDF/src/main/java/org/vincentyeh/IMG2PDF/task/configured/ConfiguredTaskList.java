package org.vincentyeh.IMG2PDF.task.configured;

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

public class ConfiguredTaskList extends ArrayList<ConfiguredTask> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public ConfiguredTaskList(String filepath) throws ParserConfigurationException, SAXException, IOException {
		this(getDOMParsedDocument(filepath));
	}

	public ConfiguredTaskList(Document doc) throws FileNotFoundException {
		this(doc.getRootElement());
	}
	
	public ConfiguredTaskList(Element root) throws FileNotFoundException {
		ArrayList<Element> importedTaskList = new ArrayList<Element>(root.getChildren("TASK"));
		for (Element task : importedTaskList) {
			this.add(new ConfiguredTask(task));
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



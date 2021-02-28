package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.xml.sax.SAXException;

public class TaskList extends ArrayList<Task> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7305144027050402452L;

	public TaskList() {

	}

	public TaskList(File xml_file) throws ParserConfigurationException, SAXException, IOException, UnrecognizedEnumException {
		this(getDOMParsedDocument(xml_file));
	}

	public TaskList(Document doc) throws FileNotFoundException, UnrecognizedEnumException {
		this(doc.getRootElement());
	}

	public TaskList(Element root) throws FileNotFoundException, UnrecognizedEnumException {
		ArrayList<Element> importedTaskList = new ArrayList<Element>(root.getChildren("TASK"));
		for (Element task : importedTaskList) {

			this.add(new Task(task));
		}
	}

	/**
	 * Write XML Element to the file.
	 * 
	 * @param file destination of output XML file.
	 * @throws IOException
	 */
	public void toXMLFile(File file) throws IOException {
		
		if(file.getParentFile().mkdirs()) {
			System.out.println("Required folders have been created in advance:"+file.getParent());
		}
		
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
		for (Task task : this) {
			root.addContent(task.toElement());
		}
		return root;
	}

	private static Document getDOMParsedDocument(final File xml_file)
			throws ParserConfigurationException, SAXException, IOException {
		if (!xml_file.exists())
			throw new FileNotFoundException(xml_file.getName() + " not found.");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// If want to make namespace aware.
		// factory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		org.w3c.dom.Document w3cDocument = documentBuilder.parse(xml_file);
		return new DOMBuilder().build(w3cDocument);
	}

}

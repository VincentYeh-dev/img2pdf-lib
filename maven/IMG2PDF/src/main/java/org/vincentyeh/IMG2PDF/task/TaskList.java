package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.vincentyeh.IMG2PDF.file.PDFFile;
/**
 * The collection of Task.This class is able to convert between collection and XML format.
 * 
 * @author VincentYeh
 */
public class TaskList extends ArrayList<Task> {
	
	private Element xml_root;
	private Document doc;

	/**
	 * Create the empty collection of Task.
	 */
	public TaskList() {
		xml_root = new Element("TASKLIST");
		doc = new Document();
	}

	
	/**
	 * Create the collection of Task by Document.
	 * @param doc The XML　document that contain TASKLIST.
	 */
	public TaskList(Document doc) {
		this.doc = doc;
		xml_root = this.doc.getRootElement();
		ArrayList<Element> el_tasks = new ArrayList<Element>(xml_root.getChildren("TASK"));

		for (Element el_task : el_tasks) {
			this.add(new Task(el_task));
		}
	}

	@Override
	public void add(int index, Task element) {
		xml_root.addContent(element);
		super.add(index, element);
	}

	@Override
	public boolean add(Task element) {
		xml_root.addContent(element);
		return super.add(element);
	}

	@Override
	public Task remove(int index) {
		xml_root.removeContent(index);
		return super.remove(index);
	}

	/**
	 * Write XML Element to the file.
	 * @param file destination of output XML file.
	 */
	public void toXMLFile(File file) throws IOException {
		doc.setRootElement(xml_root);
		// Create the XML
		XMLOutputter outter = new XMLOutputter();
		outter.setFormat(Format.getPrettyFormat());
		outter.output(doc, new FileWriter(file));
	}
}

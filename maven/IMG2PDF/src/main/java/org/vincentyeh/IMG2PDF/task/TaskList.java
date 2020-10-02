package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
/**
 * The collection of Task.This class is able to convert between collection and XML format.
 * 
 * @author VincentYeh
 */
public class TaskList extends ArrayList<Task> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2891486701232492442L;
	
	private Element elementsData;
	private Document doc;

	/**
	 * Create the empty collection of Task.
	 */
	public TaskList() {
		elementsData = new Element("TASKLIST");
		doc = new Document();
	}
	
	/**
	 * Create the collection of Task by Document.
	 * @param doc The XML　document that contain TASKLIST.
	 */
	public TaskList(Document doc) {
		this.doc = doc;
		elementsData = this.doc.getRootElement();
		ArrayList<Element> el_tasks = new ArrayList<Element>(elementsData.getChildren("TASK"));

		for (Element el_task : el_tasks) {
			this.add(new Task(el_task));
		}
	}
	@Override
	public void add(int index, Task element) {
		elementsData.addContent(element);
		super.add(index, element);
	}

	@Override
	public boolean add(Task element) {
		elementsData.addContent(element);
		return super.add(element);
	}

	@Override
	public Task remove(int index) {
		elementsData.removeContent(index);
		return super.remove(index);
	}
	
	public boolean addAll(TaskList c) {
		// TODO Auto-generated method stub
		
		for (Content con : c.elementsData.clone().detach().getContent()) {
			this.elementsData.addContent(con.clone().detach());
		}
		
		return super.addAll(c);
	}
	
	/**
	 *Write XML Element to the file.
	 * @param file destination of output XML file.
	 * @throws IOException Exception of writing PDF
	 */
	public void toXMLFile(File file) throws IOException {
		doc.setRootElement(elementsData);
		// Create the XML
		XMLOutputter outter = new XMLOutputter();
		outter.setFormat(Format.getPrettyFormat());
		outter.output(doc, new FileWriter(file));
	}
}

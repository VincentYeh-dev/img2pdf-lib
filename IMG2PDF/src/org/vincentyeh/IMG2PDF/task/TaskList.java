package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class TaskList extends ArrayList<Task>{
	private Element xml_root;
	private Document doc;
	
	public TaskList() {
		xml_root=new Element("TASKLIST");
		doc=new Document();
	}
	
	public TaskList(String name) {
		xml_root=new Element(name);
		doc=new Document();
		
	}
	
	@Override
	public void add(int index, Task element) {
		try {
			xml_root.addContent(element.toXMLTask());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.add(index, element);
	}
	
	@Override
	public boolean add(Task element) {
		try {
			xml_root.addContent(element.toXMLTask());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return super.add(element);
	}
	@Override
	public Task remove(int index) {
		xml_root.removeContent(index);
		return super.remove(index);
	}
	
	public void toXMLFile(File file) throws IOException {
		doc.setRootElement(xml_root);
		//Create the XML
		XMLOutputter outter=new XMLOutputter();
		outter.setFormat(Format.getPrettyFormat());
		outter.output(doc, new FileWriter(file));
	}
}

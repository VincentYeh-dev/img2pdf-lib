package org.vincentyeh.IMG2PDF.commandline;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.input.DOMBuilder;
import org.junit.Test;
import org.vincentyeh.IMG2PDF.task.UniTask;
import org.vincentyeh.IMG2PDF.task.UniTaskList;
import org.xml.sax.SAXException;

public class ElementTest {
	@Test
	public void elementTest() throws ParserConfigurationException, SAXException, IOException {
		Document doc=getDOMParsedDocument("D:\\Project\\git\\IMG2PDF\\sample\\walk-animation\\taskslist\\test.xml");
		UniTaskList as=new UniTaskList();
		UniTask a=new UniTask(doc.getRootElement().getChild("TASK"));
		as.add(a);
		
		as.toXMLFile(new File("D:\\aaaaa.xml"));
		System.out.println(a.toElement());
	}
	
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

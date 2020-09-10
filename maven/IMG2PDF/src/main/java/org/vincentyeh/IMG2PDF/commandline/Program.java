package org.vincentyeh.IMG2PDF.commandline;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jdom2.Document;
import org.jdom2.input.DOMBuilder;
import org.vincentyeh.IMG2PDF.file.PDFFile;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.xml.sax.SAXException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
/**
 * 
 * @author VincentYeh
 *
 */
public class Program {
	final ArrayList<String> lists;

	Program(ArgumentParser parser, String[] args) {
		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
		lists = new ArrayList<String>(ns.<String>getList("source"));
	}

	public static void main(String[] args) throws IOException {

		ArgumentParser parser = createArgParser();
		Program program = new Program(parser, args);

		for (String list : program.lists) {
			Document xml = getDOMParsedDocument(list);
			TaskList tasks=new TaskList(xml);
			
			for(Task task:tasks) {
				PDFFile pdf = new PDFFile(task);
				try {
					pdf.process();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}

	}

	private static Document getDOMParsedDocument(final String fileName) {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// If want to make namespace aware.
			// factory.setNamespaceAware(true);
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			org.w3c.dom.Document w3cDocument = documentBuilder.parse(fileName);
			document = new DOMBuilder().build(w3cDocument);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		return document;
	}

	static ArgumentParser createArgParser() {
		ArgumentParser parser = ArgumentParsers.newFor("IMG2PDF").build().defaultHelp(true)
				.description("Convert or merge image file to PDF");
		parser.addArgument("source").nargs("*").help("File to convert");
		return parser;
	}

}

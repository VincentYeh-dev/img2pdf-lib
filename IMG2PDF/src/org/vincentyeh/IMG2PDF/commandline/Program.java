package org.vincentyeh.IMG2PDF.commandline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.IIOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.PDFFile;
import org.vincentyeh.IMG2PDF.task.Task;
import org.xml.sax.SAXException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

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
			Element root = xml.getRootElement();
			ArrayList<Element> el_tasks = new ArrayList<Element>(root.getChildren("TASK"));

			Element err_root = new Element("ERRORLIST");
			Document doc = new Document();
			for (Element el_task : el_tasks) {
				Task task = new Task(el_task);

				PDFFile pdf = new PDFFile(task);
				try {
					pdf.process();
				} catch (IIOException e) {
					Element err_task = task.toXMLTask();
					Element err = new Element("ERROR");
					err.addContent(e.getMessage());
					err_task.addContent(err);
					err_root.addContent(err_task);
				}
			}
			doc.setRootElement(root);
			// Create the XML
			XMLOutputter outter = new XMLOutputter();
			outter.setFormat(Format.getPrettyFormat());
			outter.output(doc, new FileWriter(new File("error_list.xml")));
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

		parser.addArgument("-m", "--merge").choices("yes", "no").setDefault("no")
				.help("Merge all image files in Folder");

		parser.addArgument("-s", "--sortby").choices("name", "date").help("Merge all image files in Folder");

		parser.addArgument("-odr", "--order").choices("ics", "dcs")
				.help("order by increasing(0,1,2,3) or decreasing(3,2,1,0) value");

		parser.addArgument("-sz", "--size")
				.choices("A0", "A1", "A2", "A3", "A4", "A5", "A6", "LEGAL", "LETTER", "DEPEND")
				.help("PDF each page size.\ntype DEPEND to set each page size depend on each image size");
		parser.addArgument("-ownpwd", "--owner_password").nargs(1).help("PDF owner password");
		parser.addArgument("-usepwd", "--user_password").nargs(1).help("PDF user password");
		parser.addArgument("-d", "--destination").nargs(1).help("Destination of converted file");
		parser.addArgument("source").nargs("*").help("File to convert");
		return parser;
	}

}

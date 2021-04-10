package org.vincentyeh.IMG2PDF.task;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
import org.vincentyeh.IMG2PDF.util.FileChecker;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TaskList extends ArrayList<Task> {

    private static final Charset charset = StandardCharsets.UTF_8;
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
        ArrayList<Element> importedTaskList = new ArrayList<>(root.getChildren("TASK"));
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
        FileChecker.makeParentDirsIfNotExists(file);
        FileChecker.checkWritableFile(file);
//
//        if (file.getParentFile().mkdirs()) {
//            System.out.printf(Configuration.getResString("info_required_folder_created") + "\n", file.getParent());
//        }

        Document doc = new Document();
        Element root = toElement();
        doc.setRootElement(root);
        XMLOutputter outter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        outter.setFormat(format);

        outter.output(doc, new OutputStreamWriter(new FileOutputStream(file), charset));
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
        FileChecker.checkReadableFile(xml_file);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // If want to make namespace aware.
        // factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        InputSource source = new InputSource(new InputStreamReader(new FileInputStream(xml_file), charset));
        org.w3c.dom.Document w3cDocument = documentBuilder.parse(source);
        return new DOMBuilder().build(w3cDocument);
    }

}

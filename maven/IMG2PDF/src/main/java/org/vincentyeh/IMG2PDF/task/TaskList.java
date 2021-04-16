package org.vincentyeh.IMG2PDF.task;

import java.io.*;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.vincentyeh.IMG2PDF.util.FileChecker;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TaskList {
    private final ArrayList<Task> arrayList = new ArrayList<>();

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

    public void toXMLFile(File file) throws IOException {
        if(!file.getParentFile().exists()){
//            TODO:remove usage of print.
            if(file.getParentFile().mkdirs())
                System.out.printf(SharedSpace.getResString("public.info.required_folder_created") + "\n", file.getParentFile());
        }
        FileChecker.checkWritableFile(file);

        Document doc = new Document();
        Element root = toElement();
        doc.setRootElement(root);
        XMLOutputter outer = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        outer.setFormat(format);

        outer.output(doc, new OutputStreamWriter(new FileOutputStream(file), SharedSpace.Configuration.DEFAULT_CHARSET));
    }

    public Element toElement() {
        Element root = new Element("TASKLIST");
        for (Task task : arrayList) {
            root.addContent(task.toElement());
        }
        return root;
    }

    public void add(Task task) {
        arrayList.add(task);
    }

    public Task[] getArray() {
        Task[] array=new Task[arrayList.size()];
        arrayList.toArray(array);
        return array;
    }

    private static Document getDOMParsedDocument(final File xml_file)
            throws ParserConfigurationException, SAXException, IOException {
        FileChecker.checkReadableFile(xml_file);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // If want to make namespace aware.
        // factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        InputSource source = new InputSource(new InputStreamReader(new FileInputStream(xml_file), SharedSpace.Configuration.DEFAULT_CHARSET));
        org.w3c.dom.Document w3cDocument = documentBuilder.parse(source);
        return new DOMBuilder().build(w3cDocument);
    }

}

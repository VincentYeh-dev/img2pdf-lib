package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.vincentyeh.IMG2PDF.pdf.doc.DocumentArgument;
import org.vincentyeh.IMG2PDF.pdf.page.PageArgument;
import org.vincentyeh.IMG2PDF.util.file.FileChecker;

public class Task {
    private final DocumentArgument documentArgument;
    private final PageArgument pageArgument;

    private final File[] images;
    private final File destination;


    public Task(DocumentArgument documentArgument, PageArgument pageArgument, File[] images,File destination) {
        if (images == null)
            throw new IllegalArgumentException("images is null");
        this.images = images;
        this.destination = destination;
        this.documentArgument = documentArgument;
        this.pageArgument = pageArgument;
    }

    public Task(Element element) throws FileNotFoundException, UnrecognizedEnumException {
        if (element == null)
            throw new NullPointerException("element is null");

        this.documentArgument = new DocumentArgument(element.getChild("DocumentArgument"));
        this.pageArgument = new PageArgument(element.getChild("PageArgument"));
        this.destination=new File(element.getChild("destination").getValue());
        List<Element> contains_files = element.getChild("files").getChildren("image");
        ArrayList<Element> xml_files = new ArrayList<>(contains_files);
        this.images = parseElementsToImages(xml_files);

    }

   public Element toElement() {
        Element task = new Element("task");
        Element xml_files = new Element("files");
        for (File img : images) {
            Element xml_file = new Element("image");
            xml_file.addContent(img.getAbsolutePath());
            xml_files.addContent(xml_file);
        }
        task.addContent(xml_files);
        Element elm_destination=new Element("destination");
        elm_destination.addContent(destination.getAbsolutePath());

        task.addContent(elm_destination);
        task.addContent(documentArgument.toElement());
        task.addContent(pageArgument.toElement());

        return task;
    }

    private File[] parseElementsToImages(ArrayList<Element> el_files) throws FileNotFoundException {
        File[] imgFiles = new File[el_files.size()];
        for (int i = 0; i < imgFiles.length; i++) {
            Element el = el_files.get(i);
            imgFiles[i] = new File(new File(el.getValue()).getAbsolutePath());
            FileChecker.checkExists(imgFiles[i]);
            if (imgFiles[i].isDirectory())
                throw new RuntimeException(imgFiles[i].getAbsolutePath()+" is directory.");

        }

        return imgFiles;
    }


    public File[] getImages() {
        return this.images;
    }

    public File getDestination() {
        return destination;
    }

    public DocumentArgument getDocumentArgument() {
        return documentArgument;
    }

    public PageArgument getPageArgument() {
        return pageArgument;
    }



}

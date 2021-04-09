package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;
import org.vincentyeh.IMG2PDF.pdf.doc.DocumentArgument;
import org.vincentyeh.IMG2PDF.pdf.page.PageArgument;

public class Task {
    private final DocumentArgument documentArgument;
    private final PageArgument pageArgument;

    private final File[] imgs;


    public Task(DocumentArgument documentArgument, PageArgument pageArgument, File[] imgs) {
        if (imgs == null)
            throw new IllegalArgumentException("imgs is null");
        this.imgs = imgs;
        this.documentArgument = documentArgument;
        this.pageArgument = pageArgument;
    }

    /**
     * Initialize Task by element. This constructor is designed for parsed XML
     * element.
     *
     * @param element
     * @throws FileNotFoundException     When source image not found.
     * @throws UnrecognizedEnumException
     */
    public Task(Element element) throws FileNotFoundException, UnrecognizedEnumException {
        if (element == null)
            throw new NullPointerException("element is null");

        this.documentArgument = new DocumentArgument(element.getChild("DocumentArgument"));
        this.pageArgument = new PageArgument(element.getChild("PageArgument"));

        List<Element> contains_files = element.getChild("FILES").getChildren("FILE");
        ArrayList<Element> xml_files = new ArrayList<>(contains_files);
        this.imgs = parseElementsToImages(xml_files);

    }

    /**
     * To Element that contain Task attribute.
     *
     * @return Element
     */
    public Element toElement() {
        Element task = new Element("TASK");
        Element xml_files = new Element("FILES");
        for (File img : imgs) {
            Element xml_file = new Element("FILE");
            xml_file.addContent(img.getAbsolutePath());
            xml_files.addContent(xml_file);
        }
        task.addContent(xml_files);

        task.addContent(documentArgument.toElement());
        task.addContent(pageArgument.toElement());

        return task;
    }

    private File[] parseElementsToImages(ArrayList<Element> el_files) throws FileNotFoundException {
        File[] imgFiles = new File[el_files.size()];
        for (int i = 0; i < imgFiles.length; i++) {
            Element el = el_files.get(i);
            imgFiles[i] = new File(new File(el.getValue()).getAbsolutePath());
            if (!imgFiles[i].exists())
                throw new FileNotFoundException(imgFiles[i].getAbsolutePath() + " not found.");
            if (imgFiles[i].isDirectory())
                throw new RuntimeException(imgFiles[i].getAbsolutePath()+" is directory.");

        }

        return imgFiles;
    }


    public File[] getImgs() {
        return this.imgs;
    }

    public DocumentArgument getDocumentArgument() {
        return documentArgument;
    }

    public PageArgument getPageArgument() {
        return pageArgument;
    }



}

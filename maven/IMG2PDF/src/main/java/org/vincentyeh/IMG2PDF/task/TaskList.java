package org.vincentyeh.IMG2PDF.task;

import java.io.*;
import java.util.ArrayList;
import org.jdom2.Document;
import org.jdom2.Element;

public class TaskList extends ArrayList<Task>{
//    private final ArrayList<Task> arrayList = new ArrayList<>();

    public TaskList() {
    }

    public TaskList(Document doc) throws FileNotFoundException, IllegalArgumentException {
        this(doc.getRootElement());
    }

    private TaskList(Element root) throws FileNotFoundException, IllegalArgumentException {
        ArrayList<Element> importedTaskList = new ArrayList<>(root.getChildren("task"));
        for (Element task : importedTaskList) {
            this.add(new Task(task));
        }
    }


    public Element toElement() {
        Element root = new Element("TASKLIST");
        for (Task task : this) {
            root.addContent(task.toElement());
        }
        return root;
    }


}

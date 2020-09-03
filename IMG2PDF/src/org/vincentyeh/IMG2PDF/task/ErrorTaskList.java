package org.vincentyeh.IMG2PDF.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class ErrorTaskList extends TaskList{
	public ErrorTaskList() {
		super("ERRORTASKLIST");
	}
	
}

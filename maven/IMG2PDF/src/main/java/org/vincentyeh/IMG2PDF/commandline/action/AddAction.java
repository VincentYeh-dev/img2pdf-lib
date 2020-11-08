package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;

import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.NameFormatter;
import org.xml.sax.SAXException;

import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class AddAction extends CreateAction {
	@Arg(dest = "source")
	protected ArrayList<String> sources;

	public AddAction() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void setupByNamespace(Namespace ns) {
		super.setupByNamespace(ns);
		this.sources = (ArrayList<String>) ns.get("source");
	}

	@Override
	public void start() {
		try {
			createFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createFile() throws IOException, ParserConfigurationException, SAXException {
		TaskList tasks = new TaskList(list_destination);

		for (String source : sources) {
			tasks.addAll(importTasksFromTXT(new File(source)));
		}

		tasks.toXMLFile(new File(list_destination));
	}
	
	public static void setupParser(Subparsers subparsers) {
		Subparser import_parser = subparsers.addParser("add").help("Type \"add -h\" to get more help.");
		import_parser.setDefault("action", new AddAction());
		import_parser.addArgument("-s", "--source").nargs("*");
	}
	
}

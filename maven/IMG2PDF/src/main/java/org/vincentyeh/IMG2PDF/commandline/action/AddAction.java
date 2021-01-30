package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.xml.sax.SAXException;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class AddAction extends CreateAction {

	protected List<String> sources;
	protected FileFilterHelper filter;

	public AddAction() {

	}

	@Override
	public void setupByNamespace(Namespace ns) {
		super.setupByNamespace(ns);
		this.sources = ns.get("source");
		this.filter = (FileFilterHelper) ns.get("filter");
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

//		Create task list form list_destination.
		TaskList tasks = new TaskList(list_destination);

		for (String source : sources) {
			tasks.addAll(importTasksFromTXT(new File(source), filter));
		}

		tasks.toXMLFile(new File(list_destination));
	}

	public static void setupParser(Subparsers subparsers) {
		Subparser parser = subparsers.addParser("add").help(lagug_resource.getString("help_add"));
		parser.setDefault("action", new AddAction());
		parser.addArgument("-s", "--source").nargs("*").help(lagug_resource.getString("help_add_source"));
		parser.addArgument("-f", "--filter").type(new FileFilterHelper(""))
				.help(lagug_resource.getString("help_add_filter"));

	}

}

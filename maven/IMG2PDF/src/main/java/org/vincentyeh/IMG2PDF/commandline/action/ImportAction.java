package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.task.TaskList;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ImportAction extends CreateAction {

	
	protected ArrayList<String> sources;
	protected FileFilterHelper filter;
	public ImportAction() {

	}

	@Override
	public void start() {
		try {
			createFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setupByNamespace(Namespace ns) {
		super.setupByNamespace(ns);
		this.sources = (ArrayList<String>) ns.get("source");
		this.filter=(FileFilterHelper)ns.get("filter");
//		https://regex101.com/
		
	}

	public void createFile() throws IOException {
//		ConfigureTaskList tasks = new ConfigureTaskList();
		TaskList tasks=new TaskList();
		
		for (String source : sources) {
				tasks.addAll(importTasksFromTXT(new File(source),filter));
		}
		
		tasks.toXMLFile(new File(list_destination));
	}

	public static void setupParser(Subparsers subparsers) {
		Subparser parser = subparsers.addParser("import").help(lagug_resource.getString("help_import"));
		parser.setDefault("action", new ImportAction());
		parser.addArgument("-s", "--source").nargs("*").help(lagug_resource.getString("help_import_source"));
		parser.addArgument("-f", "--filter").type(new FileFilterHelper("")).help(lagug_resource.getString("help_import_filter"));
		
	}
}
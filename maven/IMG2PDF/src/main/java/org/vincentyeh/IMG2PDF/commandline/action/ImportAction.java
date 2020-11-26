package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.vincentyeh.IMG2PDF.commandline.MainProgram;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.file.text.UTF8InputStream;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.task.TaskList;
import org.vincentyeh.IMG2PDF.util.NameFormatter;

import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ImportAction extends CreateAction {

	
	protected ArrayList<String> sources;
	protected String filter;
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
		this.filter=ns.getString("filter");
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
		parser.addArgument("-f", "--filter").type(String.class).help(lagug_resource.getString("help_import_filter"));
		
	}
}
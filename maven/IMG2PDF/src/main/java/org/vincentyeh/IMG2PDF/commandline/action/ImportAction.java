package org.vincentyeh.IMG2PDF.commandline.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.vincentyeh.IMG2PDF.commandline.action.exception.SourceFolderException;
import org.vincentyeh.IMG2PDF.file.FileFilterHelper;
import org.vincentyeh.IMG2PDF.task.TaskList;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ImportAction extends CreateAction {

	protected List<String> sources;
	protected FileFilterHelper filter;

	public ImportAction() {

	}

	@Override
	public void start() {
//		Create empty task list.
		TaskList tasks = new TaskList();
		
		for (String source : sources) {
			try {
				tasks.addAll(importTasksFromTXT(new File(source), filter));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		tasks.toXMLFile(new File(list_destination));
	}

	@Override
	public void setupByNamespace(Namespace ns) {
		super.setupByNamespace(ns);
		this.sources = ns.getList("source");
		this.filter = (FileFilterHelper) ns.get("filter");

	}

	public static void setupParser(Subparsers subparsers) {
		Subparser parser = subparsers.addParser("import").help(lagug_resource.getString("help_import"));
		parser.setDefault("action", new ImportAction());
		parser.addArgument("-s", "--source").type(String.class).nargs("*")
				.help(lagug_resource.getString("help_import_source"));
		parser.addArgument("-f", "--filter").type(new FileFilterHelper(""))
				.help(lagug_resource.getString("help_import_filter"));

	}
}
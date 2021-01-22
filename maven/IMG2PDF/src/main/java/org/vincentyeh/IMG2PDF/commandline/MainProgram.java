package org.vincentyeh.IMG2PDF.commandline;

import java.util.Locale;
import java.util.ResourceBundle;
import org.vincentyeh.IMG2PDF.commandline.action.AbstractAction;
import org.vincentyeh.IMG2PDF.commandline.action.ConvertAction;
import org.vincentyeh.IMG2PDF.commandline.action.CreateAction;
import org.vincentyeh.IMG2PDF.commandline.action.exception.SourceFolderException;
import org.vincentyeh.IMG2PDF.util.ArgumentUtil;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

public class MainProgram {
	public static ResourceBundle lagug_resource;
	
	public final static String PROGRAM_NAME = "IMG2PDF";
	public final static String PROGRAM_VERSION = "v0.9";
	private final AbstractAction action;

	static {
		lagug_resource = ResourceBundle.getBundle("language_package", Locale.getDefault());
	}

	public MainProgram(String[] args) throws ArgumentParserException {
		args=ArgumentUtil.fixArgumentSpaceArray(ArgumentUtil.fixSymbol(args));
		
		ArgumentParser parser = ArgumentParsers.newFor(PROGRAM_NAME).build();
		parser.version(PROGRAM_VERSION);

		AbstractAction.setLagug_resource(lagug_resource);
		parser.description(lagug_resource.getString("root_description"));
		Subparsers subparser = parser.addSubparsers().help(lagug_resource.getString("help_action"));
		CreateAction.setupParser(subparser);
		ConvertAction.setupParser(subparser);

		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);

		} catch (ArgumentParserException e) {
			parser.handleError(e);

			System.err.println("Program is interrupted.");
			throw e;
//			System.exit(1);
		}

		if (ns == null)
			throw new NullPointerException("Namespace is null.");

		action = (AbstractAction) ns.get("action");
		if (action == null)
			throw new NullPointerException("action==null.");
		action.setupByNamespace(ns);
	}

	public void startCommand() {
		try {
			action.start();
		} catch (SourceFolderException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void main(String[] args) {

		if (args == null) {
			System.err.println("args is null.");
			return;
		}

		MainProgram main = null;
		try {
			main = new MainProgram(args);
			main.startCommand();
		} catch (ArgumentParserException e) {
//			System.exit(0);
			return;
		}
	}

//	Only for test.This function never been used in the release executable file.
	public static void main(String args){
		main(args.split("\\s"));
	}
	
}

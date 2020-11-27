package org.vincentyeh.IMG2PDF.commandline;

import java.util.Locale;
import java.util.ResourceBundle;

import org.vincentyeh.IMG2PDF.commandline.action.AbstractAction;
import org.vincentyeh.IMG2PDF.commandline.action.ConvertAction;
import org.vincentyeh.IMG2PDF.commandline.action.CreateAction;

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
		action.setupByNamespace(ns);
	}

	public void startCommand() {
		if (action == null)
			throw new NullPointerException("Action is null.");
		action.start();
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
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

//	Only for test.This function never been used in the release executable file.
	public static void main(String args) throws ArgumentParserException {
		main(args.split("\\s"));
	}

}

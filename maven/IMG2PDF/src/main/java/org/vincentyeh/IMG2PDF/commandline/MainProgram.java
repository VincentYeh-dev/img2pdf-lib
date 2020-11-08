package org.vincentyeh.IMG2PDF.commandline;

import org.vincentyeh.IMG2PDF.commandline.action.Action;
import org.vincentyeh.IMG2PDF.commandline.action.ConvertAction;
import org.vincentyeh.IMG2PDF.commandline.action.CreateAction;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

public class MainProgram {
	public static String PROGRAM_NAME = "IMG2PDF";
	private final Action action;
	public MainProgram(String[] args) {
		ArgumentParser parser = ArgumentParsers.newFor(PROGRAM_NAME).build();
		Subparsers subparser = parser.addSubparsers().help("sub-command help");
		CreateAction.setupParser(subparser);
		ConvertAction.setupParser(subparser);
		Namespace ns = null;
		
		try {
			ns = parser.parseArgs(args);

		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
		if(ns==null) {
			System.err.println("Namespace is null.");
		}
		
		action = (Action) ns.get("action");
		action.setupByNamespace(ns);
	}
	
	public void startCommand() {
		action.start();
	}

	public static void main(String[] args) {
		MainProgram main =new MainProgram(args);
		main.startCommand();
	}

}

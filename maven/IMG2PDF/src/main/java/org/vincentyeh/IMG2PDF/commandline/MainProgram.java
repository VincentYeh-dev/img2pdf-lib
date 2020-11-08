package org.vincentyeh.IMG2PDF.commandline;


import java.io.IOException;

import org.vincentyeh.IMG2PDF.commandline.action.Action;
import org.vincentyeh.IMG2PDF.commandline.action.ConvertAction;
import org.vincentyeh.IMG2PDF.commandline.action.CreateAction;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

public class MainProgram {

	public static void main(String[] args) throws IOException {
		ArgumentParser parser = ArgumentParsers.newFor("prog").build();
		Subparsers subparser = parser.addSubparsers().help("sub-command help");
		CreateAction.setupParser(subparser);
		ConvertAction.setupParser(subparser);

		try {
			Namespace ns = parser.parseArgs(args);
			Action action = (Action) ns.get("action");
			action.setupByNamespace(ns);
			action.start();

		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}

	}

}

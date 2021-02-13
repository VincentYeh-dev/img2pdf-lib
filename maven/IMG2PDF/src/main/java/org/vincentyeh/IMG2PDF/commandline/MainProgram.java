package org.vincentyeh.IMG2PDF.commandline;

import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.vincentyeh.IMG2PDF.commandline.action.AbstractAction;
import org.vincentyeh.IMG2PDF.commandline.action.ConvertAction;
import org.vincentyeh.IMG2PDF.commandline.action.CreateAction;
import org.vincentyeh.IMG2PDF.util.ArgumentUtil;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

public class MainProgram {
	public static ResourceBundle lagug_resource;

	public final static String PROGRAM_NAME = "IMG2PDF";
	private AbstractAction action;

	static {
		lagug_resource = ResourceBundle.getBundle("language_package", Locale.getDefault());
	}

	public MainProgram(String[] args) throws ParseException {
		
		
		Options options = new Options();

		Option opt_mode = new Option("m", "mode", true, "mode");
		options.addOption(opt_mode);

		CommandLineParser parser = new RelaxedParser();
		
		CommandLine mode_chooser = parser.parse(options, args);

//		action = new CreateAction(args);
		
		if (mode_chooser.hasOption("mode")) {
			String value = mode_chooser.getOptionValue("mode");
			if (value.equals("create")) {
				System.out.println("create");
				try {
					action = new CreateAction(args);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (value.equals("convert")) {
				System.out.println("convert");

				try {
					action = new ConvertAction(args);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				throw new RuntimeException("sss");
			}

			try {
				action.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	public void startCommand() {
		try {
			action.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			MainProgram main = new MainProgram(args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		main.startCommand();
	}

}

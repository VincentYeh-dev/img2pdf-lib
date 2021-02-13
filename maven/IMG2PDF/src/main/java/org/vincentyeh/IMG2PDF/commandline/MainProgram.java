package org.vincentyeh.IMG2PDF.commandline;

import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.vincentyeh.IMG2PDF.commandline.action.AbstractAction;
import org.vincentyeh.IMG2PDF.commandline.action.ConvertAction;
import org.vincentyeh.IMG2PDF.commandline.action.CreateAction;
import org.vincentyeh.IMG2PDF.commandline.action.exception.HelperException;


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
		Option opt_help = new Option("h", "help", false, "help");
		options.addOption(opt_mode);
		options.addOption(opt_help);

		CommandLineParser parser = new RelaxedParser();

		CommandLine mode_chooser = parser.parse(options, args);

//		action = new CreateAction(args);
		
		try {
			
			if (args == null || args.length == 0 || (mode_chooser.hasOption("help") && args.length == 1)) {
				throw new HelperException(options);
			}
			
			if (mode_chooser.hasOption("mode")) {
				String value = mode_chooser.getOptionValue("mode");

				if (value == null)
					throw new NullPointerException();
				else if (value.equals("create")) {
					System.out.println("create");
					action = new CreateAction(args);

				} else if (value.equals("convert")) {
					System.out.println("convert");
					action = new ConvertAction(args);
				} else {
					throw new RuntimeException("sss");
				}

			}

		} catch (HelperException e) {
			System.err.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ant", e.opt);
		}catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startCommand() {
		try {
			if (action != null)
				action.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			MainProgram main = new MainProgram(args);
			main.startCommand();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

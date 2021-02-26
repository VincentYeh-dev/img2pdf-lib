package org.vincentyeh.IMG2PDF.commandline;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.vincentyeh.IMG2PDF.commandline.action.AbstractAction;
import org.vincentyeh.IMG2PDF.commandline.action.ActionMode;
import org.vincentyeh.IMG2PDF.commandline.action.ConvertAction;
import org.vincentyeh.IMG2PDF.commandline.action.CreateAction;
import org.vincentyeh.IMG2PDF.commandline.action.exception.HelperException;
import org.vincentyeh.IMG2PDF.commandline.action.exception.UnrecognizedEnumException;

public class MainProgram {
	private AbstractAction action;

	private static final Option opt_help = new Option("h", "help", false, "help");

	static {
//	Local:
		Configuration.setLagugRes(ResourceBundle.getBundle("language_package", Locale.getDefault()));
//	root:

//		Configuration.setLagugRes(ResourceBundle.getBundle("language_package", Locale.ROOT));
	}

	public MainProgram(String[] args) throws ParseException {

		System.out.println("##IMG2PDF##");
		System.out.printf("%s: %s", Configuration.getResString("common_developer"), Configuration.DEVELOPER);
		System.out.printf("\n%s: %s\n", Configuration.getResString("common_version"), Configuration.PROGRAM_VER);
		System.out.println("-----------------------");

//		Options options = new Options();
//		Option opt_mode = new Option("m", "mode", true,
//				String.format(Configuration.getResString("root_mode"), listEnum(ActionMode.class)));
//		Option opt_help = new Option("h", "help", false, Configuration.getResString("root_help"));
//
//		options.addOption(opt_mode);
//		options.addOption(opt_help);
		Options options = setupOptions();

		CommandLineParser parser = new RelaxedParser();

		try {

			CommandLine mode_chooser = parser.parse(options, args);

			if (args == null || args.length == 0 || (mode_chooser.hasOption("help") && args.length == 1)) {
				throw new HelperException(options);
			}

			if (mode_chooser.hasOption("mode")) {
				String value = mode_chooser.getOptionValue("mode");

				if (value == null)
					throw new NullPointerException();
				else if (value.equals("create")) {
					action = new CreateAction(args);

				} else if (value.equals("convert")) {
					action = new ConvertAction(args);
				} else {
					throw new HelperException(options);
				}

			}

		} catch (HelperException e) {
//			System.err.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Configuration.PROGRAM_NAME, e.opt);
		} catch (UnrecognizedOptionException e) {
//			e.printStackTrace();
			System.err.println(e.getMessage());
		} catch (UnrecognizedEnumException e) {
			System.err.println(e.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Options setupOptions() {

		Options options = new Options();
		Option opt_mode = new Option("m", "mode", true,
				String.format(Configuration.getResString("root_mode"), listEnum(ActionMode.class)));
		
		options.addOption(opt_mode);
		options.addOption(opt_help);

		return options;
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
//		dumpArray(args);
		try {
			MainProgram main = new MainProgram(args);
			main.startCommand();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static <T> void dumpArray(T[] array) {
		System.out.print("[");
		System.out.print(array[0]);
		for (int i = 1; i < array.length; i++) {
			System.out.print(",");
			System.out.print(array[i]);
		}
		System.out.print("]\n");
	}

	protected static <T> String listEnum(Class<T> _class_enum) {
		T[] enums = _class_enum.getEnumConstants();
		StringBuilder sb = new StringBuilder();
		sb.append(enums[0].toString());
		for (int i = 1; i < enums.length; i++) {
			sb.append(",");
			sb.append(enums[i].toString());
		}
		return sb.toString();
	}

	protected static Option createArgOption(String opt, String longOpt, String res_description) {
		return new Option(opt, longOpt, true, Configuration.getResString(res_description));
	}

	protected static <T> Option createEnumOption(String opt, String longOpt, String res_description, Class<T> _enum) {
		return new Option(opt, longOpt, true,
				String.format(Configuration.getResString(res_description), listEnum(_enum)));
	}

	protected static Option createOption(String opt, String longOpt, String res_description) {
		return new Option(opt, longOpt, false, Configuration.getResString(res_description));
	}

}

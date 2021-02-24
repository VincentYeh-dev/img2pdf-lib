package org.vincentyeh.IMG2PDF.commandline.action;


import org.apache.commons.cli.Option;
import org.vincentyeh.IMG2PDF.commandline.Configuration;

public abstract class AbstractAction implements Action {
	
	public void start() throws Exception {
		System.out.println("\n##IMG2PDF##");
		System.out.println("Developer: VincentYeh-dev");
		System.out.println("Version: XXX");
		System.out.println("-----------------------");

	}

	public void done() {
		System.out.println("DONE.");
	}

	protected static Option createArgOption(String opt, String longOpt, String res_description) {
		return new Option(opt, longOpt, true,Configuration.getResString(res_description));
	}

	protected static <T> Option createEnumOption(String opt, String longOpt, String res_description, Class<T> _enum) {
		return new Option(opt, longOpt, true,
				String.format(Configuration.getResString(res_description), listEnum(_enum)));
	}

	protected static Option createOption(String opt, String longOpt, String res_description) {
		return new Option(opt, longOpt, false, Configuration.getResString(res_description));
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
}

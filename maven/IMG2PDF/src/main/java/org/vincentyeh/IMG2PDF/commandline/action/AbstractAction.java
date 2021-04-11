package org.vincentyeh.IMG2PDF.commandline.action;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.vincentyeh.IMG2PDF.SharedSpace;

public abstract class AbstractAction implements Action {
	protected Options options;

	public AbstractAction(Options options) {
		this.options = options;
	}

//	protected static Option createArgOption(String opt, String longOpt, String res_description,Object ...values) {
//	    return new PropertiesOption(opt,longOpt,true,res_description,values);
//		return new Option(opt, longOpt, true, Configuration.getResString(res_description));
//	}

//	protected static <T> Option createEnumOption(String opt, String longOpt, String res_description, Class<T> _enum) {
//		return new Option(opt, longOpt, true,
//				String.format(Configuration.getResString(res_description), listEnum(_enum)));
//	}

//	protected static Option createOption(String opt, String longOpt, String res_description) {
//		return new Option(opt, longOpt, false, Configuration.getResString(res_description));
//	}

	/**
	 * Build the exception message from the specified list of options.
	 *
	 * @param missingOptions the list of missing options and groups
	 */
	protected static String createMissingOptionsMessage(List<?> missingOptions) {
		StringBuilder buf = new StringBuilder(SharedSpace.getResString("err_missing_option"));
		buf.append(": ");

		Iterator<?> it = missingOptions.iterator();
		while (it.hasNext()) {
			buf.append(it.next());
			if (it.hasNext()) {
				buf.append(", ");
			}
		}

		return buf.toString();
	}
	
	protected static String createMissingArgumentOptionsMessage(Option option) {
		 return String.format(SharedSpace.getResString("err_missing_argument_option"),option.getOpt());
	}
	
	protected static String createUnrecognizedOptionMessage(String option) {
		 return String.format(SharedSpace.getResString("err_missing_argument_option"),option);
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

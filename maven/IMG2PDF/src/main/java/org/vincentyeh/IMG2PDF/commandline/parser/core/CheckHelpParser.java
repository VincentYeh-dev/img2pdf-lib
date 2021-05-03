package org.vincentyeh.IMG2PDF.commandline.parser.core;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.vincentyeh.IMG2PDF.commandline.parser.exception.HelperException;

public class CheckHelpParser extends RelaxedParser{
	private final Option helperOption;

	public CheckHelpParser(Option option) {
		helperOption = option;
	}

	@Override
	public CommandLine parse(Options options, String[] arguments) throws ParseException {
		super.options=options;
		checkHelpOption(arguments);
		return super.parse(options, arguments);
	}

	protected void checkHelpOption(String[] arguments) throws ParseException {
		Options opts = new Options();
		opts.addOption(helperOption);
		CommandLine cmd = ((CommandLineParser) new RelaxedParser()).parse(opts, arguments);
		if (cmd.hasOption(helperOption.getOpt()))
			throw new HelperException(options);
	}
}

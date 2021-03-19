package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.vincentyeh.IMG2PDF.commandline.action.exception.HelperException;

public class CheckHelpParser extends DefaultParser {
	private final Option helperOption;

	public CheckHelpParser(Option option) {
		helperOption = option;
	}

	@Override
	public CommandLine parse(Options options, String[] arguments) throws ParseException {
		Options opts = new Options();
		opts.addOption(helperOption);

		CommandLineParser parser = new RelaxedParser();

		CommandLine cmd = parser.parse(opts, arguments);

		if (cmd.hasOption(helperOption.getOpt()))
			throw new HelperException(options);

		return super.parse(options, arguments);
	}

}

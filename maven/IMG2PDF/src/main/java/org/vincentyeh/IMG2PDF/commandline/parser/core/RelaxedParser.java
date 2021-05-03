package org.vincentyeh.IMG2PDF.commandline.parser.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class RelaxedParser extends DefaultParser {

    @Override
    public CommandLine parse(Options options, String[] arguments) throws ParseException {
        super.options = options;

        final List<String> knownArgs = new ArrayList<>();
        for (int i = 0; i < arguments.length; i++) {
            if (isKnownOption(arguments[i])) {
                knownArgs.add(arguments[i]);

                if (i + 1 >= arguments.length)
                    continue;

                if (hasArgument(arguments[i])) {
                    knownArgs.add(arguments[i + 1]);
                }
            }
        }
        return super.parse(options, knownArgs.toArray(new String[0]));
    }

    private boolean isKnownOption(String option) {
        return options.hasOption(option);
    }

    private boolean hasArgument(String option) {
        return options.getOption(option).hasArg();
    }


}

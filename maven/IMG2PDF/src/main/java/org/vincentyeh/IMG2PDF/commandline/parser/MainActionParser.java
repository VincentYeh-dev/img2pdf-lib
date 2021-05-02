package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.*;
import org.vincentyeh.IMG2PDF.commandline.option.MultiLanguageOptionFactory;
import org.vincentyeh.IMG2PDF.commandline.action.ActionMode;
import org.vincentyeh.IMG2PDF.commandline.action.MainAction;
import org.vincentyeh.IMG2PDF.commandline.parser.exception.HelperException;

public class MainActionParser extends ActionParser<MainAction> {

    @Override
    public MainAction parse(String[] arguments) throws ParseException {
        CommandLine cmd = parser.parse(options, arguments, true);

        if (arguments.length == 0 || cmd.hasOption("help") && !cmd.hasOption("mode")) {
            throw new HelperException(options);
        }

        return new MainAction(arguments, getActionMode(cmd));
    }

    private ActionMode getActionMode(CommandLine cmd) throws HelperException {
        if (cmd.hasOption("mode")) {
            return ActionMode.valueOf(cmd.getOptionValue("mode"));
        } else {
            throw new HelperException(options);
        }
    }

    public MainActionParser() {
        super(MultiLanguageOptionFactory.getOption("h", "help", "main.help"));
        Option opt_help = MultiLanguageOptionFactory.getOption("h", "help", "main.help");
        Option opt_mode = MultiLanguageOptionFactory.getArgumentOption("m", "mode", "main.arg.mode.help", listStringArray(ArrayToStringArray(ActionMode.values())));
        options.addOption(opt_mode);
        options.addOption(opt_help);
    }

}

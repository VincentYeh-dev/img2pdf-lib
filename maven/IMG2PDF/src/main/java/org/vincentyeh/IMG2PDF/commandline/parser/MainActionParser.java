package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.*;
import org.vincentyeh.IMG2PDF.commandline.option.MultiLanguageOptionFactory;
import org.vincentyeh.IMG2PDF.commandline.action.ActionMode;
import org.vincentyeh.IMG2PDF.commandline.action.MainAction;
import org.vincentyeh.IMG2PDF.commandline.parser.exception.HelperException;

public class MainActionParser extends ActionParser<MainAction> {

    @Override
    public MainAction parse(String[] arguments) throws ParseException {
        CommandLine mode_chooser = parser.parse(options, arguments, true);

        if (arguments.length == 0 || mode_chooser.hasOption("help") && !mode_chooser.hasOption("mode")) {
            throw new HelperException(options);
        }

        ActionMode mode;
        if (mode_chooser.hasOption("mode")) {
            mode = ActionMode.valueOf(mode_chooser.getOptionValue("mode"));

        } else {
            throw new HelperException(options);
        }
        return new MainAction(arguments, mode);
    }


    public MainActionParser() {
        super(MultiLanguageOptionFactory.getOption("h", "help", "main.help"));
        Option opt_help = MultiLanguageOptionFactory.getOption("h", "help", "main.help");
        Option opt_mode = MultiLanguageOptionFactory.getArgumentOption("m", "mode", "main.arg.mode.help", listStringArray(ArrayToStringArray(ActionMode.values())));
        options.addOption(opt_mode);
        options.addOption(opt_help);

    }

}

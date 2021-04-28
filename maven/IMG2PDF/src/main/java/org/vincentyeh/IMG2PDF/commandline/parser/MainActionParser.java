package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.*;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.PropertiesOption;
import org.vincentyeh.IMG2PDF.commandline.action.ActionMode;
import org.vincentyeh.IMG2PDF.commandline.action.MainAction;
import org.vincentyeh.IMG2PDF.commandline.parser.exception.HelperException;

public class MainActionParser {

    private final Options options = new Options();

    public MainAction parse(String[] arguments) throws ParseException {
       CommandLine mode_chooser = (new DefaultParser()).parse(options, arguments,true);

        if (arguments.length == 0 || mode_chooser.hasOption("help") && !mode_chooser.hasOption("mode")) {
            throw new HelperException(options);
        }

        ActionMode mode;
        if (mode_chooser.hasOption("mode")) {
            mode = ActionMode.getByString(mode_chooser.getOptionValue("mode"));

        } else {
            throw new HelperException(options);
        }
        return new MainAction(arguments,mode);
    }

    protected static String listStringArray(String[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(",");
            sb.append(values[i]);
        }
        return sb.toString();
    }

    protected static <T> String[] ArrayToStringArray(T[] arr1) {
        String[] string_array = new String[arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            string_array[i] = arr1[i].toString();
        }
        return string_array;
    }

    public MainActionParser() {
        Option opt_mode = PropertiesOption.getArgumentOption("m", "mode", "main.arg.mode.help", listStringArray(ArrayToStringArray(ActionMode.values())));
        Option opt_help = PropertiesOption.getOption("h", "help", "main.help");
        options.addOption(opt_mode);
        options.addOption(opt_help);

    }
}

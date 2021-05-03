package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.*;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.action.Action;
import org.vincentyeh.IMG2PDF.commandline.option.MultiLanguageOptionFactory;
import org.vincentyeh.IMG2PDF.commandline.action.ActionMode;
import org.vincentyeh.IMG2PDF.commandline.action.MainAction;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.commandline.parser.exception.HelperException;

import java.util.stream.Collectors;

public class MainActionParser extends ActionParser<MainAction> {

    @Override
    public MainAction parse(String[] arguments) throws Exception {
        CommandLine cmd = parser.parse(options, arguments);

        if (arguments.length == 0 || cmd.hasOption("help") && !cmd.hasOption("mode")) {
            throw new HelperException(options);
        }

        return new MainAction(getAction(cmd, arguments));
    }

    private Action getAction(CommandLine cmd, String[] arguments) throws Exception {
        if (cmd.hasOption("mode")) {
            return parseSubAction(getValueOfActionMode(cmd.getOptionValue("mode")), arguments);
        } else {
            throw new HelperException(options);
        }
    }

    private ActionMode getValueOfActionMode(String value) throws HandledException {
        return getValueOfEnum(ActionMode.class, value);
    }

    private Action parseSubAction(ActionMode mode, String[] arguments) throws Exception {
        try {
            return mode.getParser().parse(arguments);
        } catch (HelperException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SharedSpace.Configuration.PROGRAM_NAME, e.getOptions());
            throw new HandledException(e, getClass());
        } catch (MissingOptionException e) {
            System.err.printf(SharedSpace.getResString("argperser.err.missing_option") + "\n", e.getMissingOptions().stream().map(Object::toString).collect(Collectors.joining(",")));
            throw new HandledException(e, getClass());
        } catch (MissingArgumentException e) {
            System.err.printf(SharedSpace.getResString("argperser.err.missing_argument_option") + "\n", e.getOption().getOpt());
            throw new HandledException(e, getClass());
        } catch (UnrecognizedOptionException e) {
            System.err.printf(SharedSpace.getResString("public.err.unrecognized_argument_option") + "\n", e.getOption());
            throw new HandledException(e, getClass());
        }
    }


    public MainActionParser() {
        super(MultiLanguageOptionFactory.getOption("h", "help", "main.help"));
        Option opt_help = MultiLanguageOptionFactory.getOption("h", "help", "main.help");
        Option opt_mode = MultiLanguageOptionFactory.getArgumentOption("m", "mode", "main.arg.mode.help", listEnum(ActionMode.class));
        options.addOption(opt_mode);
        options.addOption(opt_help);
    }

}

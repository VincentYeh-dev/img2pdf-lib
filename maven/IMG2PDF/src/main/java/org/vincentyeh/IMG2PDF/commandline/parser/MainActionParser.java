package org.vincentyeh.IMG2PDF.commandline.parser;

import org.apache.commons.cli.*;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.action.Action;
import org.vincentyeh.IMG2PDF.commandline.option.MultiLanguageOptionFactory;
import org.vincentyeh.IMG2PDF.commandline.action.ActionMode;
import org.vincentyeh.IMG2PDF.commandline.action.MainAction;
import org.vincentyeh.IMG2PDF.commandline.parser.core.CheckHelpParser;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.commandline.parser.core.RelaxedParser;
import org.vincentyeh.IMG2PDF.commandline.parser.exception.HelperException;

import java.util.stream.Collectors;

public class MainActionParser extends ActionParser<MainAction> {
    private final CommandLineParser parser;

    @Override
    public MainAction parse(String[] arguments) throws Exception {
        CommandLine cmd = parser.parse(options, arguments);

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
            formatter.printHelp(SharedSpace.Constance.PROGRAM_NAME, e.getOptions());
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
        Option opt_help = MultiLanguageOptionFactory.getOption("h", "help", "main.help");
        Option opt_mode = MultiLanguageOptionFactory.getArgumentOption("m", "mode", "main.arg.mode.help", listEnum(ActionMode.class));
        Options mainOption=options;
        mainOption.addOption(opt_mode);
        mainOption.addOption(opt_help);

        parser = new CheckHelpParser(opt_help) {
            @Override
            protected void checkHelpOption(String[] arguments) throws HelperException {
                RelaxedParser parser=new RelaxedParser();
                CommandLine cmd;

                try {
                    cmd=parser.parse(mainOption,arguments);
                } catch (ParseException e) {
                    throw new RuntimeException();
                }

                if (arguments.length == 0 || cmd.hasOption("h") && !cmd.hasOption("m")) {
                    throw new HelperException(mainOption);
                }
            }
        };

    }

}

package org.vincentyeh.IMG2PDF.commandline.action;

import org.apache.commons.cli.*;
import org.vincentyeh.IMG2PDF.SharedSpace;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.commandline.parser.exception.HelperException;

import java.util.stream.Collectors;

public class MainAction implements Action {
    private final ActionMode mode;
    private final String[] arguments;


    public MainAction(String[] arguments, ActionMode mode) {
        this.arguments = arguments;
        this.mode = mode;
    }

    @Override
    public void start() throws Exception {
        System.out.println("##IMG2PDF##");
        System.out.printf("%s: %s", SharedSpace.getResString("public.info.developer"), SharedSpace.Configuration.DEVELOPER);
        System.out.printf("\n%s: %s\n", SharedSpace.getResString("public.info.version"), SharedSpace.Configuration.PROGRAM_VER);
        System.out.println("-----------------------");
        Action action = getParsedAction(mode);
        action.start();
    }

    private Action getParsedAction(ActionMode mode) throws Exception {
        try {
            return mode.getParser().parse(arguments);

        } catch (HelperException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SharedSpace.Configuration.PROGRAM_NAME, e.opt);
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
}

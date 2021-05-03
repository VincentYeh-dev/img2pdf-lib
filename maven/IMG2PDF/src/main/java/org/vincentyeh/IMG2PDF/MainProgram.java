package org.vincentyeh.IMG2PDF;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.vincentyeh.IMG2PDF.commandline.action.MainAction;
import org.vincentyeh.IMG2PDF.commandline.parser.MainActionParser;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.commandline.parser.exception.HelperException;

import java.util.stream.Collectors;

public class MainProgram {
    private final String[] args;

    private MainProgram(String[] args){
        this.args = args;
    }

    public static void main(String[] args) {
        MainProgram main=new MainProgram(args);
        try {
            main.start();
        }catch (HandledException e){
           System.err.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() throws Exception {
        MainActionParser parser = new MainActionParser();
        try {
            MainAction action = parser.parse(args);
            action.start();
        } catch (HelperException e) {
//            e.printStackTrace();
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SharedSpace.Configuration.PROGRAM_NAME, e.getOptions());
            throw new HandledException(e,getClass());
        } catch (MissingOptionException e) {
            System.err.printf(SharedSpace.getResString("argperser.err.missing_option") + "\n", e.getMissingOptions().stream().map(Object::toString).collect(Collectors.joining(",")));
            throw new HandledException(e,getClass());
        } catch (MissingArgumentException e) {
            System.err.printf(SharedSpace.getResString("argperser.err.missing_argument_option") + "\n", e.getOption().getOpt());
            throw new HandledException(e,getClass());
        } catch (UnrecognizedOptionException e) {
            System.err.printf(SharedSpace.getResString("public.err.unrecognized_argument_option") + "\n", e.getOption());
            throw new HandledException(e,getClass());
        }
    }
}

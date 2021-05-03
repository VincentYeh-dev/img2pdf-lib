package org.vincentyeh.IMG2PDF;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.vincentyeh.IMG2PDF.commandline.action.MainAction;
import org.vincentyeh.IMG2PDF.commandline.parser.MainActionParser;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.commandline.parser.exception.HelperException;

import java.util.Iterator;

public class MainProgram {
    public static void main(String[] args) {
        MainActionParser parser = new MainActionParser();
        try {
            MainAction action = parser.parse(args);
            action.start();
        } catch (HelperException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SharedSpace.Configuration.PROGRAM_NAME, e.getOptions());
        } catch (MissingOptionException e) {
            StringBuilder buf = new StringBuilder();
            Iterator<?> it = e.getMissingOptions().iterator();
            while (it.hasNext()) {
                buf.append(it.next());
                if (it.hasNext()) {
                    buf.append(", ");
                }
            }
            System.err.printf(SharedSpace.getResString("argperser.err.missing_option") + "\n", buf);
        } catch (MissingArgumentException e) {
            System.err.printf(SharedSpace.getResString("argperser.err.missing_argument_option") + "\n", e.getOption().getOpt());
        } catch (UnrecognizedOptionException e) {
            System.err.printf(SharedSpace.getResString("public.err.unrecognized_argument_option") + "\n", e.getOption());
        }catch (HandledException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

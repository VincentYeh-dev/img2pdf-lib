package org.vincentyeh.IMG2PDF.commandline;

import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.vincentyeh.IMG2PDF.commandline.action.AbstractAction;
import org.vincentyeh.IMG2PDF.commandline.action.ActionMode;
import org.vincentyeh.IMG2PDF.commandline.action.ConvertAction;
import org.vincentyeh.IMG2PDF.commandline.action.CreateAction;
import org.vincentyeh.IMG2PDF.commandline.action.exception.HelperException;
import org.vincentyeh.IMG2PDF.commandline.parser.RelaxedParser;

public class MainProgram extends AbstractAction {

    private static final Option opt_help;

    static {
//		Language Setting:

//	Local:
        Configuration.setLagugRes(ResourceBundle.getBundle("language_package", Locale.getDefault()));
//	root:

//		Configuration.setLagugRes(ResourceBundle.getBundle("language_package", Locale.ROOT));

        opt_help = createOption("h", "help", "root_help");
    }

    private AbstractAction action;
    private ActionMode mode;

    public MainProgram(String[] args) throws MissingOptionException, ParseException, FileNotFoundException {
        super(getLocaleOptions());

        System.out.println("##IMG2PDF##");
        System.out.printf("%s: %s", Configuration.getResString("common_developer"), Configuration.DEVELOPER);
        System.out.printf("\n%s: %s\n", Configuration.getResString("common_version"), Configuration.PROGRAM_VER);
        System.out.println("-----------------------");

        CommandLine mode_chooser = (new RelaxedParser()).parse(options, args);

        if (args.length == 0 || mode_chooser.hasOption("help") && !mode_chooser.hasOption("mode")) {
            throw new HelperException(options);
        }

        if (mode_chooser.hasOption("mode")) {
            mode = ActionMode.getByString(mode_chooser.getOptionValue("mode"));

        } else {
            throw new HelperException(options);
        }

        switch (mode) {
            case create:
                action = new CreateAction(args);
                break;
            case convert:
                action = new ConvertAction(args);
                break;
        }
    }

    @Override
    public void start() throws Exception {
        action.start();
    }

    private static Options getLocaleOptions() {

        Options options = new Options();
        Option opt_mode = createEnumOption("m", "mode", "root_mode", ActionMode.class);
//		opt_mode.setRequired(true);
        options.addOption(opt_mode);
        options.addOption(opt_help);

        return options;
    }

    public static void main(String[] args) {
//		dumpArray(args);
        MainProgram main = null;
        try {
            main = new MainProgram(args);
        } catch (HelperException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Configuration.PROGRAM_NAME, e.opt);
            return;
        } catch (MissingOptionException e) {
            System.err.println(createMissingOptionsMessage(e.getMissingOptions()));
            return;
        } catch (MissingArgumentException e) {
            System.err.println(createMissingArgumentOptionsMessage(e.getOption()));
            return;
        } catch (UnrecognizedOptionException e) {
//		e.printStackTrace();
            System.err.println(createUnrecognizedOptionMessage(e.getOption()));
            System.err.println(e.getMessage());
            return;
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return;
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            main.start();
		}catch (RuntimeException e){
			System.err.println(e.getMessage());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static <T> void dumpArray(T[] array) {
        System.out.print("[");
        System.out.print(array[0]);
        for (int i = 1; i < array.length; i++) {
            System.out.print(",");
            System.out.print(array[i]);
        }
        System.out.print("]\n");
    }

}

package org.vincentyeh.IMG2PDF;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.vincentyeh.IMG2PDF.commandline.command.ConvertCommand;
import org.vincentyeh.IMG2PDF.commandline.command.IMG2PDFCommand;
import org.vincentyeh.IMG2PDF.commandline.handler.CommandlineParameterHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ErrorHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ResourceBundleHandler;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.util.PrinterUtils;
import picocli.CommandLine;

import java.io.File;


public class MainProgram {

    public static void main(String[] args) {
        try {
            AnsiConsole.systemInstall();
            ConfigurationAgent.loadOrCreateProperties(new File("config.properties"));
            ResourceBundleHandler.setResourceBundle(ConfigurationAgent.getHandlerResourceBundle());

            CommandLine cmd = new CommandLine(new IMG2PDFCommand());
            cmd.addSubcommand(new ConvertCommand(ConfigurationAgent.getConvertConfig()));
            cmd.setParameterExceptionHandler(new CommandlineParameterHandler(ConfigurationAgent.getHandlerResourceBundle()));
            cmd.setResourceBundle(ConfigurationAgent.getCommandResourceBundle());

            int exitCode = cmd.execute(args);
            AnsiConsole.systemUninstall();
            System.exit(exitCode);
        } catch (Error e) {
            ErrorHandler handler = new ErrorHandler(null);
            try {
                PrinterUtils.printColor(handler.handle(e), Ansi.Color.RED);
            } catch (Handler.CantHandleException cantHandleException) {
                cantHandleException.printStackTrace();
            }
            System.exit(1000);
        }

    }
}

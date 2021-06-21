package org.vincentyeh.IMG2PDF;

import org.fusesource.jansi.AnsiConsole;
import org.vincentyeh.IMG2PDF.commandline.command.IMG2PDFCommand;
import org.vincentyeh.IMG2PDF.commandline.command.ConvertCommand;
import org.vincentyeh.IMG2PDF.commandline.handler.CommandlineExecutionHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.CommandlineParameterHandler;
import picocli.CommandLine;

import java.io.*;


public class MainProgram {

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        ConfigurationAgent.loadOrCreateProperties(new File("config.properties"));

        CommandLine cmd = new CommandLine(new IMG2PDFCommand());
        cmd.addSubcommand(new ConvertCommand(ConfigurationAgent.getConvertConfig()));

        cmd.setExecutionExceptionHandler(new CommandlineExecutionHandler(ConfigurationAgent.getHandlerResourceBundle()));
        cmd.setParameterExceptionHandler(new CommandlineParameterHandler(ConfigurationAgent.getHandlerResourceBundle()));
        cmd.setResourceBundle(ConfigurationAgent.getCommandResourceBundle());

        int exitCode=cmd.execute(args);
        AnsiConsole.systemUninstall();
        System.exit(exitCode);
    }
}

package org.vincentyeh.IMG2PDF;

import org.vincentyeh.IMG2PDF.commandline.command.IMG2PDFCommand;
import org.vincentyeh.IMG2PDF.commandline.command.ConvertCommand;
import org.vincentyeh.IMG2PDF.commandline.handler.ResourceBundleExecutionHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.ResourceBundleParameterHandler;
import picocli.CommandLine;

import java.io.*;


public class MainProgram {

    public static void main(String[] args) {
        ConfigurationAgent.loadOrCreateProperties(new File("config.properties"));

        CommandLine cmd = new CommandLine(new IMG2PDFCommand());
        cmd.addSubcommand(new ConvertCommand(ConfigurationAgent.getConvertConfig()));

        cmd.setExecutionExceptionHandler(new ResourceBundleExecutionHandler(ConfigurationAgent.getHandlerResourceBundle()));
        cmd.setParameterExceptionHandler(new ResourceBundleParameterHandler(ConfigurationAgent.getHandlerResourceBundle()));
        cmd.setResourceBundle(ConfigurationAgent.getCommandResourceBundle());

        System.exit(cmd.execute(args));
    }
}

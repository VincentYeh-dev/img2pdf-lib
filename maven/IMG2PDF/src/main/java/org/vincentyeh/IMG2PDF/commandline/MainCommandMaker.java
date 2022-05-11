package org.vincentyeh.IMG2PDF.commandline;

import org.vincentyeh.IMG2PDF.commandline.concrete.command.ConvertCommand;
import org.vincentyeh.IMG2PDF.commandline.concrete.command.IMG2PDFCommand;
import org.vincentyeh.IMG2PDF.commandline.concrete.handler.ParameterHandlerAdaptor;
import org.vincentyeh.IMG2PDF.handler.ExceptionHandlerFacade;
import org.vincentyeh.IMG2PDF.resource.ResourceBundleManager;
import picocli.CommandLine;

public class MainCommandMaker {
    public static CommandLine make() {
        ExceptionHandlerFacade.setResourceBundle(ResourceBundleManager.getHandlerResourceBundle());

        final CommandLine cmd = new CommandLine(new IMG2PDFCommand());
        cmd.setResourceBundle(ResourceBundleManager.getCommandResourceBundle());
        cmd.setParameterExceptionHandler(new ParameterHandlerAdaptor(ExceptionHandlerFacade.getParameterExceptionHandler(null)));
        cmd.addSubcommand(new ConvertCommand());
        return cmd;
    }

}

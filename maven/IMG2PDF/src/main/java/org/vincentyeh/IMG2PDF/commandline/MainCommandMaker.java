package org.vincentyeh.IMG2PDF.commandline;

import org.vincentyeh.IMG2PDF.commandline.concrete.command.ConvertCommand;
import org.vincentyeh.IMG2PDF.commandline.concrete.command.IMG2PDFCommand;
import org.vincentyeh.IMG2PDF.commandline.concrete.handler.ParameterHandlerAdaptor;
import org.vincentyeh.IMG2PDF.handler.ExceptionHandlerFacade;
import org.vincentyeh.IMG2PDF.setting.SettingManager;
import picocli.CommandLine;

import java.util.ResourceBundle;

public class MainCommandMaker {
    public static CommandLine make() {
        ExceptionHandlerFacade.setResourceBundle(ResourceBundle.getBundle("handler", SettingManager.getLocale()));

        final CommandLine cmd = new CommandLine(new IMG2PDFCommand());
        cmd.setResourceBundle(ResourceBundle.getBundle("cmd",SettingManager.getLocale() ));
        cmd.setParameterExceptionHandler(new ParameterHandlerAdaptor(ExceptionHandlerFacade.getParameterExceptionHandler(null)));
        cmd.addSubcommand(new ConvertCommand());
        return cmd;
    }

}

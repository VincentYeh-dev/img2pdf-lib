package org.vincentyeh.IMG2PDF;
import org.vincentyeh.IMG2PDF.commandline.IMG2PDFCommand;
import org.vincentyeh.IMG2PDF.commandline.handler.execution.CreateHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.ExecutionHandlerAdaptor;
import org.vincentyeh.IMG2PDF.commandline.handler.ResourceBundleParameterHandler;
import picocli.CommandLine;

import java.util.Locale;
import java.util.ResourceBundle;


public class MainProgram {

    public static void main(String[] args) {
        SharedSpace.initialize();
        Locale locale=SharedSpace.Configuration.locale;
        CommandLine cmd= new CommandLine(new IMG2PDFCommand());
        ExecutionHandlerAdaptor adaptor=new ExecutionHandlerAdaptor();
        adaptor.registerHandler(new CreateHandler());

        cmd.setParameterExceptionHandler(new ResourceBundleParameterHandler(ResourceBundle.getBundle("cmd_err",locale)));
        cmd.setExecutionExceptionHandler(adaptor);

        cmd.setResourceBundle(ResourceBundle.getBundle("cmd",locale));
        int exitCode =cmd.execute(args);
        System.exit(exitCode);
    }
}

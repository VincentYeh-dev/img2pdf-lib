package org.vincentyeh.IMG2PDF;
import org.vincentyeh.IMG2PDF.commandline.IMG2PDFCommand;
import org.vincentyeh.IMG2PDF.commandline.handler.ResourceBundleParameterHandler;
import picocli.CommandLine;

import java.util.ResourceBundle;


public class MainProgram {

    public static void main(String[] args) {
        SharedSpace.initialize();
        CommandLine cmd= new CommandLine(new IMG2PDFCommand());
        cmd.setParameterExceptionHandler(new ResourceBundleParameterHandler(ResourceBundle.getBundle("cmd_err")));
        int exitCode =cmd.execute(args);
        System.exit(exitCode);
    }
}

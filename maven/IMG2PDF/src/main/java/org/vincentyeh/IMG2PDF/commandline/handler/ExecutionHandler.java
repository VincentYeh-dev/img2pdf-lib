package org.vincentyeh.IMG2PDF.commandline.handler;

import org.vincentyeh.IMG2PDF.commandline.handler.core.*;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import picocli.CommandLine;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;

public class ExecutionHandler implements CommandLine.IExecutionExceptionHandler {

    public ExecutionHandler(ResourceBundle resourceBundle) {
        ResourceBundleHandler.setResourceBundle(resourceBundle);
    }

    @Override
    public int handleExecutionException(Exception ex, CommandLine cmd, CommandLine.ParseResult parseResult) {
        ExceptionHandler handler = new PDFConverterExceptionHandler(new DirlistTaskFactoryExceptionHandler(null));
        try {
            printErrorText(cmd, handler.handle(ex));
        } catch (Handler.CantHandleException e) {
            printErrorText(cmd, "Can't handle");
            printStackTrance(cmd,ex);
        }

        return CommandLine.ExitCode.SOFTWARE;
    }

    private void printErrorText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(cmd.getColorScheme().errorText(message)); // bold red
    }
    private void printStackTrance(CommandLine cmd,Exception e){
        PrintWriter printer = cmd.getErr();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        printer.println(cmd.getColorScheme().errorText(sw.toString()));
    }

    private void printText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(message); // bold red
    }

}

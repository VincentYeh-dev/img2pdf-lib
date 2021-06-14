package org.vincentyeh.IMG2PDF.commandline.handler;

import org.vincentyeh.IMG2PDF.commandline.handler.core.*;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import picocli.CommandLine;
import java.io.PrintWriter;
import java.util.ResourceBundle;

public class ExecutionHandler implements CommandLine.IExecutionExceptionHandler {

    public ExecutionHandler(ResourceBundle resourceBundle) {
        ResourceBundleHandler.setResourceBundle(resourceBundle);
    }

    @Override
    public int handleExecutionException(Exception ex, CommandLine cmd, CommandLine.ParseResult parseResult) {
        ExceptionHandler handler = new DirlistExceptionHandler(new PDFConversionExceptionHandler(new SourceFileExceptionHandler(null)));
        String msg;
        try {
            msg = handler.handle(ex);
        } catch (Handler.CantHandleException e) {
            printErrorText(cmd, "Can't handle");
            msg = ex.getMessage();
            ex.printStackTrace();
        }

        printErrorText(cmd, msg);
        return CommandLine.ExitCode.SOFTWARE;
    }

    private void printErrorText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(cmd.getColorScheme().errorText(message)); // bold red
    }

    private void printText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(message); // bold red
    }

}

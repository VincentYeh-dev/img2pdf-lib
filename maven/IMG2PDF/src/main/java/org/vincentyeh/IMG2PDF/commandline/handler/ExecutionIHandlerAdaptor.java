package org.vincentyeh.IMG2PDF.commandline.handler;

import org.vincentyeh.IMG2PDF.commandline.handler.core.ClassHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ListIHandlerRegister;
import picocli.CommandLine;

import java.io.PrintWriter;

public class ExecutionIHandlerAdaptor extends ListIHandlerRegister<ClassHandler> implements CommandLine.IExecutionExceptionHandler {
    @Override
    public int handleExecutionException(Exception e, CommandLine commandLine, CommandLine.ParseResult parseResult) throws Exception {
        for (ClassHandler handler : handlers) {
            if (handler.canHandle(e,commandLine.getCommand().getClass())) {
                handler.parse(e);
            }
            printErrorText(commandLine, handler.getErrorMessage());
            printErrorText(commandLine, "AAA");

            return handler.getExitCode();
        }
        return 0;
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

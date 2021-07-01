package org.vincentyeh.IMG2PDF.commandline.handler;

import org.vincentyeh.IMG2PDF.commandline.handler.core.ParameterExceptionHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ResourceBundleHandler;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.util.ResourceBundle;

import static java.lang.String.format;

public class CommandlineParameterHandler implements CommandLine.IParameterExceptionHandler {

    private final ResourceBundle resourceBundle;

    public CommandlineParameterHandler(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        ResourceBundleHandler.setResourceBundle(resourceBundle);
    }

    @Override
    public int handleParseException(CommandLine.ParameterException ex, String[] strings) {
        CommandLine cmd = ex.getCommandLine();
        Handler<String, Exception> handler = getHandler();
        String msg;
        try {
            msg = handler.handle(ex);
        } catch (Handler.CantHandleException e) {
            printErrorText(cmd, "Can't handle");
            msg = ex.getMessage();
        }

        printErrorText(cmd, msg);

        printErrorText(cmd, cmd.getHelp().fullSynopsis());
        CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();
        printErrorText(cmd, format((resourceBundle.getString("public.try_help")), spec.qualifiedName()));

        return CommandLine.ExitCode.USAGE;
    }

    private Handler<String, Exception> getHandler() {
        return new ParameterExceptionHandler(null);
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

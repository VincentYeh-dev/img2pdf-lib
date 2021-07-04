package org.vincentyeh.IMG2PDF.commandline.handler;

import org.fusesource.jansi.Ansi;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ParameterExceptionHandler;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import picocli.CommandLine;

import java.util.ResourceBundle;

import static org.vincentyeh.IMG2PDF.util.PrinterUtils.*;

public class CommandlineParameterHandler implements CommandLine.IParameterExceptionHandler {

    private final ResourceBundle resourceBundle;

    public CommandlineParameterHandler(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    public int handleParseException(CommandLine.ParameterException ex, String[] strings) {
        CommandLine cmd = ex.getCommandLine();
        Handler<String, Exception> handler = getHandler();
        try {
            printColor(handler.handle(ex)+"\n", Ansi.Color.RED);
        } catch (Handler.CantHandleException e) {
            printColor("Can't handle"+"\n", Ansi.Color.RED);
            printStackTrance(ex);
        }

        CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();
        printColorFormat(resourceBundle.getString("handler.exception.parameter.try_help")+"\n", Ansi.Color.RED,spec.qualifiedName());

        return CommandLine.ExitCode.USAGE;
    }

    private Handler<String, Exception> getHandler() {
        return new ParameterExceptionHandler(null);
    }

}

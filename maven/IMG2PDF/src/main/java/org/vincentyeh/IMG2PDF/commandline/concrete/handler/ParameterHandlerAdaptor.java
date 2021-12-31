package org.vincentyeh.IMG2PDF.commandline.concrete.handler;

import org.fusesource.jansi.Ansi;
import org.vincentyeh.IMG2PDF.handler.framework.ExceptionHandler;
import org.vincentyeh.IMG2PDF.handler.framework.CantHandleException;
import picocli.CommandLine;

import static org.vincentyeh.IMG2PDF.util.PrinterUtils.*;

public class ParameterHandlerAdaptor implements CommandLine.IParameterExceptionHandler {
    private final ExceptionHandler handler;
    public ParameterHandlerAdaptor(ExceptionHandler handler) {
        this.handler = handler;
    }

    @Override
    public int handleParseException(CommandLine.ParameterException ex, String[] strings) {
        CommandLine cmd = ex.getCommandLine();
        try {
            printColor(handler.handle(ex)+"\n", Ansi.Color.RED);
        } catch (CantHandleException e) {
            printColor("Can't handle"+"\n", Ansi.Color.RED);
            printStackTrance(ex);
        }

        CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();
        printColorFormat(handler.getResourceBundle().getString("public.try_help")+"\n", Ansi.Color.RED,spec.qualifiedName());

        return CommandLine.ExitCode.USAGE;
    }

}

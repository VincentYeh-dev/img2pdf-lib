package org.vincentyeh.IMG2PDF.commandline.handler;

import org.fusesource.jansi.Ansi;
import org.vincentyeh.IMG2PDF.commandline.handler.core.ParameterExceptionHandler;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import picocli.CommandLine;


import static org.vincentyeh.IMG2PDF.util.PrinterUtils.*;

public class CommandLineParameterHandlerAdaptor extends ParameterExceptionHandler implements CommandLine.IParameterExceptionHandler {

    public CommandLineParameterHandlerAdaptor() {
        super(null);
    }

    @Override
    public int handleParseException(CommandLine.ParameterException ex, String[] strings) {
        CommandLine cmd = ex.getCommandLine();
        try {
            printColor(super.handle(ex)+"\n", Ansi.Color.RED);
        } catch (Handler.CantHandleException e) {
            printColor("Can't handle"+"\n", Ansi.Color.RED);
            printStackTrance(ex);
        }

        CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();
        printColorFormat(getLocaleString("try_help")+"\n", Ansi.Color.RED,spec.qualifiedName());

        return CommandLine.ExitCode.USAGE;
    }

}

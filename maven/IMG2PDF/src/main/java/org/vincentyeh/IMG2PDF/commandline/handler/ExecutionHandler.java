package org.vincentyeh.IMG2PDF.commandline.handler;

import org.vincentyeh.IMG2PDF.commandline.handler.execution.ConvertHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.execution.CreateHandler;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.pattern.HandlerRegister;
import picocli.CommandLine;

import java.io.PrintWriter;

public class ExecutionHandler implements CommandLine.IExecutionExceptionHandler {
    @Override
    public int handleExecutionException(Exception e, CommandLine commandLine, CommandLine.ParseResult parseResult) throws Exception {
        HandleCondition condition = new HandleCondition(commandLine.getCommand().getClass(), e);
        HandlerRegister<String,HandleCondition> register=new HandlerRegister<>();
        register.registerHandler(CreateHandler.class);
        register.registerHandler(ConvertHandler.class);

        Handler<String, HandleCondition> handler = register.getHandler();

        printErrorText(commandLine,handler.handle(condition));
        printErrorText(commandLine, "BBB");

        return -9;
    }


    private void printErrorText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(cmd.getColorScheme().errorText(message)); // bold red
    }

    private void printText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(message); // bold red
    }


    public static class HandleCondition {
        private final Class<?> clazz;
        private final Exception exception;

        private HandleCondition(Class<?> clazz, Exception exception) {
            this.clazz = clazz;
            this.exception = exception;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public Exception getException() {
            return exception;
        }
    }

}

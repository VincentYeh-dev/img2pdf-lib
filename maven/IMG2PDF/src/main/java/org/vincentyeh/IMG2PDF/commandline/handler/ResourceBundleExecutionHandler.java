package org.vincentyeh.IMG2PDF.commandline.handler;

import org.vincentyeh.IMG2PDF.commandline.handler.execution.ConvertHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.execution.CreateHandler;
import org.vincentyeh.IMG2PDF.commandline.handler.execution.ResourceBundleHandler;
import org.vincentyeh.IMG2PDF.pattern.Handler;
import org.vincentyeh.IMG2PDF.pattern.HandlerRegister;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.util.ResourceBundle;

public class ResourceBundleExecutionHandler implements CommandLine.IExecutionExceptionHandler {

    public ResourceBundleExecutionHandler(ResourceBundle resourceBundle) {
        ResourceBundleHandler.setResourceBundle(resourceBundle);
    }

    @Override
    public int handleExecutionException(Exception e, CommandLine cmd, CommandLine.ParseResult parseResult) {
        HandlerRegister<ResourceBundleHandler<String, HandleCondition>> register = new HandlerRegister<>();
        register.registerHandler(ConvertHandler.class);
        register.registerHandler(CreateHandler.class);
        String msg;
        try {
            msg = register.getHandler().handle(new HandleCondition(cmd.getCommand().getClass(), e));
        }catch (Handler.CantHandleException ex){
            msg=e.getMessage();
            printErrorText(cmd,"Can't handle");
        }

        printErrorText(cmd, msg);
        printErrorText(cmd, "BBB");
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

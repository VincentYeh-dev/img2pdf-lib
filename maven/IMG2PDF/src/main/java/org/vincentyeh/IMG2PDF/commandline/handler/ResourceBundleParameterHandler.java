package org.vincentyeh.IMG2PDF.commandline.handler;

import picocli.CommandLine;

import java.io.PrintWriter;
import java.util.ResourceBundle;

public class ResourceBundleParameterHandler implements CommandLine.IParameterExceptionHandler {
    private final ResourceBundle resourceBundle;

    public ResourceBundleParameterHandler(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    public int handleParseException(CommandLine.ParameterException ex, String[] strings) throws Exception {
        CommandLine cmd = ex.getCommandLine();
        handleSpecifiedException(cmd,ex);
        printText(cmd,cmd.getHelp().fullSynopsis());
        CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();
        printText(cmd,String.format(getLocaleResource("try_help"), spec.qualifiedName()));

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : spec.exitCodeOnInvalidInput();
    }

    private void handleSpecifiedException(CommandLine cmd, CommandLine.ParameterException ex) {

        if (ex instanceof CommandLine.MissingParameterException) {
            handleMissingParameter(cmd,ex);
        }else if(ex.getCause()!=null&&ex.getCause() instanceof CommandLine.TypeConversionException){
            handleInternalTypeConversion(cmd,ex);
        }else if(ex instanceof CommandLine.UnmatchedArgumentException){
            handleUnmatchedArgument(cmd,ex);
        }else {
            printErrorText(cmd,ex.getMessage());
        }
    }

    private void handleUnmatchedArgument(CommandLine cmd, CommandLine.ParameterException ex) {
        CommandLine.UnmatchedArgumentException exception=(CommandLine.UnmatchedArgumentException) ex;
        printErrorText(cmd,exception.getMessage());
//        TODO:handle error
    }

    private void handleInternalTypeConversion(CommandLine cmd, CommandLine.ParameterException ex) {
        printErrorText(cmd,ex.getMessage());
    }

    private void handleMissingParameter(CommandLine cmd, CommandLine.ParameterException ex) {

        CommandLine.MissingParameterException missingEx = (CommandLine.MissingParameterException) ex;
        for (CommandLine.Model.ArgSpec argSpec : missingEx.getMissing()) {
            String type = getArgStringType(argSpec);
            if (argSpec.isPositional()) {
                printErrorText(cmd,String.format(getLocaleResource("missing_required") + "\n", getPublicResource(type), getPositionalParamSpec(argSpec).paramLabel()));
            } else if (argSpec.isOption()) {
                printErrorText(cmd,String.format(getLocaleResource("missing_required") + "\n", getPublicResource(type), getOptionSpec(argSpec).longestName()));
            }
        }
    }

    private void printErrorText(CommandLine cmd,String message){
        PrintWriter printer=cmd.getErr();
        printer.println(cmd.getColorScheme().errorText(message)); // bold red
    }

    private void printText(CommandLine cmd,String message){
        PrintWriter printer=cmd.getErr();
        printer.println(message); // bold red
    }

    private CommandLine.Model.OptionSpec getOptionSpec(CommandLine.Model.ArgSpec argSpec) {
        return (CommandLine.Model.OptionSpec) argSpec;
    }

    private CommandLine.Model.PositionalParamSpec getPositionalParamSpec(CommandLine.Model.ArgSpec argSpec) {
        return (CommandLine.Model.PositionalParamSpec) argSpec;
    }

    private String getArgStringType(CommandLine.Model.ArgSpec argSpec) {
        if (argSpec.isOption())
            return "option";
        else if (argSpec.isPositional())
            return "parameter";
        return "";
    }

    private String getLocaleResource(String key) {
        return resourceBundle.getString("err.parameter.handler." + key);
    }

    private String getPublicResource(String key) {
        return resourceBundle.getString("public." + key);
    }
}

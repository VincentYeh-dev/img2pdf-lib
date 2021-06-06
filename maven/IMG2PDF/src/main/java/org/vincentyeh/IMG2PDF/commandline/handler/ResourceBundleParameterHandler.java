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
        PrintWriter err = cmd.getErr();

        handleSpecifiedException(ex, err);

//        CommandLine.UnmatchedArgumentException.printSuggestions(ex, err);
        err.print(cmd.getHelp().fullSynopsis());

        CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();
        err.printf(getLocaleResource("try_help"), spec.qualifiedName());

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : spec.exitCodeOnInvalidInput();
    }

    private void handleSpecifiedException(CommandLine.ParameterException ex, PrintWriter err) {
        if (ex instanceof CommandLine.MissingParameterException) {
            handleMissingParameter(ex, err);
        }
    }

    private void handleMissingParameter(CommandLine.ParameterException ex, PrintWriter err) {
        CommandLine.MissingParameterException missingEx = (CommandLine.MissingParameterException) ex;
        for (CommandLine.Model.ArgSpec argSpec : missingEx.getMissing()) {
            String type = getArgStringType(argSpec);
            if (argSpec.isPositional()) {
                err.printf(getLocaleResource("missing_required") + "\n", getPublicResource(type), getPositionalParamSpec(argSpec).paramLabel());
            } else if (argSpec.isOption()) {
                err.printf(getLocaleResource("missing_required") + "\n", getPublicResource(type), getOptionSpec(argSpec).longestName());
            }
        }
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

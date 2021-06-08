package org.vincentyeh.IMG2PDF.commandline.handler;

import picocli.CommandLine;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ResourceBundleParameterHandler implements CommandLine.IParameterExceptionHandler {
    private final ResourceBundle resourceBundle;

    public ResourceBundleParameterHandler(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    public int handleParseException(CommandLine.ParameterException ex, String[] strings) throws Exception {
        CommandLine cmd = ex.getCommandLine();
        handleSpecifiedException(cmd, ex);
        printText(cmd, cmd.getHelp().fullSynopsis());
        CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();
        printText(cmd, String.format(getLocaleResource("try_help"), spec.qualifiedName()));

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : spec.exitCodeOnInvalidInput();
    }

    private void handleSpecifiedException(CommandLine cmd, CommandLine.ParameterException ex) {
        if (ex instanceof CommandLine.MissingParameterException) {
            handleMissingParameter(cmd, ex);
        } else if (ex.getCause() != null && ex.getCause() instanceof CommandLine.TypeConversionException) {
            handleInternalTypeConversion(cmd, ex);
        } else if (ex instanceof CommandLine.UnmatchedArgumentException) {
            handleUnmatchedArgument(cmd, ex);
        } else {
            printErrorText(cmd, ex.getMessage());
        }
    }

    private void handleUnmatchedArgument(CommandLine cmd, CommandLine.ParameterException ex) {
        CommandLine.UnmatchedArgumentException exception = (CommandLine.UnmatchedArgumentException) ex;
        String list = exception.getUnmatched().stream().map(str -> '\'' + str + '\'').collect(Collectors.joining(", "));
        printErrorText(cmd, String.format(getLocaleResource("unmatched_argument.unknown_option"), list));
    }

    private void handleInternalTypeConversion(CommandLine cmd, CommandLine.ParameterException ex) {
        CommandLine.Model.ArgSpec argSpec = ex.getArgSpec();
        Class<?> clazz = argSpec.type();

        CommandLine.Model.OptionSpec optionSpec = (CommandLine.Model.OptionSpec) argSpec;
        if (clazz.isEnum())
            printErrorText(cmd, String.format(getLocaleResource("type_convert.enum"), getResourceArgSpecType(argSpec), optionSpec.longestName(), getEnumConstants(clazz), ex.getValue()));
        else
            printErrorText(cmd, String.format(getLocaleResource("type_convert.normal"), getResourceArgSpecType(argSpec), optionSpec.longestName(), ex.getValue()));

    }

    private void handleMissingParameter(CommandLine cmd, CommandLine.ParameterException ex) {
        CommandLine.MissingParameterException exception = (CommandLine.MissingParameterException) ex;

        List<String> param_list = exception.getMissing().stream()
                .filter(CommandLine.Model.ArgSpec::isPositional)
                .map(argSpec -> {
                    CommandLine.Model.PositionalParamSpec positionalParamSpec = (CommandLine.Model.PositionalParamSpec) argSpec;
                    return positionalParamSpec.paramLabel();
                })
                .collect(Collectors.toList());


        List<String> option_list = exception.getMissing().stream()
                .filter(CommandLine.Model.ArgSpec::isOption)
                .map(argSpec -> {
                    CommandLine.Model.OptionSpec optionSpec = (CommandLine.Model.OptionSpec) argSpec;
                    return optionSpec.longestName();
                })
                .collect(Collectors.toList());

        if (!param_list.isEmpty())
            printErrorText(cmd, String.format(getLocaleResource("missing_required.parameter"), String.join(", ", param_list)));

        if (!option_list.isEmpty())
            printErrorText(cmd, String.format(getLocaleResource("missing_required.option"), String.join(", ", option_list)));

    }

    private void printErrorText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(cmd.getColorScheme().errorText(message)); // bold red
    }

    private void printText(CommandLine cmd, String message) {
        PrintWriter printer = cmd.getErr();
        printer.println(message); // bold red
    }

    private String getEnumConstants(Class<?> clazz) {
        if(!clazz.isEnum())
            throw new IllegalArgumentException("clazz is not a enum.");
        List<String> constants = Arrays.stream(clazz.getEnumConstants()).map(Object::toString).collect(Collectors.toList());
        return String.join(", ", constants);
    }

    private String getResourceArgSpecType(CommandLine.Model.ArgSpec argSpec) {
        if (argSpec.isPositional())
            return getPublicResource("parameter");
        else if (argSpec.isOption())
            return getPublicResource("option");
        return null;
    }

    private String getLocaleResource(String key) {
        return resourceBundle.getString("err.parameter.handler." + key);
    }

    private String getPublicResource(String key) {
        return resourceBundle.getString("public." + key);
    }

}

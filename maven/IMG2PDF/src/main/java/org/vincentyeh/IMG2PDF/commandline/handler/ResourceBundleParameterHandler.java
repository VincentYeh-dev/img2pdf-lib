package org.vincentyeh.IMG2PDF.commandline.handler;

import org.vincentyeh.IMG2PDF.pattern.Handler;
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
    public int handleParseException(CommandLine.ParameterException ex, String[] strings) {
        CommandLine cmd = ex.getCommandLine();
        Handler<String,Exception> handler=new MissingParameterExceptionHandler(new TypeConversionExceptionHandler(new UnmatchedArgumentExceptionHandler(null)));
//        handleSpecifiedException(cmd, ex);
        printErrorText(cmd,handler.handle(ex));

        printText(cmd, cmd.getHelp().fullSynopsis());
        CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();
        printText(cmd, String.format(getLocaleResource("try_help"), spec.qualifiedName()));

        return CommandLine.ExitCode.USAGE;
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
        if (!clazz.isEnum())
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

    private class UnmatchedArgumentExceptionHandler extends Handler<String, Exception> {

        public UnmatchedArgumentExceptionHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data instanceof CommandLine.UnmatchedArgumentException) {
                CommandLine.UnmatchedArgumentException exception = (CommandLine.UnmatchedArgumentException) data;
                String list = exception.getUnmatched().stream().map(str -> '\'' + str + '\'').collect(Collectors.joining(", "));
                return String.format(getLocaleResource("unmatched_argument.unknown_option"), list);
            } else {
                return doNext(data);
            }
        }
    }

    private class MissingParameterExceptionHandler extends Handler<String, Exception> {

        public MissingParameterExceptionHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data instanceof CommandLine.MissingParameterException) {
                CommandLine.MissingParameterException exception = (CommandLine.MissingParameterException) data;
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

                StringBuilder builder = new StringBuilder();
                if (!param_list.isEmpty())
                    builder.append(String.format(getLocaleResource("missing_required.parameter") + '\n', String.join(", ", param_list)));

                if (!option_list.isEmpty())
                    builder.append(String.format(getLocaleResource("missing_required.option") + '\n', String.join(", ", option_list)));

                return builder.toString();
            } else {
                return doNext(data);
            }
        }
    }

    private class TypeConversionExceptionHandler extends Handler<String, Exception> {

        public TypeConversionExceptionHandler(Handler<String, Exception> next) {
            super(next);
        }

        @Override
        public String handle(Exception data) {
            if (data.getCause() != null && data.getCause() instanceof CommandLine.TypeConversionException) {
                CommandLine.ParameterException ex = (CommandLine.ParameterException) data;
                CommandLine.Model.ArgSpec argSpec = ex.getArgSpec();
                Class<?> clazz = argSpec.type();

                CommandLine.Model.OptionSpec optionSpec = (CommandLine.Model.OptionSpec) argSpec;
                if (clazz.isEnum())
                    return String.format(getLocaleResource("type_convert.enum"), getResourceArgSpecType(argSpec), optionSpec.longestName(), getEnumConstants(clazz), ex.getValue());
                else
                    return String.format(getLocaleResource("type_convert.normal"), getResourceArgSpecType(argSpec), optionSpec.longestName(), ex.getValue());
            } else {
                return doNext(data);
            }
        }
    }
}

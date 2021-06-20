package org.vincentyeh.IMG2PDF.commandline.handler.core;


import org.vincentyeh.IMG2PDF.pattern.Handler;
import picocli.CommandLine;

import java.util.List;
import java.util.stream.Collectors;

public class MissingParameterExceptionHandler extends ExceptionHandler{

    public MissingParameterExceptionHandler(Handler<String, Exception> next) {
        super(next,"missing_required");
    }

    @Override
    public String handle(Exception data) throws CantHandleException {
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
                builder.append(String.format(getLocaleString("parameter") + '\n', String.join(", ", param_list)));

            if (!option_list.isEmpty())
                builder.append(String.format(getLocaleString("option") + '\n', String.join(", ", option_list)));

            return builder.toString();
        } else {
            return doNext(data);
        }
    }
}

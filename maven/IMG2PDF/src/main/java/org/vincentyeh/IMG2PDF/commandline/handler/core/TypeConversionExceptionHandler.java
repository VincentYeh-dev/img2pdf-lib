package org.vincentyeh.IMG2PDF.commandline.handler.core;


import org.vincentyeh.IMG2PDF.pattern.Handler;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TypeConversionExceptionHandler extends ExceptionHandler {

    public TypeConversionExceptionHandler(Handler<String, Exception> next) {
        super(next,"type_convert");
    }

    @Override
    public String handle(Exception data) throws CantHandleException {
        if (data.getCause() != null && data.getCause() instanceof CommandLine.TypeConversionException) {
            CommandLine.ParameterException ex = (CommandLine.ParameterException) data;
            CommandLine.Model.ArgSpec argSpec = ex.getArgSpec();
            Class<?> clazz = argSpec.type();

            CommandLine.Model.OptionSpec optionSpec = (CommandLine.Model.OptionSpec) argSpec;
            if (clazz.isEnum())
                return String.format(getLocaleString("enum"), getResourceArgSpecType(argSpec), optionSpec.longestName(), getEnumConstants(clazz), ex.getValue());
            else
                return String.format(getLocaleString("normal"), getResourceArgSpecType(argSpec), optionSpec.longestName(), ex.getValue());
        } else {
            return doNext(data);
        }
    }

    private String getResourceArgSpecType(CommandLine.Model.ArgSpec argSpec) {
        if (argSpec.isPositional())
            return getPublicString("parameter");
        else if (argSpec.isOption())
            return getPublicString("option");
        return null;
    }

    private String getEnumConstants(Class<?> clazz) {
        if (!clazz.isEnum())
            throw new IllegalArgumentException("clazz is not a enum.");
        List<String> constants = Arrays.stream(clazz.getEnumConstants()).map(Object::toString).collect(Collectors.toList());
        return String.join(", ", constants);
    }
}

package org.vincentyeh.IMG2PDF.newcommandline.converter;

import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;
import org.vincentyeh.IMG2PDF.util.BytesSize;
import picocli.CommandLine;

public class ByteSizeConverter implements CommandLine.ITypeConverter<BytesSize> {
    @Override
    public BytesSize convert(String s) throws Exception {
        try {
            return BytesSize.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }
}

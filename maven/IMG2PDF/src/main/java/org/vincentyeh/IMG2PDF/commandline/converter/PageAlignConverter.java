package org.vincentyeh.IMG2PDF.commandline.converter;

import org.vincentyeh.IMG2PDF.pdf.parameter.PageAlign;
import picocli.CommandLine;

public class PageAlignConverter implements CommandLine.ITypeConverter<PageAlign> {
    @Override
    public PageAlign convert(String s) throws Exception {
        try {
            return PageAlign.valueOf(s);
        }catch (Exception e){
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }
}

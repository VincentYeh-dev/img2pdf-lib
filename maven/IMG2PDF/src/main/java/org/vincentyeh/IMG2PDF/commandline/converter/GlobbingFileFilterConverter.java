package org.vincentyeh.IMG2PDF.commandline.converter;

import org.vincentyeh.IMG2PDF.util.file.GlobbingFileFilter;
import picocli.CommandLine;

public class GlobbingFileFilterConverter implements CommandLine.ITypeConverter<GlobbingFileFilter> {

    @Override
    public GlobbingFileFilter convert(String s) throws Exception {
        try {
            return new GlobbingFileFilter(s);
        }catch (Exception e){
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }
}

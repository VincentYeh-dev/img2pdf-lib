package org.vincentyeh.IMG2PDF.commandline.converter;

import org.vincentyeh.IMG2PDF.commandline.converter.core.BasicCheckConverter;
import org.vincentyeh.IMG2PDF.util.file.GlobbingFileFilter;

public class GlobbingFileFilterConverter extends BasicCheckConverter<GlobbingFileFilter> {

    @Override
    protected GlobbingFileFilter doConvert(String s) {
        return new GlobbingFileFilter(s);
    }

}

package org.vincentyeh.IMG2PDF.commandline.converter;

import org.vincentyeh.IMG2PDF.commandline.converter.core.BasicCheckConverter;
import org.vincentyeh.IMG2PDF.parameter.PageAlign;

public class PageAlignConverter extends BasicCheckConverter<PageAlign> {
    @Override
    protected PageAlign doConvert(String s) {
        return PageAlign.valueOf(s);
    }
}

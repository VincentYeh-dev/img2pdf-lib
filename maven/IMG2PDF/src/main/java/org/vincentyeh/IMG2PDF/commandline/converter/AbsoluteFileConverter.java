package org.vincentyeh.IMG2PDF.commandline.converter;

import org.vincentyeh.IMG2PDF.commandline.converter.core.BasicCheckConverter;

import java.io.File;

public class AbsoluteFileConverter extends BasicCheckConverter<File> {
    @Override
    protected File doConvert(String s) {
        return new File(s).getAbsoluteFile();
    }

}

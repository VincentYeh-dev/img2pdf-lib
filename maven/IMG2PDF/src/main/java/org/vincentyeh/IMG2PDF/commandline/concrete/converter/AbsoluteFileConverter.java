package org.vincentyeh.IMG2PDF.commandline.concrete.converter;

import org.vincentyeh.IMG2PDF.commandline.framework.converter.BasicCheckConverter;

import java.io.File;

public class AbsoluteFileConverter extends BasicCheckConverter<File> {
    @Override
    protected File doConvert(String s) {
        return new File(s).getAbsoluteFile();
    }

}

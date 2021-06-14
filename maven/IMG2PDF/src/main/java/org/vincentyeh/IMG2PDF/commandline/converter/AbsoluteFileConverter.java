package org.vincentyeh.IMG2PDF.commandline.converter;

import picocli.CommandLine;

import java.io.File;

public class AbsoluteFileConverter implements CommandLine.ITypeConverter<File> {
    @Override
    public File convert(String s) throws Exception {
        return new File(s).getAbsoluteFile();
    }
}

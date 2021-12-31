package org.vincentyeh.IMG2PDF.commandline.converter;

import org.vincentyeh.IMG2PDF.commandline.converter.core.BasicCheckConverter;
import org.vincentyeh.IMG2PDF.util.file.GlobbingFileFilter;

import java.io.FileFilter;

public class FileFilterConverter extends BasicCheckConverter<FileFilter> {

    @Override
    protected FileFilter doConvert(String s) {
        return new GlobbingFileFilter(s);
    }

}

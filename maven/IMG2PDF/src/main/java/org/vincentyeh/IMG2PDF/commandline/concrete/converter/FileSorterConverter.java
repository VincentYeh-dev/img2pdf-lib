package org.vincentyeh.IMG2PDF.commandline.concrete.converter;

import org.vincentyeh.IMG2PDF.commandline.framework.converter.BasicCheckConverter;
import org.vincentyeh.IMG2PDF.util.file.FileSorter;

public class FileSorterConverter extends BasicCheckConverter<FileSorter> {

    @Override
    protected FileSorter doConvert(String s) {
        String[] split = s.split("-");
        return new FileSorter(FileSorter.Sortby.valueOf(split[0]), FileSorter.Sequence.valueOf(split[1]));
    }
}

package org.vincentyeh.IMG2PDF.lib.concrete.commandline.converter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.vincentyeh.IMG2PDF.commandline.concrete.converter.FileSorterConverter;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileSorterConverterTest {
    @ParameterizedTest
    @NullAndEmptySource
    public void testNullAndEmpty(String input) {
        assertThrows(CommandLine.TypeConversionException.class,
                () -> new FileSorterConverter().convert(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaa","bbbb","cccc","$$$$"})
    public void testIllegal(String input){
        assertThrows(CommandLine.TypeConversionException.class,
                ()-> new FileSorterConverter().convert(input));
    }

}

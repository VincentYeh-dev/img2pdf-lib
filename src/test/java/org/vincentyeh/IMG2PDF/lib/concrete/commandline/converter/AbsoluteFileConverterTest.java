package org.vincentyeh.IMG2PDF.lib.concrete.commandline.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.vincentyeh.IMG2PDF.commandline.concrete.converter.AbsoluteFileConverter;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbsoluteFileConverterTest {
    @ParameterizedTest
    @NullAndEmptySource
    public void testNullAndEmpty(String input) {
        assertThrows(CommandLine.TypeConversionException.class,
                () -> new AbsoluteFileConverter().convert(input));
    }

    @Test
    public void testAbsolute() throws Exception {
        assertTrue(new AbsoluteFileConverter().convert("aaa").isAbsolute());
    }


}

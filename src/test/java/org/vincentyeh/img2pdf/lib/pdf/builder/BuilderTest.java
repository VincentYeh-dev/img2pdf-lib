package org.vincentyeh.img2pdf.lib.pdf.builder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.concrete.builder.PDFBoxBuilder;

public class BuilderTest {
    @Test
    public void TestNullSetting(){
        Assertions.assertThrows(IllegalArgumentException.class,
                ()->new PDFBoxBuilder(null));
    }

}

package org.vincentyeh.img2pdf.lib.image.reader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.ColorSpaceImageReader;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.DirectionImageReader;

public class ImageReaderTest {

    @Test
    public void NullTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ColorSpaceImageReader(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DirectionImageReader(null));
    }
}

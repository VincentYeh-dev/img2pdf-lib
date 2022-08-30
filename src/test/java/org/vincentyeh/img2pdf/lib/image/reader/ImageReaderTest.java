package org.vincentyeh.img2pdf.lib.image.reader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.ColorSpaceImageReader;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.DirectionImageReader;
import org.vincentyeh.img2pdf.lib.image.reader.concrete.ImageReadException;

import java.awt.color.ColorSpace;
import java.io.File;

public class ImageReaderTest {

    @Test
    public void NullTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ColorSpaceImageReader(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DirectionImageReader(null));
    }

    @Test
    public void NotExistsTest() {
        var reader = new ColorSpaceImageReader(ColorSpace.getInstance(ColorSpace.CS_sRGB));
        var file = Mockito.mock(File.class);
        Mockito.when(file.exists()).thenReturn(false);
        Mockito.when(file.getName()).thenReturn("test.txt");
        Assertions.assertThrows(ImageReadException.class, () -> reader.read(file));

        var reader2 = new DirectionImageReader();
        Assertions.assertThrows(ImageReadException.class, () -> reader2.read(file));
    }
}

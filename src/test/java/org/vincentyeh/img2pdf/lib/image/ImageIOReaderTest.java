package org.vincentyeh.img2pdf.lib.image;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;

import java.io.File;

public class ImageIOReaderTest {

    @Test
    public void argumentTest() {
        File file= Mockito.mock(File.class);
        Mockito.when(file.exists()).thenReturn(false);
        ImageReader reader = ImageIOReader.getInstance();

    }

}

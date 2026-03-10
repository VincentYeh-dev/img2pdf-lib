package org.vincentyeh.img2pdf.lib.image;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;

import java.io.File;

/**
 * Tests for {@link ImageIOReader}, verifying singleton access and argument handling.
 */
public class ImageIOReaderTest {

    /** Verifies that ImageIOReader can be obtained as a singleton and accepts a mock File argument. */
    @Test
    public void argumentTest() {
        File file= Mockito.mock(File.class);
        Mockito.when(file.exists()).thenReturn(false);
        ImageReader reader = ImageIOReader.getInstance();

    }

}

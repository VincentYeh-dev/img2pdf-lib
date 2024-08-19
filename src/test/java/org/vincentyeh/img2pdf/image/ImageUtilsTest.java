package org.vincentyeh.img2pdf.image;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;

public class ImageUtilsTest {

    @Test
    public void argumentTest() {
        File file= Mockito.mock(File.class);
        Mockito.when(file.exists()).thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class, () -> ImageUtils.readImage(null, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ImageUtils.readImage(file, null));
    }

}

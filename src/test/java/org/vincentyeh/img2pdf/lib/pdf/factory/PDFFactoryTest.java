package org.vincentyeh.img2pdf.lib.pdf.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePageStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;

import java.io.File;

public class PDFFactoryTest {


    @Test
    public void TestNull() {

        ImageReader imageReader = Mockito.mock(ImageReader.class);
        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImagePageStrategy strategy = Mockito.mock(ImagePageStrategy.class);

        Assertions.assertDoesNotThrow(
                () ->
                        new ImagePDFFactory(null, null,
                                builder, imageReader, strategy, false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () ->
                        new ImagePDFFactory(null, null,
                                null, null, null, false));

    }

    @Test
    public void TestNullImage() {
        var imageReader = Mockito.mock(ImageReader.class);
        Mockito.when(imageReader.read(Mockito.any())).thenReturn(null);
        var builder = Mockito.mock(PDFBuilder.class);
        var strategy = Mockito.mock(ImagePageStrategy.class);

        var factory = new ImagePDFFactory(null, null,
                builder, imageReader, strategy, false);
        Assertions.assertThrows(PDFFactoryException.class,
                () ->
                        factory.start(new File[]{null}, new File(""), null));
    }
}

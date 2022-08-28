package org.vincentyeh.img2pdf.lib.pdf.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePageStrategy;

public class PDFFactoryTest {


    @Test
    public void TestNull(){

        ImageReader imageReader=Mockito.mock(ImageReader.class);
        PDFBuilder builder=Mockito.mock(PDFBuilder.class);
        ImagePageStrategy strategy=Mockito.mock(ImagePageStrategy.class);

        Assertions.assertDoesNotThrow(
                () ->
                        new ImagePDFFactory(null,null,
                                builder,imageReader,strategy,false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () ->
                        new ImagePDFFactory(null,null,
                                null,null,null,false));

    }

}

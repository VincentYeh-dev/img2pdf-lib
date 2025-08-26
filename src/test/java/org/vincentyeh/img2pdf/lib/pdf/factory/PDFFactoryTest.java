package org.vincentyeh.img2pdf.lib.pdf.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.DefaultImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageReadImpl;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;

import java.io.File;

public class PDFFactoryTest {


    @Test
    public void TestNull() {

        ImageReadImpl impl = Mockito.mock(ImageReadImpl.class);
        ImageScalingStrategy strategy = Mockito.mock(ImageScalingStrategy.class);

        Assertions.assertDoesNotThrow(
                () ->
                        new DefaultImagePDFFactory(null, null,
                                impl, strategy, false));

        Assertions.assertDoesNotThrow(
                () ->
                        new DefaultImagePDFFactory(null, null,
                                impl, false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () ->
                        new DefaultImagePDFFactory(null, null,
                                null, null, false));


    }


    @Test
    public void TestNullImage() {
//        ImageReader imageReader = Mockito.mock(ImageReader.class);
        ImageReadImpl impl = Mockito.mock(ImageReadImpl.class);
        Mockito.when(impl.readImage(Mockito.any())).thenReturn(null);

//        Mockito.when(imageReader.read(Mockito.any())).thenReturn(null);
        ImageScalingStrategy strategy = Mockito.mock(ImageScalingStrategy.class);

        DefaultImagePDFFactory factory = new DefaultImagePDFFactory(null, null,
                impl, strategy, false);
        Assertions.assertThrows(PDFFactoryException.class,
                () ->
                        factory.start(-1, new File[]{null}, new File(""), null));
    }


}

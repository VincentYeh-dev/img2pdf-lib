package org.vincentyeh.img2pdf.lib.pdf.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.FactoryImpl;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePageStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;

import java.awt.image.BufferedImage;
import java.io.File;

public class PDFFactoryTest {


    @Test
    public void TestNull() {

        FactoryImpl impl = Mockito.mock(FactoryImpl.class);
        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImagePageStrategy strategy = Mockito.mock(ImagePageStrategy.class);

        Assertions.assertDoesNotThrow(
                () ->
                        new ImagePDFFactory(null, null,
                                impl, builder, strategy, false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () ->
                        new ImagePDFFactory(null, null,
                                null, null, null, false));

    }

    @Test
    public void TestIdeal() {
        FactoryImpl impl = Mockito.mock(FactoryImpl.class);
        Mockito.when(impl.readImage(Mockito.any()))
                .thenReturn(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));

        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImagePageStrategy strategy = Mockito.mock(ImagePageStrategy.class);

        PDFDocumentInfo info = new PDFDocumentInfo();
        info.Title = "";

        ImagePDFFactory factory = new ImagePDFFactory(null,
                new DocumentArgument(null, null, null, info),
                impl, builder, strategy, false);

        Assertions.assertDoesNotThrow(
                () ->
                        factory.start(-1, new File[]{null}, new File(""), null));
    }

    @Test
    public void TestNullImage() {
//        ImageReader imageReader = Mockito.mock(ImageReader.class);
        FactoryImpl impl = Mockito.mock(FactoryImpl.class);
        Mockito.when(impl.readImage(Mockito.any())).thenReturn(null);

//        Mockito.when(imageReader.read(Mockito.any())).thenReturn(null);
        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImagePageStrategy strategy = Mockito.mock(ImagePageStrategy.class);

        ImagePDFFactory factory = new ImagePDFFactory(null, null,
                impl, builder, strategy, false);
        Assertions.assertThrows(PDFFactoryException.class,
                () ->
                        factory.start(-1, new File[]{null}, new File(""), null));
    }

    @Test
    public void TestListener() {
        FactoryImpl impl = Mockito.mock(FactoryImpl.class);
        Mockito.when(impl.readImage(Mockito.any()))
                .thenReturn(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));

        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImagePageStrategy strategy = Mockito.mock(ImagePageStrategy.class);
        ImagePDFFactory.Listener listener = Mockito.mock(ImagePDFFactory.Listener.class);

        ImagePDFFactory factory = new ImagePDFFactory(null, null,
                impl, builder, strategy, false);
        Assertions.assertDoesNotThrow(
                () ->
                        factory.start(-1,new File[]{null}, new File(""), listener));
    }

    @Test
    public void TestOverwrite() {
        FactoryImpl impl = Mockito.mock(FactoryImpl.class);
        Mockito.when(impl.readImage(Mockito.any()))
                .thenReturn(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));

        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImagePageStrategy strategy = Mockito.mock(ImagePageStrategy.class);
        File destination = Mockito.mock(File.class);
        Mockito.when(destination.exists()).thenReturn(true);
        ImagePDFFactory factory = new ImagePDFFactory(null, null,
                impl,builder, strategy, false);
        Assertions.assertThrows(PDFFactoryException.class,
                () ->
                        factory.start(-1,new File[]{null}, destination, null));

        Mockito.when(destination.exists()).thenReturn(false);
        Assertions.assertDoesNotThrow(
                () ->
                        factory.start(-1,new File[]{null}, destination, null));
    }

}

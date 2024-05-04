package org.vincentyeh.img2pdf.pdf.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.pdf.concrete.factory.DefaultImagePDFFactory;
import org.vincentyeh.img2pdf.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.pdf.framework.factory.ImageReadImpl;
import org.vincentyeh.img2pdf.pdf.framework.factory.ImagePDFFactoryListener;
import org.vincentyeh.img2pdf.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.pdf.parameter.PDFDocumentInfo;

import java.awt.image.BufferedImage;
import java.io.File;

public class PDFFactoryTest {


    @Test
    public void TestNull() {

        ImageReadImpl impl = Mockito.mock(ImageReadImpl.class);
        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImageScalingStrategy strategy = Mockito.mock(ImageScalingStrategy.class);

        Assertions.assertDoesNotThrow(
                () ->
                        new DefaultImagePDFFactory(null, null,
                                impl, builder, strategy, false));

        Assertions.assertDoesNotThrow(
                () ->
                        new DefaultImagePDFFactory(null, null,
                                impl, builder, false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () ->
                        new DefaultImagePDFFactory(null, null,
                                null, null, null, false));


    }

    @Test
    public void TestIdeal() {
        ImageReadImpl impl = Mockito.mock(ImageReadImpl.class);
        Mockito.when(impl.readImage(Mockito.any()))
                .thenReturn(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));

        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImageScalingStrategy strategy = Mockito.mock(ImageScalingStrategy.class);

        PDFDocumentInfo info = new PDFDocumentInfo();
        info.Title = "";

        DefaultImagePDFFactory factory = new DefaultImagePDFFactory(null,
                new DocumentArgument(null, null, null, info),
                impl, builder, strategy, false);

        Assertions.assertDoesNotThrow(
                () ->
                        factory.start(-1, new File[]{null}, new File(""), null));
    }

    @Test
    public void TestNullImage() {
//        ImageReader imageReader = Mockito.mock(ImageReader.class);
        ImageReadImpl impl = Mockito.mock(ImageReadImpl.class);
        Mockito.when(impl.readImage(Mockito.any())).thenReturn(null);

//        Mockito.when(imageReader.read(Mockito.any())).thenReturn(null);
        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImageScalingStrategy strategy = Mockito.mock(ImageScalingStrategy.class);

        DefaultImagePDFFactory factory = new DefaultImagePDFFactory(null, null,
                impl, builder, strategy, false);
        Assertions.assertThrows(PDFFactoryException.class,
                () ->
                        factory.start(-1, new File[]{null}, new File(""), null));
    }

    @Test
    public void TestListener() {
        ImageReadImpl impl = Mockito.mock(ImageReadImpl.class);
        Mockito.when(impl.readImage(Mockito.any()))
                .thenReturn(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));

        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImageScalingStrategy strategy = Mockito.mock(ImageScalingStrategy.class);
        ImagePDFFactoryListener listener = Mockito.mock(ImagePDFFactoryListener.class);

        DefaultImagePDFFactory factory = new DefaultImagePDFFactory(null, null,
                impl, builder, strategy, false);
        Assertions.assertDoesNotThrow(
                () ->
                        factory.start(-1, new File[]{null}, new File(""), listener));
    }

    @Test
    public void TestOverwrite() {
        ImageReadImpl impl = Mockito.mock(ImageReadImpl.class);
        Mockito.when(impl.readImage(Mockito.any()))
                .thenReturn(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));

        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImageScalingStrategy strategy = Mockito.mock(ImageScalingStrategy.class);
        File destination = Mockito.mock(File.class);
        Mockito.when(destination.exists()).thenReturn(true);
        DefaultImagePDFFactory factory = new DefaultImagePDFFactory(null, null,
                impl, builder, strategy, false);
        Assertions.assertThrows(PDFFactoryException.class,
                () ->
                        factory.start(-1, new File[]{null}, destination, null));

        Mockito.when(destination.exists()).thenReturn(false);
        Assertions.assertDoesNotThrow(
                () ->
                        factory.start(-1, new File[]{null}, destination, null));
    }

}

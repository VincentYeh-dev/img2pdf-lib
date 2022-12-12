package org.vincentyeh.img2pdf.lib.pdf.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePageStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;

import java.awt.image.BufferedImage;
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
    public void TestIdeal() {
        ImageReader imageReader = Mockito.mock(ImageReader.class);
        Mockito.when(imageReader.read(Mockito.any()))
                .thenReturn(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImagePageStrategy strategy = Mockito.mock(ImagePageStrategy.class);

        PDFDocumentInfo info = new PDFDocumentInfo();
        info.Title="";

        ImagePDFFactory factory = new ImagePDFFactory(null,
                new DocumentArgument(null, null, null, info),
                builder, imageReader, strategy, false);
        Assertions.assertDoesNotThrow(
                () ->
                        factory.start(new File[]{null}, new File(""), null));
    }

    @Test
    public void TestNullImage() {
        ImageReader imageReader = Mockito.mock(ImageReader.class);
        Mockito.when(imageReader.read(Mockito.any())).thenReturn(null);
        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImagePageStrategy strategy = Mockito.mock(ImagePageStrategy.class);

        ImagePDFFactory factory = new ImagePDFFactory(null, null,
                builder, imageReader, strategy, false);
        Assertions.assertThrows(PDFFactoryException.class,
                () ->
                        factory.start(new File[]{null}, new File(""), null));
    }

    @Test
    public void TestListener() {
        ImageReader imageReader = Mockito.mock(ImageReader.class);
        Mockito.when(imageReader.read(Mockito.any()))
                .thenReturn(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImagePageStrategy strategy = Mockito.mock(ImagePageStrategy.class);
        ImagePDFFactory.Listener listener = Mockito.mock(ImagePDFFactory.Listener.class);

        ImagePDFFactory factory = new ImagePDFFactory(null, null,
                builder, imageReader, strategy, false);
        Assertions.assertDoesNotThrow(
                () ->
                        factory.start(new File[]{null}, new File(""), listener));
    }

    @Test
    public void TestOverwrite() {
        ImageReader imageReader = Mockito.mock(ImageReader.class);
        Mockito.when(imageReader.read(Mockito.any()))
                .thenReturn(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
        PDFBuilder builder = Mockito.mock(PDFBuilder.class);
        ImagePageStrategy strategy = Mockito.mock(ImagePageStrategy.class);
        File destination = Mockito.mock(File.class);
        Mockito.when(destination.exists()).thenReturn(true);
        ImagePDFFactory factory = new ImagePDFFactory(null, null,
                builder, imageReader, strategy, false);
        Assertions.assertThrows(PDFFactoryException.class,
                () ->
                        factory.start(new File[]{null}, destination, null));

        Mockito.when(destination.exists()).thenReturn(false);
        Assertions.assertDoesNotThrow(
                () ->
                        factory.start(new File[]{null}, destination, null));
    }

}

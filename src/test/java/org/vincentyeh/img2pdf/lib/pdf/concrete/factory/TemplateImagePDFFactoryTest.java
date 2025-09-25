package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactoryListener;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingResult;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class TemplateImagePDFFactoryTest {

    @Test
    void testStartCreatesPagesAndCallsListener() throws Exception {
        // Arrange
        File f1 = Mockito.mock(File.class);
        Mockito.when(f1.exists()).thenReturn(true);
        Mockito.when(f1.isFile()).thenReturn(true);
        Mockito.when(f1.canRead()).thenReturn(true);
        File f2 = Mockito.mock(File.class);
        Mockito.when(f2.exists()).thenReturn(true);
        Mockito.when(f2.isFile()).thenReturn(true);
        Mockito.when(f2.canRead()).thenReturn(true);

        File[] imageFiles = {f1, f2};

        DocumentArgument docArg = new DocumentArgument();
        PageArgument pageArg = new PageArgument();

        IDocument mockDoc = mock(IDocument.class);
        IPage mockPage = mock(IPage.class);


        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        Mockito.when(mockStrategy.execute(Mockito.any(), Mockito.any())).thenReturn(mock(ImageScalingResult.class));

        BufferedImage mockImage = Mockito.mock(BufferedImage.class);
        Mockito.when(mockImage.getWidth()).thenReturn(100);
        Mockito.when(mockImage.getHeight()).thenReturn(100);

        ImageReader mockReader = mock(ImageReader.class);
        when(mockReader.readImage(Mockito.any(File.class), Mockito.any(ColorType.class))).thenReturn(mockImage);


        ImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, mockReader, 1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return mockDoc;
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return mockPage;
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }

        };

        ImagePDFFactoryListener mockListener = mock(ImagePDFFactoryListener.class);

        // Act
        IDocument result = factory.start(imageFiles, ColorType.sRGB, docArg, pageArg, mockListener);

        // Assert
        Assertions.assertSame(mockDoc, result);
        Mockito.verify(mockDoc, Mockito.times(2)).addPage(mockPage);
        Mockito.verify(mockListener, Mockito.times(2)).onAppend(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void testConstructorThrowsOnNullStrategy() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> new TemplateImagePDFFactory(null, null, 1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return null;
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return null;
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }
        });
    }

    @Test
    void testConstructorThrowsOnInvalidThreadCount() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        ImageReader mockReader = mock(ImageReader.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> new TemplateImagePDFFactory(mockStrategy, mockReader, 0) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return null;
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return null;
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }

        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> new TemplateImagePDFFactory(mockStrategy, mockReader, -1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return null;
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return null;
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }

        });
    }

    @Test
    void testStartThrowsOnNullFiles() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        ImageReader mockReader = mock(ImageReader.class);
        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, mockReader, 1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return mock(IDocument.class);
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return mock(IPage.class);
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }

        };
        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(null, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    @Test
    void testStartThrowsOnNullDocumentArgument() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        ImageReader mockReader = mock(ImageReader.class);
        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, mockReader, 1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return mock(IDocument.class);
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return mock(IPage.class);
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }

        };
        File[] files = new File[]{mock(File.class)};
        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(files, ColorType.sRGB, null, new PageArgument()));
    }

    @Test
    void testStartThrowsOnNullPageArgument() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        ImageReader mockReader = mock(ImageReader.class);
        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, mockReader, 1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return mock(IDocument.class);
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return mock(IPage.class);
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }

        };
        File[] files = new File[]{mock(File.class)};
        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(files, ColorType.sRGB, new DocumentArgument(), null));
    }

    @Test
    void testStartThrowsOnEmptyFiles() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        ImageReader mockReader = mock(ImageReader.class);
        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, mockReader, 1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return mock(IDocument.class);
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return mock(IPage.class);
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }

        };
        File[] files = new File[0];
        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(files, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    @Test
    void testStartThrowsOnUnreadableFile() {
        File f1 = mock(File.class);
        when(f1.exists()).thenReturn(false); // unreadable
        File[] files = new File[]{f1};
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        ImageReader mockReader = mock(ImageReader.class);

        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, mockReader, 1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return mock(IDocument.class);
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return mock(IPage.class);
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }

        };
        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(files, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    @Test
    void testShutdownDoesNotThrow() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);

        ImageReader mockReader = mock(ImageReader.class);
        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, mockReader, 1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return mock(IDocument.class);
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return mock(IPage.class);
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }

        };
        Assertions.assertDoesNotThrow(factory::shutdown);
    }

    @Test
    void testListenerMethodsCalledInOrder() throws Exception {
        File[] files = new File[10];
        for (int i = 0; i < files.length; i++) {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.isFile()).thenReturn(true);
            when(f.canRead()).thenReturn(true);
            files[i] = f;
        }

        DocumentArgument docArg = new DocumentArgument();
        PageArgument pageArg = new PageArgument();
        IDocument mockDoc = mock(IDocument.class);
        IPage mockPage = mock(IPage.class);
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        when(mockStrategy.execute(any(), any())).thenReturn(mock(ImageScalingResult.class));
        ImageReader mockReader = mock(ImageReader.class);
        BufferedImage mockImage = mock(BufferedImage.class);
        when(mockImage.getWidth()).thenReturn(100);
        when(mockImage.getHeight()).thenReturn(100);
        when(mockReader.readImage(any(File.class), any(ColorType.class))).thenReturn(mockImage);


        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, mockReader, 1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return mockDoc;
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return mockPage;
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }

        };

        ImagePDFFactoryListener listener = mock(ImagePDFFactoryListener.class);

        factory.start(files, ColorType.sRGB, docArg, pageArg, listener);

        InOrder inOrder = inOrder(listener);
        inOrder.verify(listener).initializing(eq(files.length));
        inOrder.verify(listener).onConversionComplete();
    }

    @Test
    public void testInvalidFileState() {
        File f1 = mock(File.class);
        when(f1.exists()).thenReturn(false); // does not exist
        File f2 = mock(File.class);
        when(f2.exists()).thenReturn(true);
        when(f2.isFile()).thenReturn(false); // not a file

        File f3 = mock(File.class);
        when(f3.exists()).thenReturn(true);
        when(f3.isFile()).thenReturn(true);
        when(f3.canRead()).thenReturn(false); // cannot be read


        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        ImageReader mockReader = mock(ImageReader.class);

        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, mockReader, 1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return mock(IDocument.class);
            }

            @Override
            protected IPage createPage(int pageNumber, SizeF pageSize) {
                return mock(IPage.class);
            }

            @Override
            protected boolean parallelProcessingSupported() {
                return true;
            }

        };

        PDFFactoryException exception1 = Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{null}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
        Assertions.assertTrue(exception1.getCause() instanceof NullPointerException);

        PDFFactoryException exception2 = Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{f1}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
        Assertions.assertTrue(exception2.getCause() instanceof IOException);

        PDFFactoryException exception3 = Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{f2}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
        Assertions.assertTrue(exception3.getCause() instanceof IOException);

        PDFFactoryException exception4 = Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{f3}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
        Assertions.assertTrue(exception4.getCause() instanceof IOException);


    }
}

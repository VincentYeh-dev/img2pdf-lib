package org.vincentyeh.img2pdf.lib.pdf.factory;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.TemplateImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactoryListener;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.ImageScalingResult;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.awt.image.BufferedImage;
import java.io.File;

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

        File[] imageFiles = {f1,f2};

        DocumentArgument docArg = new DocumentArgument();
        PageArgument pageArg = new PageArgument();

        IDocument mockDoc = mock(IDocument.class);
        IPage mockPage = mock(IPage.class);


        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);

        Mockito.when(mockStrategy.execute(Mockito.any(), Mockito.any())).thenReturn(mock(ImageScalingResult.class));

        ImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, 1) {
            @Override
            protected IDocument createDocument(DocumentArgument argument) {
                return mockDoc;
            }

            @Override
            protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) {
                return mockPage;
            }

            @Override
            protected BufferedImage readImage(File imageFile, ColorType colorType) {
                return new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
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
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TemplateImagePDFFactory(null, 1) {
            @Override protected IDocument createDocument(DocumentArgument argument) { return null; }
            @Override protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) { return null; }
            @Override protected BufferedImage readImage(File imageFile, ColorType colorType) { return null; }
        });
    }

    @Test
    void testConstructorThrowsOnInvalidThreadCount() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TemplateImagePDFFactory(mockStrategy, 0) {
            @Override protected IDocument createDocument(DocumentArgument argument) { return null; }
            @Override protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) { return null; }
            @Override protected BufferedImage readImage(File imageFile, ColorType colorType) { return null; }
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> new TemplateImagePDFFactory(mockStrategy, -1) {
            @Override protected IDocument createDocument(DocumentArgument argument) { return null; }
            @Override protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) { return null; }
            @Override protected BufferedImage readImage(File imageFile, ColorType colorType) { return null; }
        });
    }

    @Test
    void testStartThrowsOnNullFiles() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, 1) {
            @Override protected IDocument createDocument(DocumentArgument argument) { return mock(IDocument.class); }
            @Override protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) { return mock(IPage.class); }
            @Override protected BufferedImage readImage(File imageFile, ColorType colorType) { return new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB); }
        };
        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(null, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    @Test
    void testStartThrowsOnNullDocumentArgument() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, 1) {
            @Override protected IDocument createDocument(DocumentArgument argument) { return mock(IDocument.class); }
            @Override protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) { return mock(IPage.class); }
            @Override protected BufferedImage readImage(File imageFile, ColorType colorType) { return new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB); }
        };
        File[] files = new File[]{mock(File.class)};
        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(files, ColorType.sRGB, null, new PageArgument()));
    }

    @Test
    void testStartThrowsOnNullPageArgument() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, 1) {
            @Override protected IDocument createDocument(DocumentArgument argument) { return mock(IDocument.class); }
            @Override protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) { return mock(IPage.class); }
            @Override protected BufferedImage readImage(File imageFile, ColorType colorType) { return new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB); }
        };
        File[] files = new File[]{mock(File.class)};
        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(files, ColorType.sRGB, new DocumentArgument(), null));
    }

    @Test
    void testStartThrowsOnEmptyFiles() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, 1) {
            @Override protected IDocument createDocument(DocumentArgument argument) { return mock(IDocument.class); }
            @Override protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) { return mock(IPage.class); }
            @Override protected BufferedImage readImage(File imageFile, ColorType colorType) { return new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB); }
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
        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, 1) {
            @Override protected IDocument createDocument(DocumentArgument argument) { return mock(IDocument.class); }
            @Override protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) { return mock(IPage.class); }
            @Override protected BufferedImage readImage(File imageFile, ColorType colorType) { return new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB); }
        };
        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(files, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    @Test
    void testShutdownDoesNotThrow() {
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, 1) {
            @Override protected IDocument createDocument(DocumentArgument argument) { return mock(IDocument.class); }
            @Override protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) { return mock(IPage.class); }
            @Override protected BufferedImage readImage(File imageFile, ColorType colorType) { return new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB); }
        };
        Assertions.assertDoesNotThrow(factory::shutdown);
    }

    @Test
    void testListenerMethodsCalledInOrder() throws Exception {
        File f1 = mock(File.class);
        when(f1.exists()).thenReturn(true);
        when(f1.isFile()).thenReturn(true);
        when(f1.canRead()).thenReturn(true);

        File[] files = new File[]{f1};
        DocumentArgument docArg = new DocumentArgument();
        PageArgument pageArg = new PageArgument();
        IDocument mockDoc = mock(IDocument.class);
        IPage mockPage = mock(IPage.class);
        ImageScalingStrategy mockStrategy = mock(ImageScalingStrategy.class);
        when(mockStrategy.execute(any(), any())).thenReturn(mock(ImageScalingResult.class));

        TemplateImagePDFFactory factory = new TemplateImagePDFFactory(mockStrategy, 1) {
            @Override protected IDocument createDocument(DocumentArgument argument) { return mockDoc; }
            @Override protected IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize) { return mockPage; }
            @Override protected BufferedImage readImage(File imageFile, ColorType colorType) { return new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB); }
        };

        ImagePDFFactoryListener listener = mock(ImagePDFFactoryListener.class);

        factory.start(files, ColorType.sRGB, docArg, pageArg, listener);

        InOrder inOrder = inOrder(listener);
        inOrder.verify(listener).initializing(1);
        inOrder.verify(listener).onAppend(any(), eq(1), eq(1));
        inOrder.verify(listener).onConversionComplete();
    }
}

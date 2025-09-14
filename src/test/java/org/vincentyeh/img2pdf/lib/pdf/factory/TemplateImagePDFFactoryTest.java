package org.vincentyeh.img2pdf.lib.pdf.factory;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.TemplateImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactoryListener;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.ImageScalingResult;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.awt.image.BufferedImage;
import java.io.File;

import static org.mockito.Mockito.mock;

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
}

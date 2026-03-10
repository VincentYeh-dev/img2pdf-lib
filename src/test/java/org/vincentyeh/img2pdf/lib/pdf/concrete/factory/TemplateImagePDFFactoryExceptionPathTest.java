package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.*;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

/**
 * Tests for TemplateImagePDFFactory exception paths.
 * Verifies that PDFFactoryException is properly wrapped when errors occur during processing.
 */
public class TemplateImagePDFFactoryExceptionPathTest {

    private ImageScalingStrategy mockStrategy;
    private ImageReader mockReader;
    private IDocument mockDoc;
    private IPage mockPage;
    private BufferedImage mockImage;

    @BeforeEach
    void setUp() throws IOException {
        mockStrategy = mock(ImageScalingStrategy.class);
        mockReader = mock(ImageReader.class);
        mockDoc = mock(IDocument.class);
        mockPage = mock(IPage.class);
        mockImage = mock(BufferedImage.class);

        when(mockImage.getWidth()).thenReturn(100);
        when(mockImage.getHeight()).thenReturn(100);
        when(mockStrategy.execute(any(), any())).thenReturn(mock(ImageScalingResult.class));
        when(mockReader.readImage(any(File.class), any(ColorType.class))).thenReturn(mockImage);
    }

    private TemplateImagePDFFactory createFactory() {
        return new TemplateImagePDFFactory(mockStrategy, mockReader, 1) {
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
    }

    private File createValidMockFile() {
        File f = mock(File.class);
        when(f.exists()).thenReturn(true);
        when(f.isFile()).thenReturn(true);
        when(f.canRead()).thenReturn(true);
        return f;
    }

    /** Verifies that an IOException from ImageReader is wrapped and rethrown as PDFFactoryException. */
    @Test
    void whenImageReaderThrowsIOException_thenStartThrowsPDFFactoryException() throws IOException {
        when(mockReader.readImage(any(File.class), any(ColorType.class)))
                .thenThrow(new IOException("simulated read failure"));

        File f = createValidMockFile();
        TemplateImagePDFFactory factory = createFactory();

        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{f}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    /** Verifies that a RuntimeException from ImageReader is wrapped and rethrown as PDFFactoryException. */
    @Test
    void whenImageReaderThrowsRuntimeException_thenStartThrowsPDFFactoryException() throws IOException {
        when(mockReader.readImage(any(File.class), any(ColorType.class)))
                .thenThrow(new RuntimeException("simulated runtime failure"));

        File f = createValidMockFile();
        TemplateImagePDFFactory factory = createFactory();

        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{f}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    /** Verifies that a RuntimeException from IDocument.addPage() is wrapped and rethrown as PDFFactoryException. */
    @Test
    void whenAddPageThrowsRuntimeException_thenStartThrowsPDFFactoryException() {
        doThrow(new RuntimeException("simulated addPage failure")).when(mockDoc).addPage(any(IPage.class));

        File f = createValidMockFile();
        TemplateImagePDFFactory factory = createFactory();

        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{f}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    /** Verifies that an IllegalArgumentException from IDocument.addPage() is wrapped and rethrown as PDFFactoryException. */
    @Test
    void whenAddPageThrowsIllegalArgumentException_thenStartThrowsPDFFactoryException() {
        doThrow(new IllegalArgumentException("page already exists")).when(mockDoc).addPage(any(IPage.class));

        File f = createValidMockFile();
        TemplateImagePDFFactory factory = createFactory();

        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{f}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    /** Verifies that a read failure on the second file still results in PDFFactoryException being thrown. */
    @Test
    void whenImageReaderThrowsOnSecondFile_thenStartThrowsPDFFactoryException() throws IOException {
        File f1 = createValidMockFile();
        File f2 = createValidMockFile();

        when(mockReader.readImage(eq(f1), any(ColorType.class))).thenReturn(mockImage);
        when(mockReader.readImage(eq(f2), any(ColorType.class)))
                .thenThrow(new IOException("second file read failure"));

        TemplateImagePDFFactory factory = createFactory();

        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{f1, f2}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    /** Verifies that the original IOException is preserved in the cause chain of the thrown PDFFactoryException. */
    @Test
    void whenImageReaderThrowsIOException_thenCauseIsPreservedInPDFFactoryException() throws IOException {
        IOException cause = new IOException("disk error");
        when(mockReader.readImage(any(File.class), any(ColorType.class))).thenThrow(cause);

        File f = createValidMockFile();
        TemplateImagePDFFactory factory = createFactory();

        PDFFactoryException ex = Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{f}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));

        // PDFFactoryException wraps ExecutionException which wraps IOException
        Throwable root = ex.getCause();
        boolean foundIOException = false;
        while (root != null) {
            if (root instanceof IOException) {
                foundIOException = true;
                break;
            }
            root = root.getCause();
        }
        Assertions.assertTrue(foundIOException, "Root cause should eventually be IOException");
    }

    /** Verifies that a RuntimeException from IPage.render() is wrapped and rethrown as PDFFactoryException. */
    @Test
    void whenPageRenderThrowsRuntimeException_thenStartThrowsPDFFactoryException() {
        doThrow(new RuntimeException("render failure")).when(mockPage).render(any(IDocument.class));

        File f = createValidMockFile();
        TemplateImagePDFFactory factory = createFactory();

        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{f}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    /** Verifies that a RuntimeException from ImageScalingStrategy.execute() is wrapped and rethrown as PDFFactoryException. */
    @Test
    void whenScalingStrategyThrowsRuntimeException_thenStartThrowsPDFFactoryException() {
        when(mockStrategy.execute(any(), any())).thenThrow(new RuntimeException("scaling failure"));

        File f = createValidMockFile();
        TemplateImagePDFFactory factory = createFactory();

        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(new File[]{f}, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }

    /** Verifies that a page-add failure on the first file of a batch still results in PDFFactoryException being thrown. */
    @Test
    void whenAddPageThrowsOnFirstOfMany_thenPDFFactoryExceptionIsThrown() {
        doThrow(new RuntimeException("addPage fail on first")).when(mockDoc).addPage(any(IPage.class));

        File[] files = new File[5];
        for (int i = 0; i < files.length; i++) {
            files[i] = createValidMockFile();
        }

        TemplateImagePDFFactory factory = createFactory();

        Assertions.assertThrows(PDFFactoryException.class, () ->
                factory.start(files, ColorType.sRGB, new DocumentArgument(), new PageArgument()));
    }
}

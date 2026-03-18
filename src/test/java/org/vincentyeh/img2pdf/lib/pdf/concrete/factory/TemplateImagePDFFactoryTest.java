package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

/**
 * Tests for {@link TemplateImagePDFFactory}, covering page creation, listener callbacks,
 * constructor validation, input argument guard clauses, and exception path handling.
 */
public class TemplateImagePDFFactoryTest {

    // =========================================================================
    // Shared fields for exception path tests
    // =========================================================================

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

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Creates a {@link TemplateImagePDFFactory} using the shared mock fields.
     *
     * @return a concrete factory backed by mocks
     */
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

    /**
     * Creates a mock {@link File} that appears to exist, is a regular file, and is readable.
     *
     * @return a valid-looking mock file
     */
    private File createValidMockFile() {
        File f = mock(File.class);
        when(f.exists()).thenReturn(true);
        when(f.isFile()).thenReturn(true);
        when(f.canRead()).thenReturn(true);
        return f;
    }

    // =========================================================================
    // Core behavior tests
    // =========================================================================

    /** Verifies that start() creates a page per image file, adds each to the document, and notifies the listener. */
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

    /** Verifies that the listener receives initializing() before onConversionComplete() for a multi-file batch. */
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

    // =========================================================================
    // Constructor validation tests
    // =========================================================================

    /** Verifies that passing a null scaling strategy to the constructor throws IllegalArgumentException. */
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

    /** Verifies that passing zero or negative thread counts to the constructor throws IllegalArgumentException. */
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

    // =========================================================================
    // start() guard clause tests
    // =========================================================================

    /** Verifies that start() throws PDFFactoryException when the image files array is null. */
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

    /** Verifies that start() throws PDFFactoryException when the DocumentArgument is null. */
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

    /** Verifies that start() throws PDFFactoryException when the PageArgument is null. */
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

    /** Verifies that start() throws PDFFactoryException when the image files array is empty. */
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

    /** Verifies that start() throws PDFFactoryException when a file does not exist on disk. */
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

    /** Verifies that shutdown() completes without throwing any exception. */
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

    /** Verifies that null file element, non-existent file, non-file path, and unreadable file each throw PDFFactoryException with the correct cause. */
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

    // =========================================================================
    // Exception path tests (from TemplateImagePDFFactoryExceptionPathTest)
    // =========================================================================

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

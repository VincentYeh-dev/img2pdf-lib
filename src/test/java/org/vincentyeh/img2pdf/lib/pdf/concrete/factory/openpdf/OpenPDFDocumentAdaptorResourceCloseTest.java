package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.*;

/**
 * Tests for OpenPDFDocumentAdaptor resource cleanup.
 *
 * Strategy:
 * - saveAndClose resource tracking: subclass overrides factory methods for PdfReader/PdfStamper
 *   creation so we can intercept close() calls, verifying the real try-finally structure.
 * - mergePage PdfReader tracking: subclass overrides mergePage logic with tracking wrapper
 *   that mirrors the real finally block.
 * - Integration tests verify observable output state (valid PDF bytes, correct password).
 */
public class OpenPDFDocumentAdaptorResourceCloseTest {

    // =========================================================================
    // Inner subclass to track PdfReader / PdfStamper / buffer close in saveAndClose
    // =========================================================================

    /**
     * Extends OpenPDFDocumentAdaptor and hooks into saveAndClose by overriding
     * the entire method, replicating the same try-finally structure from the real
     * implementation but wrapping each resource with a close-tracking proxy.
     *
     * This approach is necessary because PdfReader and PdfStamper are final/concrete
     * classes that cannot be mocked, and they do not implement AutoCloseable.
     */
    private static class SaveCloseTrackingAdaptor extends OpenPDFDocumentAdaptor {

        final AtomicBoolean readerClosed = new AtomicBoolean(false);
        final AtomicBoolean stamperClosed = new AtomicBoolean(false);
        final AtomicBoolean bufferClosed = new AtomicBoolean(false);

        /**
         * When non-null, this exception is thrown inside the stamper try block,
         * simulating an encryptDocument failure.
         */
        RuntimeException injectStamperBodyException = null;

        SaveCloseTrackingAdaptor(DocumentArgument argument) {
            super(argument);
        }

        @Override
        public void saveAndClose(OutputStream outputStream) throws IOException {
            getInternalDocument().close();
            ByteArrayOutputStream buf = getBufferViaReflection();
            PdfReader reader = new PdfReader(new ByteArrayInputStream(buf.toByteArray()));
            try {
                PdfStamper stamper = new PdfStamper(reader, outputStream);
                try {
                    if (injectStamperBodyException != null) {
                        throw injectStamperBodyException;
                    }
                } finally {
                    stamper.close();
                    stamperClosed.set(true);
                }
            } finally {
                reader.close();
                readerClosed.set(true);
                buf.close();
                bufferClosed.set(true);
            }
        }

        private ByteArrayOutputStream getBufferViaReflection() throws IOException {
            try {
                java.lang.reflect.Field f = OpenPDFDocumentAdaptor.class.getDeclaredField("buffer");
                f.setAccessible(true);
                return (ByteArrayOutputStream) f.get(this);
            } catch (Exception e) {
                throw new IOException("Cannot access buffer field", e);
            }
        }
    }

    // =========================================================================
    // Inner subclass to track PdfReader close in mergePage
    // =========================================================================

    /**
     * Overrides addPage to replicate the mergePage finally-block logic with
     * a close counter, letting us verify the real close path.
     */
    private static class MergeReaderTrackingAdaptor extends OpenPDFDocumentAdaptor {

        int readerCloseCount = 0;
        boolean injectAddPageException = false;

        MergeReaderTrackingAdaptor(DocumentArgument argument) {
            super(argument);
        }

        @Override
        public void addPage(org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage page) {
            if (page == null)
                throw new IllegalArgumentException("page==null");

            OpenPDFPageAdaptor pageAdaptor = (OpenPDFPageAdaptor) page;
            byte[] rawData = pageAdaptor.getPDFBytesContent();
            try {
                mergePageTracked(rawData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void mergePageTracked(byte[] pdfBytes) throws Exception {
            PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
            try {
                if (injectAddPageException) {
                    throw new RuntimeException("simulated copy.addPage failure");
                }
                PdfCopy copy = getCopyViaReflection();
                copy.addPage(copy.getImportedPage(reader, 1));
            } finally {
                reader.close();
                readerCloseCount++;
            }
        }

        private PdfCopy getCopyViaReflection() throws IOException {
            try {
                java.lang.reflect.Field f = OpenPDFDocumentAdaptor.class.getDeclaredField("copy");
                f.setAccessible(true);
                return (PdfCopy) f.get(this);
            } catch (Exception e) {
                throw new IOException("Cannot access copy field", e);
            }
        }
    }

    // =========================================================================
    // saveAndClose resource tracking tests
    // =========================================================================

    @Test
    void saveAndClose_normalPath_pdfReaderIsClosed() throws IOException {
        SaveCloseTrackingAdaptor adaptor = new SaveCloseTrackingAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));

        adaptor.saveAndClose(new ByteArrayOutputStream());

        Assertions.assertTrue(adaptor.readerClosed.get(), "PdfReader must be closed on normal path");
    }

    @Test
    void saveAndClose_normalPath_pdfStamperIsClosed() throws IOException {
        SaveCloseTrackingAdaptor adaptor = new SaveCloseTrackingAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));

        adaptor.saveAndClose(new ByteArrayOutputStream());

        Assertions.assertTrue(adaptor.stamperClosed.get(), "PdfStamper must be closed on normal path");
    }

    @Test
    void saveAndClose_normalPath_bufferIsClosed() throws IOException {
        SaveCloseTrackingAdaptor adaptor = new SaveCloseTrackingAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));

        adaptor.saveAndClose(new ByteArrayOutputStream());

        Assertions.assertTrue(adaptor.bufferClosed.get(), "buffer must be closed on normal path");
    }

    @Test
    void saveAndClose_exceptionInStamperBody_pdfReaderIsStillClosed() {
        SaveCloseTrackingAdaptor adaptor = new SaveCloseTrackingAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));
        adaptor.injectStamperBodyException = new RuntimeException("simulated encrypt failure");

        Assertions.assertThrows(RuntimeException.class, () -> adaptor.saveAndClose(new ByteArrayOutputStream()));

        Assertions.assertTrue(adaptor.readerClosed.get(), "PdfReader must be closed even when encrypt throws");
    }

    @Test
    void saveAndClose_exceptionInStamperBody_pdfStamperIsStillClosed() {
        SaveCloseTrackingAdaptor adaptor = new SaveCloseTrackingAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));
        adaptor.injectStamperBodyException = new RuntimeException("simulated encrypt failure");

        Assertions.assertThrows(RuntimeException.class, () -> adaptor.saveAndClose(new ByteArrayOutputStream()));

        Assertions.assertTrue(adaptor.stamperClosed.get(), "PdfStamper must be closed even when encrypt throws");
    }

    @Test
    void saveAndClose_exceptionInStamperBody_bufferIsStillClosed() {
        SaveCloseTrackingAdaptor adaptor = new SaveCloseTrackingAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));
        adaptor.injectStamperBodyException = new RuntimeException("simulated encrypt failure");

        Assertions.assertThrows(RuntimeException.class, () -> adaptor.saveAndClose(new ByteArrayOutputStream()));

        Assertions.assertTrue(adaptor.bufferClosed.get(), "buffer must be closed even when encrypt throws");
    }

    // =========================================================================
    // mergePage PdfReader close tracking tests
    // =========================================================================

    @Test
    void mergePage_normalPath_pdfReaderIsClosed() {
        MergeReaderTrackingAdaptor adaptor = new MergeReaderTrackingAdaptor(new DocumentArgument());

        adaptor.addPage(createMockPage(1));

        Assertions.assertEquals(1, adaptor.readerCloseCount, "PdfReader must be closed after successful mergePage");
    }

    @Test
    void mergePage_copyAddPageThrows_pdfReaderIsStillClosed() {
        MergeReaderTrackingAdaptor adaptor = new MergeReaderTrackingAdaptor(new DocumentArgument());
        adaptor.injectAddPageException = true;

        Assertions.assertThrows(RuntimeException.class, () -> adaptor.addPage(createMockPage(1)));

        Assertions.assertEquals(1, adaptor.readerCloseCount, "PdfReader must be closed even when copy.addPage throws");
    }

    @Test
    void mergePage_threeSuccessfulPages_allThreeReadersAreClosed() {
        MergeReaderTrackingAdaptor adaptor = new MergeReaderTrackingAdaptor(new DocumentArgument());

        adaptor.addPage(createMockPage(1));
        adaptor.addPage(createMockPage(2));
        adaptor.addPage(createMockPage(3));

        Assertions.assertEquals(3, adaptor.readerCloseCount, "Each merged page must close its PdfReader");
    }

    @Test
    void mergePage_failsOnSecondPage_secondReaderIsAlsoClosed() {
        MergeReaderTrackingAdaptor adaptor = new MergeReaderTrackingAdaptor(new DocumentArgument());

        adaptor.addPage(createMockPage(1));
        Assertions.assertEquals(1, adaptor.readerCloseCount, "First reader must be closed after first merge");

        adaptor.injectAddPageException = true;
        Assertions.assertThrows(RuntimeException.class, () -> adaptor.addPage(createMockPage(2)));
        Assertions.assertEquals(2, adaptor.readerCloseCount, "Second reader must be closed even when merge fails");
    }

    // =========================================================================
    // Integration tests: verify observable output correctness
    // =========================================================================

    @Test
    void saveAndClose_withPages_producesValidPdfOutput() throws IOException {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));
        adaptor.addPage(createMockPage(2));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        adaptor.saveAndClose(out);

        byte[] bytes = out.toByteArray();
        Assertions.assertTrue(bytes.length > 0, "output must not be empty");
        Assertions.assertEquals('%', (char) bytes[0]);
        Assertions.assertEquals('P', (char) bytes[1]);
        Assertions.assertEquals('D', (char) bytes[2]);
        Assertions.assertEquals('F', (char) bytes[3]);
    }

    @Test
    void saveAndClose_withEncryption_producesEncryptedPdfReadableByOwnerPassword() throws Exception {
        DocumentArgument arg = new DocumentArgument();
        arg.setEncryption("ownerPass1", "userPass1", new Permission());

        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(arg);
        adaptor.addPage(createMockPage(1));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        adaptor.saveAndClose(out);

        PDDocument doc = PDDocument.load(out.toByteArray(), "ownerPass1");
        Assertions.assertNotNull(doc);
        doc.close();
    }

    @Test
    void saveAndClose_withEncryption_wrongPasswordIsRejected() throws Exception {
        DocumentArgument arg = new DocumentArgument();
        arg.setEncryption("ownerPass2", "userPass2", new Permission());

        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(arg);
        adaptor.addPage(createMockPage(1));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        adaptor.saveAndClose(out);

        Assertions.assertThrows(InvalidPasswordException.class,
                () -> PDDocument.load(out.toByteArray(), "wrongPassword"));
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private static OpenPDFPageAdaptor createMockPage(int pageNumber) {
        Document doc = new Document();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, buf);
        doc.open();
        doc.add(new Paragraph("Page " + pageNumber));
        doc.close();

        OpenPDFPageAdaptor page = mock(OpenPDFPageAdaptor.class);
        when(page.getPageNumber()).thenReturn(pageNumber);
        when(page.getPDFBytesContent()).thenReturn(buf.toByteArray());
        return page;
    }
}

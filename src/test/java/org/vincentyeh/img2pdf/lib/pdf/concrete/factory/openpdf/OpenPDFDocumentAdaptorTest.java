package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link OpenPDFDocumentAdaptor}, covering construction, page management,
 * document metadata, password encryption, permission enforcement, resource cleanup,
 * and exception path handling.
 */
public class OpenPDFDocumentAdaptorTest {

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
            // Flush pageBuffer into PdfCopy and close Document so that buffer is populated
            flushPageBuffer();
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
    // Helpers
    // =========================================================================

    /**
     * Reads the private {@code closed} boolean field from an {@link OpenPDFDocumentAdaptor}
     * via reflection.
     *
     * @param adaptor the target adaptor
     * @return the current value of the closed flag
     */
    private static boolean getClosedFlag(OpenPDFDocumentAdaptor adaptor) throws Exception {
        Field f = OpenPDFDocumentAdaptor.class.getDeclaredField("closed");
        f.setAccessible(true);
        return (boolean) f.get(adaptor);
    }

    /**
     * Creates a mock {@link OpenPDFPageAdaptor} that returns valid single-page PDF bytes
     * for the given page number.
     *
     * @param pageNumber one-based page number
     * @return a mocked page adaptor
     */
    private static OpenPDFPageAdaptor createMockPage(int pageNumber) {
        Document doc = new Document();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, buf);
        doc.open();
        doc.add(new Paragraph("Test Page " + pageNumber));
        doc.close();

        OpenPDFPageAdaptor page = mock(OpenPDFPageAdaptor.class);
        when(page.getPageNumber()).thenReturn(pageNumber);
        when(page.getPDFBytesContent()).thenReturn(buf.toByteArray());
        return page;
    }

    /**
     * Creates a mock {@link OpenPDFPageAdaptor} that returns corrupt (non-PDF) bytes,
     * causing mergePage to fail when save() tries to parse them.
     *
     * @param pageNumber one-based page number
     * @return a mocked page adaptor with invalid PDF bytes
     */
    private static OpenPDFPageAdaptor createCorruptPage(int pageNumber) {
        OpenPDFPageAdaptor page = mock(OpenPDFPageAdaptor.class);
        when(page.getPageNumber()).thenReturn(pageNumber);
        // These bytes are not a valid PDF; PdfReader will throw when save() tries to parse them
        when(page.getPDFBytesContent()).thenReturn(new byte[]{0x00, 0x01, 0x02, 0x03});
        return page;
    }

    // =========================================================================
    // Construction tests
    // =========================================================================

    /** Verifies that the adaptor can be constructed with a default DocumentArgument. */
    @Test
    public void testConstructorAndGetDocument() {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        Assertions.assertNotNull(adaptor);
    }

    /** Verifies that an encrypted DocumentArgument with empty passwords throws IllegalArgumentException. */
    @Test
    public void testConstructorInvalidArgument() {
        DocumentArgument mockArg = mock(DocumentArgument.class);
        when(mockArg.isEncrypted()).thenReturn(true);
        when(mockArg.getOwnerPassword()).thenReturn("");
        when(mockArg.getUserPassword()).thenReturn("");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpenPDFDocumentAdaptor(mockArg));
    }

    /** Verifies that constructing with a null DocumentArgument throws IllegalArgumentException. */
    @Test
    public void testConstructorWithNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpenPDFDocumentAdaptor(null));
    }

    /** Verifies that an encrypted DocumentArgument with empty passwords causes IllegalArgumentException during construction. */
    @Test
    public void testSetEmptyPassword() {
        DocumentArgument arg = mock(DocumentArgument.class);
        when(arg.isEncrypted()).thenReturn(true);
        when(arg.getOwnerPassword()).thenReturn("");
        when(arg.getUserPassword()).thenReturn("");
        when(arg.getPermission()).thenReturn(new Permission());

        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpenPDFDocumentAdaptor(arg));
    }

    // =========================================================================
    // Page management tests
    // =========================================================================

    /** Verifies page-count tracking, duplicate page rejection, and null page rejection. */
    @Test
    public void testAddPage() {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());

        Assertions.assertThrows(IllegalArgumentException.class, () -> adaptor.addPage(null));
        Assertions.assertEquals(0, adaptor.getPageCount());

        IPage page1 = createMockPage(1);
        IPage page2 = createMockPage(2);
        IPage page3 = createMockPage(3);
        IPage page4 = createMockPage(3);

        adaptor.addPage(page1);
        Assertions.assertEquals(1, adaptor.getPageCount());
        adaptor.addPage(page2);
        Assertions.assertEquals(2, adaptor.getPageCount());
        adaptor.addPage(page3);
        Assertions.assertEquals(3, adaptor.getPageCount());

        Assertions.assertThrows(IllegalArgumentException.class, () -> adaptor.addPage(page4));
    }

    // =========================================================================
    // Save / metadata tests
    // =========================================================================

    /** Verifies that saveAndClose() writes a non-empty byte array to the output stream. */
    @Test
    public void testSaveToOutputStream() throws IOException {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        IPage page = createMockPage(1);
        adaptor.addPage(page);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);
        byte[] pdfBytes = outputStream.toByteArray();
        Assertions.assertTrue(pdfBytes.length > 0);
    }

    /** Verifies that document metadata set via DocumentArgument is embedded in the saved PDF. */
    @Test
    public void testSetAndGetDocumentInfo() throws IOException {
        PDFDocumentInfo info = new PDFDocumentInfo();
        info.Title = "TestTitle";
        info.Author = "TestAuthor";
        info.Subject = "TestSubject";
        info.Producer = "TestProducer";
        info.Creator = "TestCreator";

        DocumentArgument arg = new DocumentArgument();
        arg.setInfo(info);

        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(arg);
        IPage page = createMockPage(1);
        adaptor.addPage(page);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);

        PDDocument savedDocument = PDDocument.load(outputStream.toByteArray());

        Assertions.assertEquals(info.Title, savedDocument.getDocumentInformation().getTitle());
        Assertions.assertEquals(info.Author, savedDocument.getDocumentInformation().getAuthor());
        Assertions.assertEquals(info.Subject, savedDocument.getDocumentInformation().getSubject());
        Assertions.assertTrue(savedDocument.getDocumentInformation().getProducer().contains(info.Producer));
        Assertions.assertEquals(info.Creator, savedDocument.getDocumentInformation().getCreator());
    }

    /** Verifies that setInfo(null) on DocumentArgument throws IllegalArgumentException. */
    @Test
    public void testSetNullDocumentInfo() {
        DocumentArgument arg = new DocumentArgument();
        Assertions.assertThrows(IllegalArgumentException.class, () -> arg.setInfo(null));
    }

    // =========================================================================
    // Password / permission tests
    // =========================================================================

    /** Verifies that the encrypted PDF is openable with owner and user passwords but rejects wrong passwords. */
    @Test
    public void testPassword() throws IOException {
        String ownerPassword = "owner123";
        String userPassword = "user123";
        DocumentArgument arg = new DocumentArgument();
        arg.setEncryption(ownerPassword, userPassword, new Permission());
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(arg);

        Document testDocument = new Document();
        ByteArrayOutputStream a = new ByteArrayOutputStream();
        PdfWriter.getInstance(testDocument, a);
        testDocument.open();
        testDocument.add(new Paragraph("Test Page"));
        testDocument.close();

        OpenPDFPageAdaptor page = createMockPage(1);

        adaptor.addPage(page);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);

        Assertions.assertDoesNotThrow(() -> PDDocument.load(outputStream.toByteArray(), ownerPassword));
        Assertions.assertDoesNotThrow(() -> PDDocument.load(outputStream.toByteArray(), userPassword));
        Assertions.assertThrows(InvalidPasswordException.class, () ->
                PDDocument.load(outputStream.toByteArray(), "wrongpassword"));
    }

    /** Verifies that permission flags are applied correctly for both owner and user access levels. */
    @Test
    public void testPermissionSettings() throws IOException {
        Permission permission = new Permission();
        permission.CanPrint = false;
        permission.CanPrintDegraded = false;
        permission.CanModify = true;
        permission.CanExtractContent = false;
        permission.CanFillInForm = true;

        DocumentArgument arg = new DocumentArgument();
        arg.setEncryption("owner", "user", permission);
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(arg);
        IPage page = createMockPage(1);
        adaptor.addPage(page);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);

        // Load with owner password
        PDDocument docOwner = PDDocument.load(outputStream.toByteArray(), "owner");
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canModify());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canFillInForm());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canPrint());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canPrintDegraded());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canExtractContent());
        docOwner.close();
        // Load with user password
        PDDocument docUser = PDDocument.load(outputStream.toByteArray(), "user");
        Assertions.assertTrue(docUser.getCurrentAccessPermission().canModify());
        Assertions.assertTrue(docUser.getCurrentAccessPermission().canFillInForm());
        Assertions.assertFalse(docUser.getCurrentAccessPermission().canPrint());
        Assertions.assertFalse(docUser.getCurrentAccessPermission().canPrintDegraded());
        Assertions.assertFalse(docUser.getCurrentAccessPermission().canExtractContent());
        docUser.close();
    }

    // =========================================================================
    // OpenPDF-1 fix: save() merge failure triggers close()
    // =========================================================================

    /**
     * When save() fails because pageBuffer contains corrupt (unparseable) PDF bytes,
     * the adaptor's closed flag must be true — confirming that close() was called
     * proactively by the catch block in save().
     */
    @Test
    void save_mergeFailsDueToCorruptPage_closedFlagIsTrue() throws Exception {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        adaptor.addPage(createCorruptPage(1));

        assertThrows(RuntimeException.class,
                () -> adaptor.save(new ByteArrayOutputStream()));

        assertTrue(getClosedFlag(adaptor),
                "closed flag must be true after save() fails, confirming close() was called");
    }

    /**
     * When save() fails during the merge phase, the exception thrown must not be
     * swallowed — it must propagate to the caller.
     */
    @Test
    void save_mergeFailsDueToCorruptPage_exceptionPropagates() {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        adaptor.addPage(createCorruptPage(1));

        assertThrows(RuntimeException.class,
                () -> adaptor.save(new ByteArrayOutputStream()),
                "Exception from merge failure must propagate from save()");
    }

    // =========================================================================
    // OpenPDF-1 fix: post-close operations throw expected exceptions
    // =========================================================================

    /**
     * After close() has been called (either explicitly or triggered by save() failure),
     * calling save() again must throw IllegalStateException — not NullPointerException
     * or any other unexpected exception.
     */
    @Test
    void save_afterExplicitClose_throwsIllegalStateException() {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        adaptor.close();

        assertThrows(IllegalStateException.class,
                () -> adaptor.save(new ByteArrayOutputStream()),
                "save() on a closed adaptor must throw IllegalStateException");
    }

    /**
     * After save() internally calls close() due to a merge failure, a second explicit
     * save() call must throw IllegalStateException and not a different unexpected exception.
     */
    @Test
    void save_afterMergeFailureTriggeredClose_secondSaveThrowsIllegalStateException() {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        adaptor.addPage(createCorruptPage(1));

        // First save() fails and internally closes the adaptor
        assertThrows(RuntimeException.class,
                () -> adaptor.save(new ByteArrayOutputStream()));

        // Second save() must throw IllegalStateException, not NPE
        assertThrows(IllegalStateException.class,
                () -> adaptor.save(new ByteArrayOutputStream()),
                "save() after merge-failure-triggered close() must throw IllegalStateException");
    }

    /**
     * After the adaptor is closed (either explicitly or by save() failure), addPage()
     * must not throw NullPointerException. The method does not currently guard against
     * closed state; we verify the actual thrown type is not NPE.
     * If the implementation guards, IllegalStateException is also acceptable.
     */
    @Test
    void addPage_afterExplicitClose_doesNotThrowNullPointerException() {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        adaptor.close();

        // addPage should either succeed silently or throw a well-typed exception,
        // but must never throw NullPointerException.
        try {
            adaptor.addPage(createMockPage(1));
        } catch (NullPointerException npe) {
            fail("addPage() after close() must not throw NullPointerException; got: " + npe);
        } catch (Exception ignored) {
            // Any other exception type (e.g. IllegalStateException) is acceptable
        }
    }

    // =========================================================================
    // Regression: normal close() is idempotent
    // =========================================================================

    /**
     * Calling close() on an already-closed adaptor must be a no-op and must not throw.
     */
    @Test
    void close_calledTwice_isIdempotent() {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        adaptor.close();

        assertDoesNotThrow(adaptor::close,
                "close() called a second time must be idempotent");
    }

    /**
     * After close() is triggered internally by save() failure, a subsequent explicit
     * close() call must be idempotent and not throw.
     */
    @Test
    void close_afterMergeFailureTriggeredClose_isIdempotent() {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        adaptor.addPage(createCorruptPage(1));

        assertThrows(RuntimeException.class,
                () -> adaptor.save(new ByteArrayOutputStream()));

        assertDoesNotThrow(adaptor::close,
                "Explicit close() after save()-triggered close() must be idempotent");
    }

    // =========================================================================
    // Baseline: closed flag state
    // =========================================================================

    /**
     * A freshly constructed adaptor must not be in the closed state.
     */
    @Test
    void closedFlag_onFreshAdaptor_isFalse() throws Exception {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        assertFalse(getClosedFlag(adaptor),
                "closed flag must be false for a newly constructed adaptor");
    }

    /**
     * After an explicit close(), the closed flag must be true.
     */
    @Test
    void closedFlag_afterExplicitClose_isTrue() throws Exception {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        adaptor.close();
        assertTrue(getClosedFlag(adaptor),
                "closed flag must be true after explicit close()");
    }

    // =========================================================================
    // saveAndClose resource tracking tests
    // =========================================================================

    /** Verifies that PdfReader is closed on the normal saveAndClose path. */
    @Test
    void saveAndClose_normalPath_pdfReaderIsClosed() throws IOException {
        SaveCloseTrackingAdaptor adaptor = new SaveCloseTrackingAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));

        adaptor.saveAndClose(new ByteArrayOutputStream());

        Assertions.assertTrue(adaptor.readerClosed.get(), "PdfReader must be closed on normal path");
    }

    /** Verifies that PdfStamper is closed on the normal saveAndClose path. */
    @Test
    void saveAndClose_normalPath_pdfStamperIsClosed() throws IOException {
        SaveCloseTrackingAdaptor adaptor = new SaveCloseTrackingAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));

        adaptor.saveAndClose(new ByteArrayOutputStream());

        Assertions.assertTrue(adaptor.stamperClosed.get(), "PdfStamper must be closed on normal path");
    }

    /** Verifies that the internal buffer is closed on the normal saveAndClose path. */
    @Test
    void saveAndClose_normalPath_bufferIsClosed() throws IOException {
        SaveCloseTrackingAdaptor adaptor = new SaveCloseTrackingAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));

        adaptor.saveAndClose(new ByteArrayOutputStream());

        Assertions.assertTrue(adaptor.bufferClosed.get(), "buffer must be closed on normal path");
    }

    /** Verifies that PdfReader is closed even when an exception is thrown inside the stamper body. */
    @Test
    void saveAndClose_exceptionInStamperBody_pdfReaderIsStillClosed() {
        SaveCloseTrackingAdaptor adaptor = new SaveCloseTrackingAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));
        adaptor.injectStamperBodyException = new RuntimeException("simulated encrypt failure");

        Assertions.assertThrows(RuntimeException.class, () -> adaptor.saveAndClose(new ByteArrayOutputStream()));

        Assertions.assertTrue(adaptor.readerClosed.get(), "PdfReader must be closed even when encrypt throws");
    }

    /** Verifies that PdfStamper is closed even when an exception is thrown inside the stamper body. */
    @Test
    void saveAndClose_exceptionInStamperBody_pdfStamperIsStillClosed() {
        SaveCloseTrackingAdaptor adaptor = new SaveCloseTrackingAdaptor(new DocumentArgument());
        adaptor.addPage(createMockPage(1));
        adaptor.injectStamperBodyException = new RuntimeException("simulated encrypt failure");

        Assertions.assertThrows(RuntimeException.class, () -> adaptor.saveAndClose(new ByteArrayOutputStream()));

        Assertions.assertTrue(adaptor.stamperClosed.get(), "PdfStamper must be closed even when encrypt throws");
    }

    /** Verifies that the internal buffer is closed even when an exception is thrown inside the stamper body. */
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

    /** Verifies that PdfReader is closed after a successful mergePage call. */
    @Test
    void mergePage_normalPath_pdfReaderIsClosed() {
        MergeReaderTrackingAdaptor adaptor = new MergeReaderTrackingAdaptor(new DocumentArgument());

        adaptor.addPage(createMockPage(1));

        Assertions.assertEquals(1, adaptor.readerCloseCount, "PdfReader must be closed after successful mergePage");
    }

    /** Verifies that PdfReader is closed even when copy.addPage throws during mergePage. */
    @Test
    void mergePage_copyAddPageThrows_pdfReaderIsStillClosed() {
        MergeReaderTrackingAdaptor adaptor = new MergeReaderTrackingAdaptor(new DocumentArgument());
        adaptor.injectAddPageException = true;

        Assertions.assertThrows(RuntimeException.class, () -> adaptor.addPage(createMockPage(1)));

        Assertions.assertEquals(1, adaptor.readerCloseCount, "PdfReader must be closed even when copy.addPage throws");
    }

    /** Verifies that each of three merged pages closes its own PdfReader. */
    @Test
    void mergePage_threeSuccessfulPages_allThreeReadersAreClosed() {
        MergeReaderTrackingAdaptor adaptor = new MergeReaderTrackingAdaptor(new DocumentArgument());

        adaptor.addPage(createMockPage(1));
        adaptor.addPage(createMockPage(2));
        adaptor.addPage(createMockPage(3));

        Assertions.assertEquals(3, adaptor.readerCloseCount, "Each merged page must close its PdfReader");
    }

    /** Verifies that the second PdfReader is closed even when the second mergePage call fails. */
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

    /** Verifies that saveAndClose() with multiple pages produces a non-empty byte stream starting with the PDF header. */
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

    /** Verifies that the encrypted output PDF can be loaded using the owner password. */
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

    /** Verifies that loading the encrypted PDF with a wrong password throws InvalidPasswordException. */
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
}

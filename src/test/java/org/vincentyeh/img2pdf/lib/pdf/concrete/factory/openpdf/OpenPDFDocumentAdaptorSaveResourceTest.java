/**
 * Edge-case tests targeting the OpenPDF-1 fix:
 * when save() fails during the merge phase, close() is called proactively to release
 * PdfCopy resources, and any close() exception is attached via addSuppressed().
 */
package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Verifies that {@link OpenPDFDocumentAdaptor#save(OutputStream)} calls {@link OpenPDFDocumentAdaptor#close()}
 * when the merge phase fails (OpenPDF-1 fix), and that subsequent operations on the
 * already-closed adaptor behave predictably.
 */
class OpenPDFDocumentAdaptorSaveResourceTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

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
        doc.add(new Paragraph("Page " + pageNumber));
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

    // -------------------------------------------------------------------------
    // OpenPDF-1 fix: save() merge failure triggers close()
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // OpenPDF-1 fix: post-close operations throw expected exceptions
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Regression: normal close() is idempotent
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Baseline: closed flag starts as false
    // -------------------------------------------------------------------------

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
}

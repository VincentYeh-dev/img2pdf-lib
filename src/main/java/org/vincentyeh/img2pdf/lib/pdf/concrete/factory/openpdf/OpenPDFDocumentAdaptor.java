package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter that wraps the OpenPDF (librepdf) {@link Document} / {@link PdfCopy} API to
 * implement the {@link IDocument} contract.
 *
 * <p>The design uses a two-phase approach to support both parallel page rendering and
 * optional AES encryption:</p>
 * <ol>
 *   <li><strong>Buffer phase</strong> — each call to {@link #addPage(IPage)} extracts
 *       the raw single-page PDF bytes from an {@link OpenPDFPageAdaptor} and stores them
 *       in an internal {@link ConcurrentHashMap} keyed by page number. This operation is
 *       thread-safe; duplicate page numbers are rejected atomically via
 *       {@link ConcurrentHashMap#putIfAbsent}. No merging into {@link PdfCopy} occurs
 *       at this stage.</li>
 *   <li><strong>Save phase</strong> — {@link #save(java.io.OutputStream)} first drains
 *       the buffer in page-number order, merging each entry into the in-memory
 *       {@link PdfCopy} stream, then closes the {@link PdfCopy} document (first call
 *       only) to flush all accumulated page data into the buffer. A {@link PdfStamper}
 *       is subsequently used to apply AES-128 encryption (if configured) before writing
 *       the final bytes to the caller-provided stream.</li>
 * </ol>
 *
 * <p>{@link #save} only serializes the document without releasing the internal buffer.
 * {@link #close()} releases all resources without saving. Use try-with-resources to ensure
 * proper cleanup:</p>
 * <pre>{@code
 * try (IDocument doc = factory.start(...)) {
 *     doc.save(destination);
 * }
 * }</pre>
 *
 * <p>Page ordering is enforced by the buffer drain order (sorted by key) within
 * {@link #save}; duplicate page numbers are rejected to catch programming errors early.</p>
 */
public class OpenPDFDocumentAdaptor implements IDocument {

    private final Document document;
    private final DocumentArgument docArgument;
    /** Thread-safe buffer that holds raw single-page PDF bytes keyed by page number. */
    private final ConcurrentHashMap<Integer, byte[]> pageBuffer = new ConcurrentHashMap<>();
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final PdfCopy copy;

    // PdfCopy has been closed and buffer has been filled
    private boolean documentFlushed = false;
    // buffer has been released and the whole adaptor has been closed
    private boolean closed = false;

    /**
     * Constructs a new adaptor and opens an in-memory OpenPDF document ready to receive pages.
     *
     * <p>If the argument carries {@link PDFDocumentInfo}, the metadata is applied to the
     * underlying {@link Document} immediately.</p>
     *
     * @param argument document-level settings including optional metadata and encryption;
     *                 must not be {@code null}
     * @throws IllegalArgumentException if {@code argument} is {@code null}, or if encryption
     *                                  is requested but either password is empty
     */
    public OpenPDFDocumentAdaptor(DocumentArgument argument) {
        checkArgument(argument);

        this.docArgument = argument;
        document = new Document();

        copy = new PdfCopy(document, buffer);
        document.open();

        if (argument.hasInfo())
            setPDFDocumentInfo(argument.getInfo(), document);

    }

    /**
     * Validates the {@link DocumentArgument} before use.
     *
     * @param argument the argument to validate
     * @throws IllegalArgumentException if {@code argument} is {@code null}, or if encryption
     *                                  is requested but either the owner or user password is empty
     */
    private void checkArgument(DocumentArgument argument) {
        if (argument == null) {
            throw new IllegalArgumentException("argument==null");
        }
        if (argument.isEncrypted()) {
            if (Objects.requireNonNull(argument.getOwnerPassword()).isEmpty())
                throw new IllegalArgumentException("ownerPassword can not be empty");
            if (Objects.requireNonNull(argument.getUserPassword()).isEmpty())
                throw new IllegalArgumentException("userPassword can not be empty");
        }
    }

    /**
     * Buffers the rendered PDF bytes from the given page for deferred merging.
     *
     * <p>This method is <strong>thread-safe</strong>. The supplied {@code page} must be an
     * {@link OpenPDFPageAdaptor}. Its raw single-page PDF bytes are stored in an internal
     * {@link ConcurrentHashMap} keyed by page number using
     * {@link ConcurrentHashMap#putIfAbsent}, which guarantees atomic duplicate detection.
     * Actual merging into the {@link PdfCopy} stream is deferred until {@link #save} is
     * called.</p>
     *
     * @param page the rendered page to buffer; must not be {@code null} and must be an
     *             instance of {@link OpenPDFPageAdaptor}
     * @throws IllegalArgumentException if {@code page} is {@code null} or if its page
     *                                  number has already been added
     */
    @Override
    public void addPage(IPage page) {
        if (page == null)
            throw new IllegalArgumentException("page==null");

        OpenPDFPageAdaptor adaptor = (OpenPDFPageAdaptor) page;
        byte[] rawData = adaptor.getPDFBytesContent();
        byte[] existing = pageBuffer.putIfAbsent(page.getPageNumber(), rawData);
        if (existing != null)
            throw new IllegalArgumentException("page number " + page.getPageNumber() + " already exists");
    }

    /**
     * Drains {@code pageBuffer} in page-number order and merges each entry into the internal
     * {@link PdfCopy} stream, then closes the {@link PdfCopy} document to flush all data into
     * {@code buffer}.
     *
     * <p>This method is idempotent: if the document has already been flushed, it returns
     * immediately. Exposed as {@code protected} so subclasses used in tests can invoke the
     * flush step before performing their own tracking logic in overridden save methods.</p>
     *
     * @throws IOException if any page merge or document-close operation fails
     */
    protected void flushPageBuffer() throws IOException {
        if (documentFlushed) return;
        try {
            pageBuffer.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        try {
                            mergePage(entry.getValue());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (RuntimeException mergeEx) {
            try {
                close();
            } catch (Exception closeEx) {
                mergeEx.addSuppressed(closeEx);
            }
            throw mergeEx;
        }
        document.close();
        documentFlushed = true;
    }

    /**
     * Copies a single-page PDF (given as raw bytes) into the internal {@link PdfCopy} stream.
     *
     * <p>A temporary {@link PdfReader} is opened around the byte array, the sole page is
     * imported and appended to the copy, and the reader is closed in a {@code finally} block
     * to release native resources.</p>
     *
     * @param pdfBytes the raw bytes of a valid single-page PDF document
     * @throws Exception if the bytes cannot be parsed or the copy operation fails
     */
    private void mergePage(byte[] pdfBytes) throws Exception {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
        try {
            copy.addPage(copy.getImportedPage(reader, 1));
        } finally {
            reader.close();
        }
    }

    /**
     * Serializes the document to the given output stream without releasing resources.
     *
     * <p>On the first call, all buffered pages are drained in page-number order and merged
     * into the internal {@link PdfCopy} stream, after which the {@link PdfCopy} document is
     * closed to flush all accumulated data into the byte buffer. On subsequent calls, the
     * already-flushed buffer is reused directly. If AES encryption is configured, a
     * {@link PdfStamper} applies it before writing to the stream.</p>
     *
     * <p>The provided {@code outputStream} is <em>not</em> closed by this method, nor is
     * the internal buffer released. Call {@link #close()} to release all resources.</p>
     *
     * @param outputStream the stream to which the final PDF bytes are written;
     *                     must not be {@code null}
     * @throws IllegalStateException if this document has already been closed
     * @throws IOException           if writing to the stream or any internal PDF operation fails
     */
    @Override
    public void save(OutputStream outputStream) throws IOException {
        if (closed)
            throw new IllegalStateException("Document has already been closed");

        // Drain pageBuffer, merge pages into PdfCopy, and close Document to flush the buffer
        flushPageBuffer();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(buffer.toByteArray()));
        try {
            PdfStamper stamper = new PdfStamper(reader, outputStream);
            try {
                if (docArgument.isEncrypted()) {
                    encryptDocument(stamper, docArgument);
                }
            } finally {
                stamper.close();
            }
        } finally {
            reader.close();
        }
        // Note: buffer is not closed here; close() is responsible for that
    }

    /**
     * Serializes the document to the given file without releasing resources.
     *
     * <p>Opens a {@link java.io.FileOutputStream} over {@code destination} and delegates to
     * {@link #save(OutputStream)}. Resources held by this document are <em>not</em>
     * released; call {@link #close()} to free them.</p>
     *
     * @param destination the target file; will be created or overwritten; must not be {@code null}
     * @throws IllegalArgumentException if {@code destination} is {@code null}
     * @throws IOException              if the file cannot be opened or if the underlying save
     *                                  operation fails
     */
    @Override
    public void save(File destination) throws IOException {
        if (destination == null)
            throw new IllegalArgumentException("destination==null");

        try (FileOutputStream fos = new FileOutputStream(destination)) {
            save(fos);
        }
    }

    /**
     * Releases all resources held by this adaptor without saving.
     *
     * <p>If the internal {@link PdfCopy} document has not yet been flushed, it is closed
     * here to release OpenPDF's native resources. The internal buffer is also closed.
     * This method is idempotent: multiple calls are safe.</p>
     */
    @Override
    public void close() {
        if (closed)
            return;
        closed = true;
        // If save() was never called, flush the document to release PdfCopy-held resources
        if (!documentFlushed) {
            try {
                document.close();
            } catch (Exception ignored) {
                // Silently ignore resource release failures
            }
            documentFlushed = true;
        }
        try {
            buffer.close();
        } catch (IOException ignored) {
            // ByteArrayOutputStream.close() is a no-op; kept here in case of subclass overrides
        }
    }

    /**
     * Returns the number of pages that have been buffered in this document.
     *
     * @return the current buffered page count; {@code 0} if no pages have been added yet
     */
    @Override
    public int getPageCount() {
        return pageBuffer.size();
    }

    /**
     * Applies the non-null fields of the given {@link PDFDocumentInfo} to the OpenPDF
     * {@link Document} as standard PDF metadata.
     *
     * @param info     the metadata source; fields that are {@code null} are skipped
     * @param document the target OpenPDF document
     */
    private void setPDFDocumentInfo(PDFDocumentInfo info, Document document) {
        if (info.Title != null)
            document.addTitle(info.Title);
        if (info.Author != null)
            document.addAuthor(info.Author);
        if (info.Subject != null)
            document.addSubject(info.Subject);
        if (info.Creator != null)
            document.addCreator(info.Creator);
        if (info.Producer != null)
            document.addProducer(info.Producer);
    }

    /**
     * Applies AES-128 standard encryption to the PDF via the given {@link PdfStamper}.
     *
     * <p>The {@link Permission} flags from the document argument are mapped to OpenPDF
     * {@link PdfWriter} constants and combined into a single bitmask. Both the user
     * password and the owner password must be non-empty; they are passed as raw byte arrays
     * to the stamper.</p>
     *
     * @param pdfStamper       the stamper wrapping the fully assembled PDF
     * @param documentArgument the argument carrying password and permission settings;
     *                         {@link DocumentArgument#isEncrypted()} must return {@code true}
     * @throws IllegalArgumentException if either password is empty
     */
    private void encryptDocument(PdfStamper pdfStamper, DocumentArgument documentArgument) {
        Permission permission = documentArgument.getPermission();

        int permissionFlag = 0;

        permissionFlag |= (permission.CanAssembleDocument) ? PdfWriter.ALLOW_ASSEMBLY : 0;
        permissionFlag |= (permission.CanExtractContent) ? PdfWriter.ALLOW_COPY : 0;
        permissionFlag |= (permission.CanExtractForAccessibility) ? PdfWriter.ALLOW_SCREENREADERS : 0;
        permissionFlag |= (permission.CanFillInForm) ? PdfWriter.ALLOW_FILL_IN : 0;
        permissionFlag |= (permission.CanModify) ? PdfWriter.ALLOW_MODIFY_CONTENTS : 0;
        permissionFlag |= (permission.CanModifyAnnotations) ? PdfWriter.ALLOW_MODIFY_ANNOTATIONS : 0;
        permissionFlag |= (permission.CanPrint) ? PdfWriter.ALLOW_PRINTING : 0;
        permissionFlag |= (permission.CanPrintDegraded) ? PdfWriter.ALLOW_DEGRADED_PRINTING : 0;


        if (documentArgument.isEncrypted()) {
            String userPassword = documentArgument.getUserPassword();
            String ownerPassword = documentArgument.getOwnerPassword();
            if (userPassword.isEmpty() || ownerPassword.isEmpty())
                throw new IllegalArgumentException("Password can not be empty");

            pdfStamper.setEncryption(userPassword.getBytes(), ownerPassword.getBytes(), permissionFlag, PdfWriter.STANDARD_ENCRYPTION_128);
        }
    }

    /**
     * Returns the underlying OpenPDF {@link Document} instance.
     *
     * <p>This accessor is intended for testing and diagnostic purposes only. The document
     * may already be closed after {@link #save(OutputStream)} has been called.</p>
     *
     * @return the internal {@link Document}; never {@code null}
     */
    public Document getInternalDocument() {
        return document;
    }

}

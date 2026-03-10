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
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Adapter that wraps the OpenPDF (librepdf) {@link Document} / {@link PdfCopy} API to
 * implement the {@link IDocument} contract.
 *
 * <p>The design uses a two-phase approach to support both parallel page rendering and
 * optional AES encryption:</p>
 * <ol>
 *   <li><strong>Accumulation phase</strong> — each call to {@link #addPage(IPage)} extracts
 *       the raw single-page PDF bytes from an {@link OpenPDFPageAdaptor} and merges them into
 *       an in-memory {@link PdfCopy} stream backed by a {@link ByteArrayOutputStream}.</li>
 *   <li><strong>Save phase</strong> — {@link #save(java.io.OutputStream)} closes the
 *       {@link PdfCopy} document (first call only), then re-opens the accumulated bytes via a
 *       {@link PdfStamper} to apply AES-128 encryption (if configured) before writing the
 *       final bytes to the caller-provided stream.</li>
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
 * <p>Page ordering is enforced by the caller ({@link TemplateImagePDFFactory}); duplicate
 * page numbers are rejected to catch programming errors early.</p>
 *
 * <p>The {@link #pageMap} is a {@link java.util.Collections#synchronizedSet(java.util.Set)
 * synchronizedSet} to guard against concurrent {@link #addPage(IPage)} calls when pages are
 * rendered in parallel.</p>
 */
public class OpenPDFDocumentAdaptor implements IDocument {

    private final Document document;
    private final DocumentArgument docArgument;
    private final Set<Integer> pageMap = Collections.synchronizedSet(new HashSet<>());
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final PdfCopy copy;

    // PdfCopy 已 close，buffer 已填充
    private boolean documentFlushed = false;
    // buffer 已釋放，整個 adaptor 已關閉
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
     * Merges the rendered page into the in-memory PDF document.
     *
     * <p>The supplied {@code page} must be an {@link OpenPDFPageAdaptor} whose
     * {@link OpenPDFPageAdaptor#render(IDocument)} has already been called. The page's
     * raw PDF bytes are extracted and appended to the internal {@link PdfCopy} stream.
     * Each page number may only appear once; duplicate numbers are rejected immediately.</p>
     *
     * @param page the rendered page to add; must not be {@code null} and must be an
     *             instance of {@link OpenPDFPageAdaptor}
     * @throws IllegalArgumentException if {@code page} is {@code null} or if its page
     *                                  number has already been added
     * @throws RuntimeException         if the underlying OpenPDF merge operation fails
     */
    @Override
    public void addPage(IPage page) {
        if (page == null)
            throw new IllegalArgumentException("page==null");

        for (Integer p : pageMap) {
            if (p.equals(page.getPageNumber()))
                throw new IllegalArgumentException("page number " + page.getPageNumber() + " already exists");
        }
        OpenPDFPageAdaptor pageAdaptor = (OpenPDFPageAdaptor) page;
        byte[] rawData = pageAdaptor.getPDFBytesContent();
        pageMap.add(page.getPageNumber());
        try {
            mergePage(rawData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
     * <p>On the first call, the internal {@link PdfCopy} document is closed to flush all
     * accumulated page data into the buffer. On subsequent calls (e.g., saving to multiple
     * destinations), the buffer is reused directly. If AES encryption is configured, a
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

        // 第一次 save 時關閉 PdfCopy 以確保所有頁面資料已寫入 buffer
        if (!documentFlushed) {
            document.close();
            documentFlushed = true;
        }

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
        // 注意：buffer 不在此處關閉，由 close() 負責
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
        // 若從未 save，需要先 flush document 以釋放 PdfCopy 持有的資源
        if (!documentFlushed) {
            try {
                document.close();
            } catch (Exception ignored) {
                // 釋放資源失敗時靜默忽略
            }
            documentFlushed = true;
        }
        try {
            buffer.close();
        } catch (IOException ignored) {
            // ByteArrayOutputStream.close() 實際上是空操作，此處保留以防子類覆寫
        }
    }

    /**
     * Returns the number of pages that have been successfully added to this document.
     *
     * @return the current page count; {@code 0} if no pages have been added yet
     */
    @Override
    public int getPageCount() {
        return pageMap.size();
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

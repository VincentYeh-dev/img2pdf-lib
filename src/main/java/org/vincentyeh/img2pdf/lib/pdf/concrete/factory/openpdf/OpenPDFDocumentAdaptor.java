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
 *   <li><strong>Save phase</strong> — {@link #saveAndClose(java.io.OutputStream)} closes the
 *       {@link PdfCopy} document, then re-opens the accumulated bytes via a {@link PdfStamper}
 *       to apply AES-128 encryption (if configured) before writing the final bytes to the
 *       caller-provided stream.</li>
 * </ol>
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
        if(argument.isEncrypted()){
            if(Objects.requireNonNull(argument.getOwnerPassword()).isEmpty())
                throw new IllegalArgumentException("ownerPassword can not be empty");
            if(Objects.requireNonNull(argument.getUserPassword()).isEmpty())
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
     * Finalises the document and writes it to the given output stream.
     *
     * <p>The internal {@link PdfCopy} document is closed first, then the accumulated bytes
     * are re-read via a {@link PdfStamper}. If the document argument specifies AES encryption,
     * the stamper applies 128-bit standard encryption with the configured permissions before
     * writing the final output. All intermediate resources (reader, stamper, buffer) are
     * closed in {@code finally} blocks.</p>
     *
     * @param outputStream the stream to which the final PDF bytes are written;
     *                     must not be {@code null}
     * @throws IOException if writing to the stream or any internal PDF operation fails
     */
    @Override
    public void saveAndClose(OutputStream outputStream) throws IOException {
        document.close();
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
            buffer.close();
        }
    }

    /**
     * Finalises the document and writes it to the given file.
     *
     * <p>Opens a {@link java.io.FileOutputStream} over {@code destination} and delegates to
     * {@link #saveAndClose(OutputStream)}.</p>
     *
     * @param destination the target file; will be created or overwritten
     * @throws IOException if the file cannot be opened or if the underlying save operation fails
     */
    @Override
    public void saveAndClose(File destination) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            saveAndClose(fos);
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
     * may already be closed after {@link #saveAndClose(OutputStream)} has been called.</p>
     *
     * @return the internal {@link Document}; never {@code null}
     */
    public Document getInternalDocument() {
        return document;
    }

}

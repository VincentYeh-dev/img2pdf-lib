package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PDFBox-backed implementation of {@link IDocument}.
 *
 * <p>This class acts as an <em>Adapter</em> between the library's document abstraction
 * ({@link IDocument}) and the Apache PDFBox {@link PDDocument} API.</p>
 *
 * <p>Pages are accumulated in a thread-safe buffer via {@link #addPage(IPage)}, which stores
 * each page's {@link PDDocument} in a {@link ConcurrentHashMap} keyed by page number. Actual
 * merging into the underlying {@link PDDocument} is deferred until {@link #save} is called,
 * at which point pages are drained from the buffer in sorted order and appended via
 * {@link PDFMergerUtility}.</p>
 *
 * <p>{@link #save} only serializes the document without releasing resources.
 * {@link #close()} releases the underlying {@link PDDocument} and any remaining buffered
 * {@link PDDocument} instances without saving. Use try-with-resources to ensure proper
 * cleanup:</p>
 * <pre>{@code
 * try (IDocument doc = factory.start(...)) {
 *     doc.save(destination);
 * }
 * }</pre>
 *
 * <p>Optional features applied at save time:</p>
 * <ul>
 *   <li><strong>AES encryption</strong> — enabled when
 *       {@link DocumentArgument#isEncrypted()} returns {@code true}; a 128-bit
 *       {@link StandardProtectionPolicy} is applied using the owner/user passwords
 *       and access permissions from the {@link DocumentArgument}.</li>
 *   <li><strong>Document metadata</strong> — title, author, subject, creator and
 *       producer fields are written when
 *       {@link DocumentArgument#hasInfo()} returns {@code true}.</li>
 * </ul>
 *
 * <p>Memory management for the underlying PDFBox scratch storage is controlled by the
 * {@link MemoryUsageSetting} supplied at construction time. When none is provided the
 * default is {@link MemoryUsageSetting#setupMainMemoryOnly()}.</p>
 *
 * <p>{@link #addPage(IPage)} is thread-safe due to the use of {@link ConcurrentHashMap}.
 * However, {@link #save} and {@link #close} must not be called concurrently with each
 * other or with {@link #addPage(IPage)}.</p>
 */
public class PDFBoxDocumentAdaptor implements IDocument {
    private final PDDocument document;
    /** Thread-safe buffer that holds single-page PDDocuments keyed by page number. */
    private final ConcurrentHashMap<Integer, PDDocument> pageBuffer = new ConcurrentHashMap<>();
    private final DocumentArgument docArgument;
    // Marks whether the document has been closed, used to implement idempotent close()
    private boolean closed = false;


    /**
     * Creates a new document adaptor using main-memory-only scratch storage.
     *
     * @param argument the document configuration (encryption, metadata, etc.);
     *                 must not be {@code null}
     * @throws IllegalArgumentException if {@code argument} is {@code null}
     */
    public PDFBoxDocumentAdaptor(DocumentArgument argument) {
        this(argument, MemoryUsageSetting.setupMainMemoryOnly());
    }

    /**
     * Creates a new document adaptor with the specified memory/storage strategy.
     *
     * @param argument             the document configuration (encryption, metadata, etc.);
     *                             must not be {@code null}
     * @param memoryUsageSetting   controls how PDFBox allocates scratch space for
     *                             large documents (main memory only, temp file only, or
     *                             mixed); must not be {@code null}
     * @throws IllegalArgumentException if either argument is {@code null}
     */
    public PDFBoxDocumentAdaptor(DocumentArgument argument, MemoryUsageSetting memoryUsageSetting) {
        if (argument == null)
            throw new IllegalArgumentException("argument==null");

        if (memoryUsageSetting == null)
            throw new IllegalArgumentException("memoryUsageSetting==null");

        document = new PDDocument(memoryUsageSetting);
        this.docArgument = argument;
    }

    /**
     * Buffers the given page's {@link PDDocument} for deferred merging.
     *
     * <p>This method is <strong>thread-safe</strong>. The single-page document owned by the
     * supplied {@link PDFBoxPageAdaptor} is placed into an internal {@link ConcurrentHashMap}
     * via {@link ConcurrentHashMap#putIfAbsent}, which provides atomic duplicate detection.
     * If a page with the same number already exists in the buffer, the incoming
     * {@code singlePageDoc} is closed before the exception is thrown to prevent resource
     * leaks. Actual merging into the underlying {@link PDDocument} is deferred until
     * {@link #save} is called.</p>
     *
     * @param page the page to buffer; must not be {@code null} and must be a
     *             {@link PDFBoxPageAdaptor}
     * @throws IllegalArgumentException if {@code page} is {@code null} or if a page
     *                                  with the same page number has already been added
     */
    @Override
    public void addPage(IPage page) {
        if (page == null)
            throw new IllegalArgumentException("page==null");

        PDFBoxPageAdaptor p = (PDFBoxPageAdaptor) page;
        PDDocument singlePageDoc = p.getOwnDocument();
        PDDocument existing = pageBuffer.putIfAbsent(page.getPageNumber(), singlePageDoc);
        if (existing != null) {
            try {
                singlePageDoc.close();
            } catch (IOException ignored) {
                // ignore close failure on duplicate
            }
            throw new IllegalArgumentException("page number " + page.getPageNumber() + " already exists");
        }
    }

    /**
     * Serializes this document to the given output stream without releasing resources.
     *
     * <p>Before writing, this method:</p>
     * <ol>
     *   <li>Drains all buffered pages in page-number order, merging each into the underlying
     *       {@link PDDocument} via {@link PDFMergerUtility}. Each buffered
     *       {@link PDDocument} is closed after merging. The buffer is cleared afterwards to
     *       prevent double-processing if {@link #close()} is called later.</li>
     *   <li>Optionally applies 128-bit AES encryption if configured.</li>
     *   <li>Optionally sets document metadata (title, author, etc.) if configured.</li>
     * </ol>
     *
     * <p>The provided {@code outputStream} is <em>not</em> closed by this method.
     * Resources held by this document are also <em>not</em> released; call
     * {@link #close()} to free them.</p>
     *
     * @param outputStream the destination stream; must not be {@code null}
     * @throws IllegalStateException if this document has already been closed
     * @throws IOException           if an I/O error occurs while saving
     */
    @Override
    public void save(OutputStream outputStream) throws IOException {
        if (closed)
            throw new IllegalStateException("Document has already been closed");

        // Drain the page buffer in sorted order before saving;
        // pageBuffer.clear() is guaranteed to run so close() never double-processes entries
        try {
            pageBuffer.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        PDDocument singlePageDoc = entry.getValue();
                        try {
                            new PDFMergerUtility().appendDocument(document, singlePageDoc);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            try { singlePageDoc.close(); } catch (IOException ignored) {}
                        }
                    });
        } finally {
            // Clear buffer to prevent close() from double-processing already-merged documents
            pageBuffer.clear();
        }

        if (docArgument.isEncrypted())
            document.protect(
                    createProtectionPolicy(docArgument.getOwnerPassword(),
                            docArgument.getUserPassword(), convertPermission(docArgument.getPermission())));

        if (docArgument.hasInfo())
            setInfo(docArgument.getInfo());

        document.save(outputStream);
    }

    /**
     * Serializes this document to the given file without releasing resources.
     *
     * <p>Delegates to {@link #save(OutputStream)} after opening a
     * {@link FileOutputStream} for {@code destination}. Resources held by this document
     * are <em>not</em> released; call {@link #close()} to free them.</p>
     *
     * @param destination the output file; must not be {@code null} and must be writable
     *                    if it already exists
     * @throws IllegalArgumentException if {@code destination} is {@code null} or is
     *                                  an existing non-writable file
     * @throws IOException              if an I/O error occurs while saving
     */
    @Override
    public void save(File destination) throws IOException {
        if (destination == null)
            throw new IllegalArgumentException("destination==null");
        if (destination.exists() && !destination.canWrite())
            throw new IllegalArgumentException("destination is not writable");

        try (FileOutputStream fos = new FileOutputStream(destination)) {
            save(fos);
        }
    }

    /**
     * Releases the underlying {@link PDDocument} and any remaining buffered
     * {@link PDDocument} instances without saving.
     *
     * <p>Buffered pages that were not yet merged (i.e., {@link #save} was never called or
     * failed mid-way) are closed individually before clearing the buffer. The main
     * {@link PDDocument} is then closed. This method is idempotent: if already closed,
     * subsequent calls return immediately without effect. Any {@link IOException} is
     * silently swallowed. The instance must not be used after this method returns.</p>
     */
    @Override
    public void close() {
        if (closed)
            return;
        closed = true;
        // Close any buffered PDDocuments that were not yet merged by save()
        for (PDDocument buffered : pageBuffer.values()) {
            try {
                buffered.close();
            } catch (IOException ignored) {
                // Silently ignore to ensure all entries are processed
            }
        }
        pageBuffer.clear();
        try {
            document.close();
        } catch (IOException ignored) {
            // Silently ignore resource release failures to ensure idempotent semantics
        }
    }

    /**
     * Copies metadata fields from the library's {@link PDFDocumentInfo} value object
     * into a PDFBox {@link PDDocumentInformation} and attaches it to the document.
     *
     * @param info the source metadata; must not be {@code null}
     */
    private void setInfo(PDFDocumentInfo info) {
        PDDocumentInformation information = new PDDocumentInformation();
        information.setTitle(info.Title);
        information.setAuthor(info.Author);
        information.setSubject(info.Subject);
        information.setCreator(info.Creator);
        information.setProducer(info.Producer);
        document.setDocumentInformation(information);
    }

    /**
     * Returns the total number of pages: buffered (not yet merged) plus already merged
     * into the underlying {@link PDDocument}.
     *
     * @return the combined page count; zero if no pages have been added yet
     */
    @Override
    public int getPageCount() {
        return pageBuffer.size() + document.getNumberOfPages();
    }

    /**
     * Converts the library's {@link Permission} value object to a PDFBox
     * {@link AccessPermission} instance.
     *
     * @param permission the source permission flags; must not be {@code null}
     * @return a fully populated {@link AccessPermission}; never {@code null}
     */
    private AccessPermission convertPermission(Permission permission) {
        AccessPermission accessPermission = new AccessPermission();
        accessPermission.setCanAssembleDocument(permission.CanAssembleDocument);
        accessPermission.setCanExtractContent(permission.CanExtractContent);
        accessPermission.setCanExtractForAccessibility(permission.CanExtractForAccessibility);
        accessPermission.setCanFillInForm(permission.CanFillInForm);
        accessPermission.setCanModify(permission.CanModify);
        accessPermission.setCanModifyAnnotations(permission.CanModifyAnnotations);
        accessPermission.setCanPrint(permission.CanPrint);
        accessPermission.setCanPrintDegraded(permission.CanPrintDegraded);
        return accessPermission;
    }

    /**
     * Builds a 128-bit AES {@link StandardProtectionPolicy} from the given credentials
     * and access permissions.
     *
     * @param ownerPassword the owner (full-access) password; must not be {@code null}
     * @param userPassword  the user (restricted-access) password; must not be {@code null}
     * @param permission    the access rights granted to the user; must not be {@code null}
     * @return a configured {@link StandardProtectionPolicy}; never {@code null}
     */
    private static StandardProtectionPolicy createProtectionPolicy(String ownerPassword, String userPassword, AccessPermission permission) {
        // Encryption key length; 40 or 128 are valid (PDFBox 2.0 also supports 256)
        int keyLength = 128;
        StandardProtectionPolicy spp = new StandardProtectionPolicy(ownerPassword, userPassword, permission);
        spp.setEncryptionKeyLength(keyLength);
        return spp;
    }

}

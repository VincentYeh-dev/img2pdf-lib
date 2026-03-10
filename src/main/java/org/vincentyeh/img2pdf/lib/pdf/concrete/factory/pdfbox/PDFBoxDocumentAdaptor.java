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
import java.util.HashSet;
import java.util.Set;

/**
 * PDFBox-backed implementation of {@link IDocument}.
 *
 * <p>This class acts as an <em>Adapter</em> between the library's document abstraction
 * ({@link IDocument}) and the Apache PDFBox {@link PDDocument} API. Each call to
 * {@link #addPage(IPage)} merges the single-page document owned by the supplied
 * {@link PDFBoxPageAdaptor} directly into the underlying {@link PDDocument} using
 * {@link PDFMergerUtility}, so pages are available immediately after addition.</p>
 *
 * <p>{@link #save} only serializes the document without releasing resources.
 * {@link #close()} releases the underlying {@link PDDocument} without saving.
 * Use try-with-resources to ensure proper cleanup:</p>
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
 * <p>This class is <strong>not</strong> thread-safe. {@link #addPage(IPage)} and the
 * {@code save}/{@code close} methods must be called from the same thread.</p>
 */
public class PDFBoxDocumentAdaptor implements IDocument {
    private final PDDocument document;
    private final Set<Integer> addedPageNumbers = new HashSet<>();
    private final DocumentArgument docArgument;
    // 標記文件是否已關閉，用於實現冪等的 close()
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
     * Merges the given page into this document by appending the page's own
     * {@link PDDocument} via {@link PDFMergerUtility}.
     *
     * <p>Duplicate page numbers are rejected immediately. The single-page document owned
     * by the supplied {@link PDFBoxPageAdaptor} is closed after the merge regardless of
     * success or failure.</p>
     *
     * @param page the page to add; must not be {@code null} and must be a
     *             {@link PDFBoxPageAdaptor}
     * @throws IllegalArgumentException if {@code page} is {@code null} or if a page
     *                                  with the same page number has already been added
     * @throws RuntimeException         if the merge operation fails
     */
    @Override
    public void addPage(IPage page) {
        if (page == null)
            throw new IllegalArgumentException("page==null");

        if (addedPageNumbers.contains(page.getPageNumber()))
            throw new IllegalArgumentException("page number " + page.getPageNumber() + " already exists");

        PDFBoxPageAdaptor p = (PDFBoxPageAdaptor) page;
        PDDocument singlePageDoc = p.getOwnDocument();
        try {
            new PDFMergerUtility().appendDocument(document, singlePageDoc);
            addedPageNumbers.add(page.getPageNumber());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                singlePageDoc.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Serializes this document to the given output stream without releasing resources.
     *
     * <p>Before writing, this method:</p>
     * <ol>
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
     * Releases the underlying {@link PDDocument} without saving.
     *
     * <p>This method is idempotent: if already closed, subsequent calls return immediately
     * without effect. Any {@link IOException} thrown by {@link PDDocument#close()} is
     * silently swallowed. The instance must not be used after this method returns.</p>
     */
    @Override
    public void close() {
        if (closed)
            return;
        closed = true;
        try {
            document.close();
        } catch (IOException ignored) {
            // 釋放資源失敗時靜默忽略，確保冪等語義
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
     * Returns the number of pages currently in this document.
     *
     * @return the page count; zero if no pages have been added yet
     */
    @Override
    public int getPageCount() {
        return document.getNumberOfPages();
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
        // 加密金鑰長度，可選 40 或 128（PDFBox 2.0 支援 256）
        int keyLength = 128;
        StandardProtectionPolicy spp = new StandardProtectionPolicy(ownerPassword, userPassword, permission);
        spp.setEncryptionKeyLength(keyLength);
        return spp;
    }

}

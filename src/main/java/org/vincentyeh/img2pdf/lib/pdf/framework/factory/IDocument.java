package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an in-memory PDF document that accumulates pages and can be persisted to a
 * file or stream.
 *
 * <p>The recommended usage pattern is try-with-resources:</p>
 * <pre>{@code
 * try (IDocument doc = factory.start(...)) {
 *     doc.save(file);
 * }
 * }</pre>
 *
 * <p>{@link #save(File)} and {@link #save(OutputStream)} only persist the document and do
 * <em>not</em> release resources. {@link #close()} only releases resources without saving;
 * if {@code close()} is called without a prior {@code save()}, the document content will
 * <strong>not</strong> be persisted.</p>
 *
 * <p>Implementations of this interface are not required to be thread-safe.</p>
 */
public interface IDocument extends AutoCloseable {

    /**
     * Appends a page to this document.
     *
     * <p>Pages are added in insertion order and appear in the same order in the saved PDF.
     * This method is the single integration point responsible for committing all page
     * content into the document; implementations perform any necessary rendering or
     * merging internally.</p>
     *
     * @param page the page to append; must not be {@code null}
     */
    void addPage(IPage page);

    /**
     * Serializes the document to the given output stream without releasing resources.
     *
     * <p>The stream is <em>not</em> closed by this method; the caller is responsible for
     * closing it. Resources held by this document are also <em>not</em> released; call
     * {@link #close()} (or use try-with-resources) to free them.</p>
     *
     * @param outputStream the destination stream; must not be {@code null}
     * @throws IOException if an I/O error occurs during serialization
     */
    void save(OutputStream outputStream) throws IOException;

    /**
     * Serializes the document to the given file without releasing resources.
     *
     * <p>If the destination file does not exist it will be created; if it already exists
     * its content will be overwritten. Resources held by this document are <em>not</em>
     * released; call {@link #close()} (or use try-with-resources) to free them.</p>
     *
     * @param destination the output file; must not be {@code null}
     * @throws IOException if an I/O error occurs during serialization or if the file cannot
     *                     be created
     */
    void save(File destination) throws IOException;

    /**
     * Releases all underlying resources held by this document without saving.
     *
     * <p>This method does <em>not</em> persist the document. If the document has not been
     * saved via {@link #save(File)} or {@link #save(OutputStream)} before this call, all
     * accumulated content will be discarded.</p>
     *
     * <p>This method is idempotent: multiple calls are safe and subsequent calls after the
     * first have no effect. The instance must not be used for any operation after
     * {@code close()} returns.</p>
     */
    @Override
    void close();

    /**
     * Returns the number of pages currently added to this document.
     *
     * @return the page count; zero if no pages have been added yet
     */
    int getPageCount();

    /**
     * Serializes the document to the given output stream and releases all underlying
     * resources.
     *
     * <p>Equivalent to calling {@link #save(OutputStream)} followed by {@link #close()}.
     * The stream is <em>not</em> closed by this method.</p>
     *
     * @param outputStream the destination stream; must not be {@code null}
     * @throws IOException if an I/O error occurs during serialization
     * @deprecated Use {@link #save(OutputStream)} with try-with-resources instead:
     *             {@code try (IDocument doc = ...) { doc.save(outputStream); }}
     */
    @Deprecated
    default void saveAndClose(OutputStream outputStream) throws IOException {
        try {
            save(outputStream);
        } finally {
            close();
        }
    }

    /**
     * Serializes the document to the given file and releases all underlying resources.
     *
     * <p>Equivalent to calling {@link #save(File)} followed by {@link #close()}.</p>
     *
     * @param destination the output file; must not be {@code null}
     * @throws IOException if an I/O error occurs during serialization or if the file cannot
     *                     be created
     * @deprecated Use {@link #save(File)} with try-with-resources instead:
     *             {@code try (IDocument doc = ...) { doc.save(destination); }}
     */
    @Deprecated
    default void saveAndClose(File destination) throws IOException {
        try {
            save(destination);
        } finally {
            close();
        }
    }

}

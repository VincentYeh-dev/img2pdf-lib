package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an in-memory PDF document that accumulates pages and can be persisted to a
 * file or stream.
 *
 * <p>The typical usage pattern is:</p>
 * <ol>
 *   <li>Obtain an {@code IDocument} instance from {@link ImagePDFFactory#start}.</li>
 *   <li>Optionally inspect page count via {@link #getPageCount()}.</li>
 *   <li>Persist the document by calling {@link #saveAndClose(File)} or
 *       {@link #saveAndClose(OutputStream)}.</li>
 * </ol>
 *
 * <p>Both {@code saveAndClose} methods flush the underlying PDF resources and close the
 * document; the instance must not be used after either method returns.</p>
 *
 * <p>Implementations of this interface are not required to be thread-safe.</p>
 */
public interface IDocument {

    /**
     * Appends a rendered page to this document.
     *
     * <p>Pages are added in insertion order and appear in the same order in the saved PDF.
     * This method delegates to {@link IPage#render(IDocument)} internally.</p>
     *
     * @param page the page to append; must not be {@code null}
     */
    void addPage(IPage page);

    /**
     * Serializes the document to the given output stream and releases all underlying
     * resources.
     *
     * <p>The stream is <em>not</em> closed by this method; the caller is responsible for
     * closing it after this call returns.</p>
     *
     * @param outputStream the destination stream; must not be {@code null}
     * @throws IOException if an I/O error occurs during serialization
     */
    void saveAndClose(OutputStream outputStream) throws IOException;

    /**
     * Serializes the document to the given file and releases all underlying resources.
     *
     * <p>If the destination file does not exist it will be created; if it already exists
     * its content will be overwritten.</p>
     *
     * @param destination the output file; must not be {@code null}
     * @throws IOException if an I/O error occurs during serialization or if the file cannot
     *                     be created
     */
    void saveAndClose(File destination) throws IOException;

    /**
     * Returns the number of pages currently added to this document.
     *
     * @return the page count; zero if no pages have been added yet
     */
    int getPageCount();

    /**
     * @param outputStream the destination stream; must not be {@code null}
     * @throws IOException if an I/O error occurs during serialization
     * @deprecated Use {@link #saveAndClose(OutputStream)} instead.
     */
    @Deprecated
    default void save(OutputStream outputStream) throws IOException {
        saveAndClose(outputStream);
    }

    /**
     * @param destination the output file; must not be {@code null}
     * @throws IOException if an I/O error occurs during serialization
     * @deprecated Use {@link #saveAndClose(File)} instead.
     */
    @Deprecated
    default void save(File destination) throws IOException {
        saveAndClose(destination);
    }

}

package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.io.File;

/**
 * Top-level contract for converting a sequence of image files into a single PDF document.
 *
 * <p>Implementations orchestrate the full conversion pipeline:</p>
 * <ol>
 *   <li>Read each image file via an {@link org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader}.</li>
 *   <li>Compute page layout using an {@link ImageScalingStrategy}.</li>
 *   <li>Render each page — possibly in parallel — and assemble them into an {@link IDocument}.</li>
 * </ol>
 *
 * <p>Implementations that use an internal thread pool <strong>must</strong> be shut down by
 * calling {@link #shutdown()} after all conversions are complete, otherwise worker threads
 * may prevent the JVM from exiting.</p>
 *
 * <p>Obtain a ready-to-use instance through the {@link org.vincentyeh.img2pdf.lib.Img2Pdf}
 * factory class rather than constructing implementations directly.</p>
 */
public interface ImagePDFFactory {

    /**
     * Converts an ordered array of image files into a single PDF document.
     *
     * <p>Each image becomes one page. The order of pages in the resulting document matches
     * the order of files in {@code imageFiles}.</p>
     *
     * @param imageFiles       ordered array of image files to convert; must not be
     *                         {@code null} or empty
     * @param colorType        target color space applied to every image during reading;
     *                         {@code null} means no color-space conversion
     * @param documentArgument document-level settings such as metadata and AES encryption;
     *                         must not be {@code null}
     * @param pageArgument     page-level settings such as size, alignment, direction, and
     *                         auto-rotation; must not be {@code null}
     * @return an {@link IDocument} whose pages have been rendered and are ready to be saved
     * @throws PDFFactoryException if any image cannot be read or a page cannot be rendered
     */
    IDocument start(File[] imageFiles,
                    ColorType colorType,
                    DocumentArgument documentArgument,
                    PageArgument pageArgument) throws PDFFactoryException;

    /**
     * Converts an ordered array of image files into a single PDF document, reporting
     * progress via the supplied listener.
     *
     * <p>The {@code listener} receives callbacks at the start of the conversion, after each
     * image is appended, and when the entire conversion is complete. Listener methods are
     * called from whichever thread drives the conversion, which may be a worker thread when
     * parallel rendering is active.</p>
     *
     * @param imageFiles       ordered array of image files to convert; must not be
     *                         {@code null} or empty
     * @param colorType        target color space applied to every image during reading;
     *                         {@code null} means no color-space conversion
     * @param documentArgument document-level settings such as metadata and AES encryption;
     *                         must not be {@code null}
     * @param pageArgument     page-level settings such as size, alignment, direction, and
     *                         auto-rotation; must not be {@code null}
     * @param listener         progress callback; must not be {@code null}
     * @return an {@link IDocument} whose pages have been rendered and are ready to be saved
     * @throws PDFFactoryException if any image cannot be read or a page cannot be rendered
     */
    IDocument start(File[] imageFiles,
                    ColorType colorType,
                    DocumentArgument documentArgument,
                    PageArgument pageArgument,
                    ImagePDFFactoryListener listener) throws PDFFactoryException;

    /**
     * Releases the internal thread pool used for parallel page rendering.
     *
     * <p>This method initiates an orderly shutdown: previously submitted tasks are executed
     * before the pool terminates, but no new tasks will be accepted. Callers should invoke
     * this method once all conversions are finished to allow the JVM to exit cleanly.</p>
     */
    void shutdown();
}

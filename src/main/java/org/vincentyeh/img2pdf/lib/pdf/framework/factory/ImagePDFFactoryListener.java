package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import java.io.File;

/**
 * Callback interface for monitoring the progress of an image-to-PDF conversion performed
 * by {@link ImagePDFFactory}.
 *
 * <p>Methods are invoked in the following order during a single {@link ImagePDFFactory#start}
 * call:</p>
 * <ol>
 *   <li>{@link #initializing(int)} — once, before any page is processed.</li>
 *   <li>{@link #onAppend(File, int, int)} — once per image, in processing order.</li>
 *   <li>{@link #onConversionComplete()} — once, after all pages have been rendered.</li>
 * </ol>
 *
 * <p>Listener methods may be called from worker threads when parallel rendering is active;
 * implementations must be thread-safe if they access shared state.</p>
 */
public interface ImagePDFFactoryListener {

    /**
     * Called once at the start of the conversion, before any image is processed.
     *
     * @param length the total number of image files to be converted; always greater than
     *               zero
     */
    void initializing(int length);

    /**
     * Called once after all pages have been rendered and assembled into the document.
     */
    void onConversionComplete();

    /**
     * Called each time a single image file has been successfully appended to the document.
     *
     * @param file          the image file that was just processed; never {@code null}
     * @param appendedCount the number of images appended so far, including this one;
     *                      ranges from {@code 1} to {@code length}
     * @param length        the total number of image files in this conversion batch
     */
    void onAppend(File file, int appendedCount, int length);
}

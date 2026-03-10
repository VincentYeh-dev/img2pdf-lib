package org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception;

/**
 * Unchecked exception thrown by {@link org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory}
 * when an error occurs during the image-to-PDF conversion pipeline.
 *
 * <p>This exception always wraps a lower-level checked exception (e.g., {@link java.io.IOException}
 * from image reading or PDF serialization) as its {@linkplain #getCause() cause}, allowing
 * callers to inspect the root failure without being forced to declare checked exceptions in
 * every call site.</p>
 */
public class PDFFactoryException extends RuntimeException {

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the underlying exception that triggered this failure; must not be
     *              {@code null}
     */
    public PDFFactoryException(Throwable cause) {
        super(cause);
    }
}

package org.vincentyeh.img2pdf.lib.image;

/**
 * Unchecked exception thrown when an image file cannot be read or decoded.
 *
 * <p>This exception is thrown by {@code ImageReader} implementations (e.g.
 * {@code ImageIOReader}) when an I/O error, an unsupported format, or any other
 * image-decoding failure occurs during {@code readImage()}.  Because it extends
 * {@link RuntimeException}, callers are not required to declare or catch it, but
 * should handle it when robustness is needed (e.g. skipping a single corrupt file
 * during a batch conversion).</p>
 *
 * <p>The underlying cause (if any) is always preserved via the {@code cause} chaining
 * constructors so that the original stack trace is not lost.</p>
 */
public class ImageReadingException extends RuntimeException {

    /**
     * Constructs an {@code ImageReadingException} with no detail message or cause.
     */
    public ImageReadingException() {

    }

    /**
     * Constructs an {@code ImageReadingException} with the given detail message.
     *
     * @param message a human-readable description of the failure
     */
    public ImageReadingException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code ImageReadingException} that wraps the given cause.
     * The detail message is set to {@code cause.toString()}.
     *
     * @param e the original exception that caused the image-reading failure; may be {@code null}
     */
    public ImageReadingException(Throwable e) {
        super(e);
    }

    /**
     * Constructs an {@code ImageReadingException} with both a detail message and a cause.
     *
     * @param message a human-readable description of the failure
     * @param e       the original exception that caused the image-reading failure; may be {@code null}
     */
    public ImageReadingException(String message, Throwable e) {
        super(message, e);
    }
}

package org.vincentyeh.img2pdf.lib.image;

public class ImageReadingException extends RuntimeException {
    public ImageReadingException() {

    }

    public ImageReadingException(String message) {
        super(message);
    }

    public ImageReadingException(Throwable e) {
        super(e);
    }

    public ImageReadingException(String message, Throwable e) {
        super(message, e);
    }
}

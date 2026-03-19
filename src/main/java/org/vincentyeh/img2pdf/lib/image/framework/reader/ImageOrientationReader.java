package org.vincentyeh.img2pdf.lib.image.framework.reader;

import java.io.File;
import java.io.IOException;
import java.util.OptionalInt;

/**
 * Strategy interface for reading the EXIF Orientation tag from an image file.
 */
public interface ImageOrientationReader {
    /**
     * Reads the orientation value from the given image file.
     *
     * @param file the image file to inspect; must not be {@code null}
     * @return an {@link OptionalInt} containing the orientation value (1–8),
     *         or {@link OptionalInt#empty()} if no orientation tag is found
     * @throws IOException if an I/O error occurs
     */
    OptionalInt readOrientation(File file) throws IOException;
}

package org.vincentyeh.img2pdf.lib.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Edge-case tests for {@link ImageIOReader}, focusing on resource-leak safety
 * introduced by the try-with-resources fix in {@code readImage(File)},
 * and boundary conditions across all readImage overloads.
 */
public class ImageIOReaderTest {

    @TempDir
    Path tempDir;

    // -------------------------------------------------------------------------
    // readImage(File) — null / missing / corrupt / empty
    // -------------------------------------------------------------------------

    /** Verifies that a null File argument throws IllegalArgumentException before any I/O. */
    @Test
    public void readImageFile_nullFile_throwsIllegalArgumentException() {
        ImageReader reader = ImageIOReader.getInstance();
        assertThrows(IllegalArgumentException.class, () -> reader.readImage((File) null));
    }

    /** Verifies that a non-existent File causes ImageReadingException (wrapping NoSuchFileException). */
    @Test
    public void readImageFile_nonExistentFile_throwsImageReadingException() {
        ImageReader reader = ImageIOReader.getInstance();
        File ghost = new File(tempDir.toFile(), "does_not_exist.jpg");
        assertThrows(ImageReadingException.class, () -> reader.readImage(ghost));
    }

    /**
     * Verifies that an empty file causes ImageReadingException.
     * ImageIO.read() returns null for an empty stream, then readExifOrientation()
     * encounters an EOF while scanning the file, which is wrapped and re-thrown as
     * ImageReadingException. The try-with-resources fix ensures the InputStream is
     * still closed before the exception propagates.
     */
    @Test
    public void readImageFile_emptyFile_throwsImageReadingException() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path emptyFile = tempDir.resolve("empty.jpg");
        Files.createFile(emptyFile);
        assertThrows(ImageReadingException.class, () -> reader.readImage(emptyFile.toFile()));
    }

    /**
     * Verifies that a file containing random garbage bytes does not throw an exception
     * and returns null. The InputStream must be closed even in this case.
     */
    @Test
    public void readImageFile_garbageBytes_returnsNull() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path corruptFile = tempDir.resolve("corrupt.jpg");
        Files.write(corruptFile, new byte[]{0x00, 0x01, 0x02, 0x03, 0x7F, (byte) 0xFF});
        BufferedImage result = reader.readImage(corruptFile.toFile());
        assertNull(result);
    }

    /**
     * Verifies that a file whose name ends with a recognised extension but whose
     * content is truncated (partial JPEG SOI marker only) returns null without exception.
     */
    @Test
    public void readImageFile_truncatedJpegMagicBytes_returnsNull() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path truncated = tempDir.resolve("truncated.jpg");
        // Only the SOI marker, no further valid JPEG data
        Files.write(truncated, new byte[]{(byte) 0xFF, (byte) 0xD8});
        BufferedImage result = reader.readImage(truncated.toFile());
        assertNull(result);
    }

    /**
     * Verifies that a file filled with null bytes (all zeros) does not throw and
     * returns null. Guards against a regression where the InputStream leaked on
     * ImageIO.read() returning null.
     */
    @Test
    public void readImageFile_allZeroBytes_returnsNull() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path zeroFile = tempDir.resolve("zeros.png");
        Files.write(zeroFile, new byte[1024]);
        BufferedImage result = reader.readImage(zeroFile.toFile());
        assertNull(result);
    }

    // -------------------------------------------------------------------------
    // readImage(File, ColorType) — null propagation
    // -------------------------------------------------------------------------

    /** Verifies that a null File propagates IllegalArgumentException through the ColorType overload. */
    @Test
    public void readImageFileColorType_nullFile_throwsIllegalArgumentException() {
        ImageReader reader = ImageIOReader.getInstance();
        assertThrows(IllegalArgumentException.class,
                () -> reader.readImage((File) null, ColorType.sRGB));
    }

    /** Verifies that a non-existent file propagates ImageReadingException through the ColorType overload. */
    @Test
    public void readImageFileColorType_nonExistentFile_throwsImageReadingException() {
        ImageReader reader = ImageIOReader.getInstance();
        File ghost = new File(tempDir.toFile(), "missing.png");
        assertThrows(ImageReadingException.class,
                () -> reader.readImage(ghost, ColorType.sRGB));
    }

    // -------------------------------------------------------------------------
    // readImage(byte[]) — empty / single byte / null-byte arrays
    // -------------------------------------------------------------------------

    /** Verifies that an empty byte array returns null (no registered ImageIO decoder can handle it). */
    @Test
    public void readImageByteArray_emptyArray_returnsNull() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        BufferedImage result = reader.readImage(new byte[0]);
        assertNull(result);
    }

    /** Verifies that a single-byte array with an arbitrary value returns null. */
    @Test
    public void readImageByteArray_singleByte_returnsNull() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        BufferedImage result = reader.readImage(new byte[]{0x42});
        assertNull(result);
    }

    /** Verifies that a large all-zero byte array returns null without throwing. */
    @Test
    public void readImageByteArray_largeZeroArray_returnsNull() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        BufferedImage result = reader.readImage(new byte[4096]);
        assertNull(result);
    }

    // -------------------------------------------------------------------------
    // readImage(InputStream) — empty stream
    // -------------------------------------------------------------------------

    /** Verifies that an already-empty InputStream returns null without throwing. */
    @Test
    public void readImageInputStream_emptyStream_returnsNull() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        ByteArrayInputStream emptyStream = new ByteArrayInputStream(new byte[0]);
        BufferedImage result = reader.readImage(emptyStream);
        assertNull(result);
    }

    // -------------------------------------------------------------------------
    // Singleton contract
    // -------------------------------------------------------------------------

    /** Verifies that repeated getInstance() calls return the exact same instance. */
    @Test
    public void getInstance_calledMultipleTimes_returnsSameInstance() {
        ImageReader first = ImageIOReader.getInstance();
        ImageReader second = ImageIOReader.getInstance();
        assertSame(first, second);
    }
}

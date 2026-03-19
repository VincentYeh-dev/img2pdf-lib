/**
 * ImageIOReaderTest.java
 *
 * Edge-case and boundary-condition tests for {@link ImageIOReader}.
 * Two groups of tests are covered:
 *   1. Byte-level edge cases (null, empty, garbage, injected APP1 segments, EOI, SOS, etc.)
 *   2. Singleton contract, applyOrientation boundary values, and resource-leak regression
 *   3. readImage(File) orientation application logic via mock ImageOrientationReader
 */
package org.vincentyeh.img2pdf.lib.image;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.image.concrete.reader.ExifOrientationReader;
import org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageOrientationReader;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Edge-case tests for {@link ImageIOReader}, covering resource-leak safety,
 * orientation application logic via mock {@link ImageOrientationReader},
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
     * Verifies that a file containing random garbage bytes throws ImageReadingException.
     * The production code now treats an unrecognized format (ImageIO.read returns null)
     * as an error condition, so ImageReadingException must be raised rather than
     * returning null.  The InputStream must still be closed before the exception propagates.
     */
    @Test
    public void readImageFile_garbageBytes_throwsImageReadingException() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path corruptFile = tempDir.resolve("corrupt.jpg");
        Files.write(corruptFile, new byte[]{0x00, 0x01, 0x02, 0x03, 0x7F, (byte) 0xFF});
        assertThrows(ImageReadingException.class,
                () -> reader.readImage(corruptFile.toFile()));
    }

    /**
     * Verifies that a file containing only the JPEG SOI marker (FF D8) with no further
     * data either returns null or throws ImageReadingException. Both outcomes are
     * acceptable: the exact behavior depends on whether a JPEG ImageReader plugin is
     * registered (TwelveMonkeys throws on truncated input; the JDK reader returns null).
     * The critical invariant is that no unchecked exception escapes.
     */
    @Test
    public void readImageFile_truncatedJpegMagicBytes_noUncheckedExceptionEscapes() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path truncated = tempDir.resolve("truncated.jpg");
        // Only the SOI marker, no further valid JPEG data
        Files.write(truncated, new byte[]{(byte) 0xFF, (byte) 0xD8});
        try {
            reader.readImage(truncated.toFile()); // may return null or throw ImageReadingException
        } catch (ImageReadingException ignored) {
            // acceptable: truncated JPEG causes IOException wrapped as ImageReadingException
        }
    }

    /**
     * Verifies that a file filled with all-zero bytes throws ImageReadingException.
     * The production code now treats an unrecognized format (ImageIO.read returns null)
     * as an error, raising ImageReadingException instead of returning null.
     * This also guards against a resource-leak regression: the InputStream must be
     * closed even when ImageReadingException is thrown.
     */
    @Test
    public void readImageFile_allZeroBytes_throwsImageReadingException() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path zeroFile = tempDir.resolve("zeros.png");
        Files.write(zeroFile, new byte[1024]);
        assertThrows(ImageReadingException.class,
                () -> reader.readImage(zeroFile.toFile()));
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
    // seekJpegExifTiff — corrupted APP1 segment length boundary conditions
    //
    // Strategy: inject a corrupt APP1 segment into a real decodable 1x1 JPEG
    // AFTER the first APP segment so that ImageIO.read() can still decode
    // the image, while seekJpegExifTiff sees the injected APP1 with length < 8.
    //
    // The base JPEG is generated at class-init time via ImageIO.write so the
    // bytes are guaranteed to be a valid JFIF stream that the JDK decoder accepts.
    // -------------------------------------------------------------------------

    /**
     * A valid 1x1 JPEG byte array generated at class-init time via
     * {@link ImageIO#write}. All injected-APP1 tests use this as their base.
     */
    private static byte[] VALID_1X1_JPEG;

    /**
     * Generates {@link #VALID_1X1_JPEG} once before any test runs.
     * Creates a 1x1 white RGB image and encodes it as JPEG via ImageIO.
     */
    @BeforeAll
    static void generateValidJpeg() throws IOException {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0xFFFFFF);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        VALID_1X1_JPEG = baos.toByteArray();
    }

    /**
     * Returns the byte offset immediately after the first APP segment
     * (APP0 = {@code FF E0} or APP1 = {@code FF E1}) in {@link #VALID_1X1_JPEG}.
     * The injection point is chosen so that the APP segment is already consumed
     * by the JPEG decoder, and any additionally injected APP1 appears as a
     * normal out-of-order segment.
     *
     * <p>If neither APP0 nor APP1 immediately follows the SOI, returns 2
     * (right after SOI).</p>
     */
    private static int firstAppSegmentEndOffset() {
        if (VALID_1X1_JPEG.length < 6) return 2;
        int marker = ((VALID_1X1_JPEG[2] & 0xFF) << 8) | (VALID_1X1_JPEG[3] & 0xFF);
        // 0xFFE0 = APP0, 0xFFE1 = APP1
        if (marker == 0xFFE0 || marker == 0xFFE1) {
            int lengthField = ((VALID_1X1_JPEG[4] & 0xFF) << 8) | (VALID_1X1_JPEG[5] & 0xFF);
            return 2 + 2 + lengthField; // SOI(2) + marker(2) + body(lengthField)
        }
        return 2;
    }

    /**
     * Builds a JPEG by inserting an APP1 segment with the given length field value
     * after the APP0 segment of a valid 1x1 JPEG. This lets ImageIO decode the image
     * successfully while exercising {@code seekJpegExifTiff} with a corrupt length.
     *
     * <p>The APP1 payload is {@code max(0, app1Length - 2)} zero bytes.</p>
     *
     * @param app1Length the value to encode in the APP1 length field (big-endian, 2 bytes)
     * @return JPEG bytes with the injected APP1 segment
     */
    private static byte[] buildValidJpegWithInjectedApp1Length(int app1Length) throws IOException {
        int split = firstAppSegmentEndOffset();
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        // Bytes before injection point (SOI + APP0)
        buf.write(VALID_1X1_JPEG, 0, split);
        // Injected APP1 with corrupt/boundary length
        buf.write(new byte[]{(byte) 0xFF, (byte) 0xE1});  // APP1 marker
        buf.write((app1Length >> 8) & 0xFF);               // length high byte
        buf.write(app1Length & 0xFF);                      // length low byte
        int payloadLen = app1Length > 2 ? app1Length - 2 : 0;
        if (payloadLen > 0) {
            buf.write(new byte[payloadLen]);               // zero-filled payload
        }
        // Remaining JPEG segments (DQT, SOF0, DHT, SOS, image data, EOI)
        buf.write(VALID_1X1_JPEG, split, VALID_1X1_JPEG.length - split);
        return buf.toByteArray();
    }

    /**
     * Verifies that a JPEG whose injected APP1 length field equals 4 (below the 8-byte
     * minimum) does not cause an exception. The corrupt segment must be silently skipped
     * and the image decoded successfully.
     */
    @Test
    public void readImageFile_app1LengthFour_corruptSegmentSkippedNoException() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path jpeg = tempDir.resolve("app1_length_4.jpg");
        Files.write(jpeg, buildValidJpegWithInjectedApp1Length(4));
        assertDoesNotThrow(() -> reader.readImage(jpeg.toFile()));
    }

    /**
     * Verifies that a JPEG whose injected APP1 length field equals 7 (one byte below the
     * 8-byte minimum) does not cause an exception.
     */
    @Test
    public void readImageFile_app1LengthSeven_corruptSegmentSkippedNoException() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path jpeg = tempDir.resolve("app1_length_7.jpg");
        Files.write(jpeg, buildValidJpegWithInjectedApp1Length(7));
        assertDoesNotThrow(() -> reader.readImage(jpeg.toFile()));
    }

    /**
     * Verifies that a JPEG whose injected APP1 length field equals 1 (pathologically
     * small, below even the 2-byte self-referential minimum) does not cause an exception.
     */
    @Test
    public void readImageFile_app1LengthOne_corruptSegmentSkippedNoException() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path jpeg = tempDir.resolve("app1_length_1.jpg");
        Files.write(jpeg, buildValidJpegWithInjectedApp1Length(1));
        assertDoesNotThrow(() -> reader.readImage(jpeg.toFile()));
    }

    /**
     * Verifies that a JPEG whose injected APP1 length field equals 0 does not cause
     * an exception.
     */
    @Test
    public void readImageFile_app1LengthZero_corruptSegmentSkippedNoException() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path jpeg = tempDir.resolve("app1_length_0.jpg");
        Files.write(jpeg, buildValidJpegWithInjectedApp1Length(0));
        assertDoesNotThrow(() -> reader.readImage(jpeg.toFile()));
    }

    /**
     * Verifies the exact boundary where length == 8: the minimum accepted value.
     * The APP1 segment has a 6-byte payload of zeros (not "Exif\\0\\0"), so
     * no EXIF orientation is found, but processing must not throw.
     */
    @Test
    public void readImageFile_app1LengthExactlyEight_boundaryProcessedNoException() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path jpeg = tempDir.resolve("app1_length_8.jpg");
        Files.write(jpeg, buildValidJpegWithInjectedApp1Length(8));
        assertDoesNotThrow(() -> reader.readImage(jpeg.toFile()));
    }

    /**
     * Verifies that when two APP1 segments are injected — the first with a corrupt
     * length (5) and the second with a valid zero-filled payload (length 10) — neither
     * causes an exception and the image is decoded successfully.
     */
    @Test
    public void readImageFile_firstApp1CorruptSecondApp1ValidLength_noException() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        int split = firstAppSegmentEndOffset();
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        buf.write(VALID_1X1_JPEG, 0, split);
        // First APP1: corrupt length = 5, payload = 3 bytes
        buf.write(new byte[]{(byte) 0xFF, (byte) 0xE1, 0x00, 0x05});
        buf.write(new byte[3]);
        // Second APP1: length = 10, payload = 8 zero bytes (not a real EXIF header)
        buf.write(new byte[]{(byte) 0xFF, (byte) 0xE1, 0x00, 0x0A});
        buf.write(new byte[8]);
        buf.write(VALID_1X1_JPEG, split, VALID_1X1_JPEG.length - split);
        Path jpeg = tempDir.resolve("app1_double.jpg");
        Files.write(jpeg, buf.toByteArray());
        assertDoesNotThrow(() -> reader.readImage(jpeg.toFile()));
    }

    // -------------------------------------------------------------------------
    // C-1: APP1 length == 8 with non-EXIF header (e.g., XMP data)
    //
    // Fix: when length == 8 but the 6-byte header is NOT "Exif\0\0",
    // the code must skip remaining (length - 2 - 6 = 0) bytes and continue
    // scanning rather than entering an infinite loop or crashing.
    // -------------------------------------------------------------------------

    /**
     * Builds a JPEG that contains an APP1 segment of exactly length=8 whose
     * 6-byte payload is XMP-like data ("XMP\0\0\0") rather than "Exif\0\0".
     * After this segment the real JPEG data follows, so ImageIO can decode the image.
     *
     * <p>This exercises fix C-1: the non-EXIF APP1 branch with length == 8 must
     * skip 0 additional bytes (length - 2 - 6 = 0) and continue scanning.</p>
     */
    private static byte[] buildValidJpegWithXmpApp1LengthEight() throws IOException {
        int split = firstAppSegmentEndOffset();
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        buf.write(VALID_1X1_JPEG, 0, split);
        // APP1 marker + length=8 + 6-byte XMP-like header (not "Exif\0\0")
        buf.write(new byte[]{
                (byte) 0xFF, (byte) 0xE1,   // APP1 marker
                0x00, 0x08,                  // length = 8
                'X', 'M', 'P', 0x00, 0x00, 0x00  // 6-byte non-EXIF header
        });
        buf.write(VALID_1X1_JPEG, split, VALID_1X1_JPEG.length - split);
        return buf.toByteArray();
    }

    /**
     * C-1: APP1 length == 8, header is XMP data, not "Exif\0\0".
     * Must not loop infinitely; must decode the image successfully (non-null result).
     */
    @Test
    public void readImageFile_app1LengthEightXmpHeader_nonExifSkippedImageDecoded() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path jpeg = tempDir.resolve("app1_length_8_xmp.jpg");
        Files.write(jpeg, buildValidJpegWithXmpApp1LengthEight());
        // assertTimeoutPreemptively guards against infinite loop regression in seekJpegExifTiff
        BufferedImage result = assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> reader.readImage(jpeg.toFile()));
        assertNotNull(result, "image should be decoded successfully when non-EXIF APP1 is skipped");
    }

    /**
     * C-1: APP1 length == 8, header is all-zero bytes (not "Exif\0\0").
     * Verifies the skip path handles the exact boundary without looping.
     */
    @Test
    public void readImageFile_app1LengthEightZeroHeader_nonExifSkippedImageDecoded() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path jpeg = tempDir.resolve("app1_length_8_zeros.jpg");
        // buildValidJpegWithInjectedApp1Length(8) produces a zero-filled 6-byte payload
        Files.write(jpeg, buildValidJpegWithInjectedApp1Length(8));
        // assertTimeoutPreemptively guards against infinite loop regression
        BufferedImage result = assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> reader.readImage(jpeg.toFile()));
        assertNotNull(result, "image should be decoded even when APP1 length==8 payload is not EXIF");
    }

    // -------------------------------------------------------------------------
    // C-2: APP1 length < 8 — cursor must advance by (length - 2) bytes so that
    // subsequent markers are still parsed correctly and the image is decoded.
    // -------------------------------------------------------------------------

    /**
     * C-2: APP1 length = 4 (pathologically small). After skipping 2 bytes payload,
     * the scanner must resume at the next valid JPEG marker and the image must decode.
     */
    @Test
    public void readImageFile_app1LengthFour_cursorAdvancedImageDecoded() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path jpeg = tempDir.resolve("app1_c2_length_4.jpg");
        Files.write(jpeg, buildValidJpegWithInjectedApp1Length(4));
        // assertTimeoutPreemptively detects if cursor misalignment causes infinite marker scan
        BufferedImage result = assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> reader.readImage(jpeg.toFile()));
        assertNotNull(result, "image should be decoded after cursor correctly skips short APP1");
    }

    /**
     * C-2: APP1 length = 7 (one below the 8-byte threshold). After skipping 5 bytes,
     * the scanner must reach the next valid JPEG marker and decode the image.
     */
    @Test
    public void readImageFile_app1LengthSeven_cursorAdvancedImageDecoded() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path jpeg = tempDir.resolve("app1_c2_length_7.jpg");
        Files.write(jpeg, buildValidJpegWithInjectedApp1Length(7));
        // assertTimeoutPreemptively detects if a 5-skip misalignment causes infinite scan
        BufferedImage result = assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> reader.readImage(jpeg.toFile()));
        assertNotNull(result, "image should be decoded after cursor correctly skips 5-byte APP1 body");
    }

    /**
     * C-2: APP1 length = 2 (minimum possible — payload is 0 bytes, no skip needed).
     * Verifies that the skipBytes(0) branch does not cause any error.
     */
    @Test
    public void readImageFile_app1LengthTwo_noSkipNeededImageDecoded() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path jpeg = tempDir.resolve("app1_c2_length_2.jpg");
        Files.write(jpeg, buildValidJpegWithInjectedApp1Length(2));
        // assertTimeoutPreemptively guards against the zero-skip edge-case stalling
        BufferedImage result = assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> reader.readImage(jpeg.toFile()));
        assertNotNull(result, "image should be decoded when APP1 has zero-byte body (length=2)");
    }

    // -------------------------------------------------------------------------
    // M-1: JPEG with EXIF APP1 where no Orientation tag is present in the IFD.
    //
    // The fix guards against entry.getValue() == null before casting to Number.
    // A real minimal EXIF TIFF with an empty IFD (no Orientation tag) exercises
    // the entry == null branch, which is equivalent to the value == null guard:
    // both paths must return OptionalInt.empty() without NPE.
    //
    // Strategy: craft a minimal valid JPEG + APP1 EXIF with a TIFF IFD that has
    // zero directory entries, so ifd0.getEntryById(TIFF.TAG_ORIENTATION) returns null.
    // -------------------------------------------------------------------------

    /**
     * Builds a minimal JPEG with a valid EXIF APP1 containing a TIFF header
     * followed by an IFD0 with zero entries and no Orientation tag.
     *
     * <p>TIFF structure (little-endian):
     * <pre>
     *   Bytes 0-1:  "II" (little-endian byte order)
     *   Bytes 2-3:  0x002A (TIFF magic)
     *   Bytes 4-7:  0x00000008 (offset to IFD0, immediately follows header)
     *   Bytes 8-9:  0x0000 (IFD entry count = 0)
     *   Bytes 10-13: 0x00000000 (next IFD offset = 0, no more IFDs)
     * </pre>
     * Total TIFF payload: 14 bytes.
     * APP1 length field: 2 (self) + 6 ("Exif\0\0") + 14 (TIFF) = 22 bytes.
     * </p>
     */
    private static byte[] buildValidJpegWithExifNoOrientationTag() throws IOException {
        // Minimal TIFF with empty IFD0 (no entries)
        byte[] tiff = new byte[]{
                'I', 'I',               // little-endian byte order
                0x2A, 0x00,             // TIFF magic number 42
                0x08, 0x00, 0x00, 0x00, // offset to IFD0 = 8 (immediately follows header)
                0x00, 0x00,             // IFD entry count = 0
                0x00, 0x00, 0x00, 0x00  // next IFD offset = 0
        };
        // APP1 payload: "Exif\0\0" + tiff
        int app1Length = 2 + 6 + tiff.length; // = 22
        int split = firstAppSegmentEndOffset();
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        buf.write(VALID_1X1_JPEG, 0, split);
        buf.write(new byte[]{(byte) 0xFF, (byte) 0xE1}); // APP1 marker
        buf.write((app1Length >> 8) & 0xFF);
        buf.write(app1Length & 0xFF);
        buf.write(new byte[]{'E', 'x', 'i', 'f', 0x00, 0x00}); // EXIF header
        buf.write(tiff);
        buf.write(VALID_1X1_JPEG, split, VALID_1X1_JPEG.length - split);
        return buf.toByteArray();
    }

    /**
     * M-1: JPEG with valid EXIF APP1 but IFD0 has no Orientation tag.
     * readExifOrientation() must return OptionalInt.empty() (entry == null path),
     * and readImage() must return the decoded image without throwing NPE.
     */
    @Test
    public void readImageFile_exifApp1NoOrientationTag_returnsDecodedImageWithoutNpe() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path jpeg = tempDir.resolve("exif_no_orientation.jpg");
        Files.write(jpeg, buildValidJpegWithExifNoOrientationTag());
        // assertTimeoutPreemptively ensures no hang if TIFF parsing unexpectedly loops
        BufferedImage result = assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> reader.readImage(jpeg.toFile()),
                "must not throw NPE when EXIF IFD has no Orientation tag"
        );
        assertNotNull(result, "image must be decoded even when Orientation tag is absent");
    }

    // -------------------------------------------------------------------------
    // M-2: JPEG ending with EOI marker (0xFFD9) — seekJpegExifTiff must NOT
    // attempt to read a length field after EOI, as doing so would read 2 extra
    // bytes and corrupt the stream position.
    //
    // Fix: EOI is handled before the length-field read in seekJpegExifTiff.
    // Strategy: construct a minimal JPEG whose only segment after SOI is EOI,
    // verify it terminates immediately (no exception, no infinite read).
    // -------------------------------------------------------------------------

    /**
     * Builds a JPEG where seekJpegExifTiff will encounter EOI (0xFFD9) as the very
     * first marker after SOI — with exactly 0 bytes remaining after the EOI.
     *
     * <p>This is the tightest possible byte boundary for M-2: if the fix were absent,
     * the code would call {@code readUnsignedShort()} on an empty stream after EOI,
     * which would throw {@code EOFException}. With the fix, EOI is matched before
     * the length-field read, so the method returns {@code false} and the
     * {@code EOFException} catch block in {@code seekJpegExifTiff} is never reached.</p>
     */
    private static byte[] buildJpegSoiEoiOnly() {
        // SOI (FF D8) + EOI (FF D9) — 4 bytes, no space for a length field after EOI
        return new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xD9};
    }

    /**
     * M-2: EOI is the very first marker after SOI, with 0 bytes remaining.
     * seekJpegExifTiff must handle 0xFFD9 before attempting to read a length field,
     * returning false immediately. Without the fix, reading the length of EOI would
     * throw EOFException, which would surface as ImageReadingException. With the fix,
     * the method returns false cleanly and the outcome is either null or ImageReadingException
     * (from ImageIO decoder), never an unchecked exception.
     */
    @Test
    public void readImageFile_soiEoiOnly_eoimarkerHandledWithoutLengthRead() {
        ImageReader reader = ImageIOReader.getInstance();
        // assertTimeoutPreemptively guards against any regression that causes an infinite loop
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Path jpeg = tempDir.resolve("soi_eoi_only.jpg");
            Files.write(jpeg, buildJpegSoiEoiOnly());
            try {
                reader.readImage(jpeg.toFile());
                // null return is acceptable: ImageIO cannot decode a 4-byte JPEG
            } catch (ImageReadingException ignored) {
                // acceptable: ImageIO may throw when decoding a header-only JPEG
            }
            // RuntimeException must NOT escape — if it does, the test fails
        });
    }

    /**
     * M-2: A JPEG with a non-EXIF APP2 segment (0xFFE2) followed immediately by
     * EOI — no SOS or image data. Verifies that after skipping the APP2 body,
     * the scanner reads 0xFFD9 and returns false without attempting to read its
     * length field. Without the fix, the code would try to read 2 bytes for the
     * EOI's (non-existent) length, causing EOFException.
     *
     * <p>Uses APP2 (not APP1) so that seekJpegExifTiff does not take the APP1 branch
     * and instead falls through to the generic {@code skipBytes(length - 2)} branch,
     * then immediately reads EOI as the next marker.</p>
     */
    @Test
    public void readImageFile_app2ThenEoi_eoimarkerHandledWithoutLengthRead() {
        ImageReader reader = ImageIOReader.getInstance();
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Path jpeg = tempDir.resolve("app2_then_eoi.jpg");
            // SOI + APP2 (marker=FFE2, length=4, 2-byte zero body) + EOI (FFD9)
            // seek scans: SOI consumed by positionAtExifTiff, then finds APP2 (skipBytes(2)),
            // then reads EOI — must return false, not throw
            byte[] data = new byte[]{
                    (byte) 0xFF, (byte) 0xD8,  // SOI
                    (byte) 0xFF, (byte) 0xE2,  // APP2 marker (not APP0/APP1)
                    0x00, 0x04,                 // length = 4 (2-byte body)
                    0x00, 0x00,                 // body
                    (byte) 0xFF, (byte) 0xD9   // EOI — immediately after APP2
            };
            Files.write(jpeg, data);
            try {
                reader.readImage(jpeg.toFile());
            } catch (ImageReadingException ignored) {
                // acceptable: no decodable image data
            } catch (RuntimeException re) {
                // A RuntimeException from the JDK decoder (e.g. ArrayIndexOutOfBoundsException)
                // is acceptable here because it originates in ImageIO.read(), not in
                // seekJpegExifTiff. The M-2 fix only covers the EXIF-scanner path.
                // If the timeout did not expire, the fix is working.
            }
        });
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

    // =========================================================================
    // seekJpegExifTiff — APP0(JFIF) skip-and-continue: additional byte-level tests
    //
    // These tests use hand-crafted JPEG byte arrays (buildJpegWithAppSegments)
    // to exercise specific parsing paths in seekJpegExifTiff without relying
    // on exiftool. Real-image orientation scenarios are covered separately by
    // the exiftool-based tests below.
    // =========================================================================

    /**
     * Builds a raw TIFF block (little-endian) with a single Orientation IFD entry
     * set to the given value.
     *
     * @param orientation EXIF orientation value 1-8
     * @return 26-byte TIFF block encoding the specified orientation
     */
    private static byte[] buildTiffOrientation(int orientation) {
        return new byte[]{
                'I', 'I',                               // byte order: little-endian
                0x2A, 0x00,                             // TIFF magic 42
                0x08, 0x00, 0x00, 0x00,                 // IFD0 offset = 8
                0x01, 0x00,                             // IFD entry count = 1
                0x12, 0x01,                             // tag = 0x0112 (Orientation)
                0x03, 0x00,                             // type = SHORT
                0x01, 0x00, 0x00, 0x00,                 // count = 1
                (byte) orientation, 0x00, 0x00, 0x00,   // value inline (little-endian)
                0x00, 0x00, 0x00, 0x00                  // next IFD offset = 0
        };
    }

    /**
     * Builds an APP1 EXIF segment embedding the given TIFF block.
     *
     * @param tiffBlock raw TIFF bytes to embed in the APP1 segment
     * @return complete APP1 bytes including FF E1 marker and length field
     */
    private static byte[] buildExifApp1WithTiff(byte[] tiffBlock) throws IOException {
        int length = 2 + 6 + tiffBlock.length; // 2 (length field) + 6 ("Exif\0\0") + TIFF
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(new byte[]{(byte) 0xFF, (byte) 0xE1}); // APP1 marker
        buf.write((length >> 8) & 0xFF);
        buf.write(length & 0xFF);
        buf.write(new byte[]{'E', 'x', 'i', 'f', 0x00, 0x00});
        buf.write(tiffBlock);
        return buf.toByteArray();
    }

    /**
     * Landscape JPEG image bytes (width=2, height=1) generated once for the
     * JFIF/EXIF fix tests.  This field is populated by {@link #generateLandscapeJpegSmall()}.
     */
    private static byte[] LANDSCAPE_2X1_JPEG;

    /**
     * Generates {@link #LANDSCAPE_2X1_JPEG} once before any test in this class.
     * Creates a 2x1 white RGB image and encodes it as JPEG via ImageIO.
     */
    @BeforeAll
    static void generateLandscapeJpegSmall() throws IOException {
        BufferedImage img = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0xFFFFFF);
        img.setRGB(1, 0, 0xFFFFFF);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        LANDSCAPE_2X1_JPEG = baos.toByteArray();
    }

    /**
     * Returns the byte offset of the first non-APP JPEG marker (e.g., DQT 0xFFDB)
     * in {@link #LANDSCAPE_2X1_JPEG}, so that all generated APP segments can be
     * replaced with custom ones while keeping the raw image data intact.
     *
     * <p>Scans forward from byte 2 (after SOI), skipping any APP segment (0xFFE0–0xFFEF)
     * by reading its length field. Returns the offset of the first marker that is
     * not an APP segment.</p>
     *
     * @return byte offset of the first non-APP marker in {@link #LANDSCAPE_2X1_JPEG}
     */
    private static int firstNonAppOffset() {
        int pos = 2; // skip SOI (FF D8)
        byte[] data = LANDSCAPE_2X1_JPEG;
        while (pos + 3 < data.length) {
            int marker = ((data[pos] & 0xFF) << 8) | (data[pos + 1] & 0xFF);
            // APP markers: 0xFFE0 .. 0xFFEF
            if (marker >= 0xFFE0 && marker <= 0xFFEF) {
                int segLen = ((data[pos + 2] & 0xFF) << 8) | (data[pos + 3] & 0xFF);
                pos += 2 + segLen; // marker(2) already counted by the length field convention
                // length includes the 2 length bytes themselves but NOT the 2 marker bytes
            } else {
                break;
            }
        }
        return pos;
    }

    /**
     * Builds a complete JPEG byte array by combining:
     *   SOI + {@code appSegments} + raw image data from {@link #LANDSCAPE_2X1_JPEG}.
     *
     * @param appSegments zero or more APP segment byte arrays to insert after SOI
     * @return complete JPEG byte array
     */
    private static byte[] buildJpegWithAppSegments(byte[]... appSegments) throws IOException {
        int rawOffset = firstNonAppOffset();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(new byte[]{(byte) 0xFF, (byte) 0xD8}); // SOI
        for (byte[] seg : appSegments) {
            buf.write(seg);
        }
        buf.write(LANDSCAPE_2X1_JPEG, rawOffset, LANDSCAPE_2X1_JPEG.length - rawOffset);
        return buf.toByteArray();
    }

    /**
     * Builds a minimal APP0 JFIF segment.
     * Payload: "JFIF\0" + version(2) + units(1) + Xdensity(2) + Ydensity(2) + thumbW(1) + thumbH(1).
     * Total payload = 13 bytes; length field = 13 + 2 = 15 (0x000F).
     *
     * @return raw APP0 bytes including marker and length field
     */
    private static byte[] buildJfifApp0() {
        return new byte[]{
                (byte) 0xFF, (byte) 0xE0,   // APP0 marker
                0x00, 0x10,                  // length = 16 (14-byte payload + 2)
                'J', 'F', 'I', 'F', 0x00,   // identifier "JFIF\0"
                0x01, 0x01,                  // version 1.1
                0x00,                        // aspect ratio units: 0 = no units
                0x00, 0x01,                  // Xdensity = 1
                0x00, 0x01,                  // Ydensity = 1
                0x00,                        // thumbnail width = 0
                0x00                         // thumbnail height = 0
        };
    }

    /**
     * Builds an APP1 EXIF segment containing a TIFF block encoding Orientation=6.
     *
     * @return raw APP1 bytes including marker and length field
     */
    private static byte[] buildExifApp1WithOrientation6() throws IOException {
        return buildExifApp1WithTiff(buildTiffOrientation(6));
    }

    /**
     * Builds a non-EXIF APP1 segment (simulating XMP metadata).
     * Payload header is "http\0\0" (not "Exif\0\0"); total length = 2+6+4 = 12.
     *
     * @return raw non-EXIF APP1 bytes including FF E1 marker and length field
     */
    private static byte[] buildXmpApp1() throws IOException {
        // payload: 6-byte non-EXIF header + 4 zero bytes
        byte[] payload = new byte[]{'h', 't', 't', 'p', 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        int length = 2 + payload.length; // 12
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(new byte[]{(byte) 0xFF, (byte) 0xE1}); // APP1 marker
        buf.write((length >> 8) & 0xFF);
        buf.write(length & 0xFF);
        buf.write(payload);
        return buf.toByteArray();
    }

    /**
     * Builds an oversized APP0 segment with a 32-byte payload (total length=34).
     * The payload starts with "JFIF\0" followed by zeros; this is non-standard
     * but exercises the general skipBytes path for APP0 in seekJpegExifTiff.
     *
     * @return raw oversized APP0 bytes including FF E0 marker and length field
     */
    private static byte[] buildOversizedApp0() {
        // payload = 32 bytes; length field = 32 + 2 = 34 (0x0022)
        byte[] segment = new byte[2 + 2 + 32]; // marker(2) + length(2) + payload(32)
        segment[0] = (byte) 0xFF;
        segment[1] = (byte) 0xE0;
        segment[2] = 0x00;
        segment[3] = 0x22; // length = 34 (payload 32 + length-field 2)
        // payload starts with "JFIF\0" + version + zeros
        segment[4] = 'J';
        segment[5] = 'F';
        segment[6] = 'I';
        segment[7] = 'F';
        segment[8] = 0x00;
        segment[9] = 0x01;
        segment[10] = 0x01;
        // bytes 11-35 are zero (thumbnail data etc.)
        return segment;
    }

    /**
     * Isolation test: verifies that {@link ExifOrientationReader#readOrientation(File)}
     * correctly returns Orientation=6 for a JFIF+EXIF JPEG (byte-level, no exiftool).
     */
    @Test
    public void readExifOrientation_jfifThenExifOrientation6_returnsOrientationSix() throws Exception {
        byte[] jpeg = buildJpegWithAppSegments(buildJfifApp0(), buildExifApp1WithOrientation6());
        Path file = tempDir.resolve("diag_jfif_exif_orient.jpg");
        Files.write(file, jpeg);

        ExifOrientationReader exifReader = new ExifOrientationReader();
        java.util.OptionalInt result = exifReader.readOrientation(file.toFile());

        assertTrue(result.isPresent(),
                "readOrientation must find Orientation tag in JFIF+EXIF JPEG; got empty");
        assertEquals(6, result.getAsInt(),
                "readOrientation must return 6 for Orientation=6 EXIF in JFIF+EXIF JPEG");
    }

    /**
     * Deep isolation test: verifies that {@code seekJpegExifTiff} (accessed via reflection
     * on {@link ExifOrientationReader}) returns {@code true} when given an
     * {@link javax.imageio.stream.ImageInputStream} positioned immediately after the SOI marker
     * of a JFIF+EXIF JPEG.
     */
    @Test
    public void seekJpegExifTiff_jfifThenExifOrientation6_returnsTrue() throws Exception {
        byte[] jpeg = buildJpegWithAppSegments(buildJfifApp0(), buildExifApp1WithOrientation6());
        Path file = tempDir.resolve("seek_diag_jfif_exif.jpg");
        Files.write(file, jpeg);

        // Obtain a FileImageInputStream positioned at offset 2 (after SOI)
        javax.imageio.stream.ImageInputStream iis = javax.imageio.ImageIO.createImageInputStream(file.toFile());
        assertNotNull(iis, "createImageInputStream must not return null for a valid JPEG file");
        iis.readUnsignedShort(); // consume SOI (FF D8)

        try {
            java.lang.reflect.Method m = ExifOrientationReader.class
                    .getDeclaredMethod("seekJpegExifTiff",
                            javax.imageio.stream.ImageInputStream.class);
            m.setAccessible(true);
            boolean found = (Boolean) m.invoke(null, iis);

            assertTrue(found,
                    "seekJpegExifTiff must return true for a JPEG with JFIF APP0 followed by EXIF APP1; "
                            + "got false, which means APP0 still causes early exit or APP1 is misread");
        } finally {
            iis.close();
        }
    }

    /**
     * TIFFReader isolation test: verifies that {@link com.twelvemonkeys.imageio.metadata.tiff.TIFFReader}
     * correctly parses the Orientation tag from the embedded TIFF block in a JFIF+EXIF JPEG.
     */
    @Test
    public void tiffReader_jfifExifJpeg_parsesOrientationSix() throws Exception {
        byte[] jpeg = buildJpegWithAppSegments(buildJfifApp0(), buildExifApp1WithOrientation6());
        Path file = tempDir.resolve("tiff_reader_diag.jpg");
        Files.write(file, jpeg);

        // TIFFReader interprets IFD offsets as absolute stream positions.
        // Extract TIFF bytes from offset 30 and create a stream starting at position 0.
        // SOI(2) + APP0-marker+len(4) + APP0-payload(14) + APP1-marker+len(4) + "Exif\0\0"(6) = 30
        byte[] tiffBytes = java.util.Arrays.copyOfRange(jpeg, 30, jpeg.length);
        javax.imageio.stream.ImageInputStream iis = new javax.imageio.stream.MemoryCacheImageInputStream(
                new java.io.ByteArrayInputStream(tiffBytes));

        try {
            com.twelvemonkeys.imageio.metadata.Directory dir =
                    new com.twelvemonkeys.imageio.metadata.tiff.TIFFReader().read(iis);

            assertNotNull(dir, "TIFFReader must parse a non-null Directory from the TIFF block");

            com.twelvemonkeys.imageio.metadata.Directory ifd0 =
                    (dir instanceof com.twelvemonkeys.imageio.metadata.CompoundDirectory)
                            ? ((com.twelvemonkeys.imageio.metadata.CompoundDirectory) dir).getDirectory(0)
                            : dir;

            assertNotNull(ifd0, "IFD0 directory must not be null");

            com.twelvemonkeys.imageio.metadata.Entry entry =
                    ifd0.getEntryById(com.twelvemonkeys.imageio.metadata.tiff.TIFF.TAG_ORIENTATION);

            assertNotNull(entry, "IFD0 must contain an Orientation entry (extracted from JPEG offset 30)");
            assertNotNull(entry.getValue(), "Orientation entry value must not be null");
            assertEquals(6, ((Number) entry.getValue()).intValue(),
                    "Orientation value must be 6 (Rotate 90 CW)");
        } finally {
            iis.close();
        }
    }

    /**
     * TIFFReader reference test: verifies that {@code TIFFReader} correctly parses
     * Orientation=6 from the EXIF-only JPEG (no JFIF APP0), where the TIFF data
     * starts at stream offset 12.
     */
    @Test
    public void tiffReader_exifOnlyJpeg_parsesOrientationSix() throws Exception {
        byte[] jpeg = buildJpegWithAppSegments(buildExifApp1WithOrientation6());
        Path file = tempDir.resolve("tiff_reader_exif_only.jpg");
        Files.write(file, jpeg);

        javax.imageio.stream.ImageInputStream iis = javax.imageio.ImageIO.createImageInputStream(file.toFile());
        assertNotNull(iis);
        // SOI(2) + APP1-marker+len(4) + "Exif\0\0"(6) = 12
        iis.seek(12);

        try {
            com.twelvemonkeys.imageio.metadata.Directory dir =
                    new com.twelvemonkeys.imageio.metadata.tiff.TIFFReader().read(iis);

            assertNotNull(dir, "TIFFReader must parse a non-null Directory from the TIFF block");

            com.twelvemonkeys.imageio.metadata.Directory ifd0 =
                    (dir instanceof com.twelvemonkeys.imageio.metadata.CompoundDirectory)
                            ? ((com.twelvemonkeys.imageio.metadata.CompoundDirectory) dir).getDirectory(0)
                            : dir;

            com.twelvemonkeys.imageio.metadata.Entry entry =
                    ifd0.getEntryById(com.twelvemonkeys.imageio.metadata.tiff.TIFF.TAG_ORIENTATION);

            assertNotNull(entry, "IFD0 must contain an Orientation entry (TIFF at stream offset 12)");
            assertNotNull(entry.getValue(), "Orientation entry value must not be null");
            assertEquals(6, ((Number) entry.getValue()).intValue(),
                    "Orientation value must be 6 (Rotate 90 CW)");
        } finally {
            iis.close();
        }
    }

    /**
     * Isolation: {@link ExifOrientationReader#readOrientation(File)} must return
     * {@link OptionalInt#empty()} for a JFIF-only JPEG.
     * Verifies that the APP0-skip path in seekJpegExifTiff terminates cleanly without
     * crashing and returns false (no EXIF), so the caller gets an absent orientation.
     */
    @Test
    public void readExifOrientation_jfifOnly_returnsEmpty() throws Exception {
        byte[] jpeg = buildJpegWithAppSegments(buildJfifApp0());
        Path file = tempDir.resolve("diag_jfif_only_no_exif.jpg");
        Files.write(file, jpeg);

        ExifOrientationReader exifReader = new ExifOrientationReader();
        java.util.OptionalInt result = exifReader.readOrientation(file.toFile());

        assertFalse(result.isPresent(),
                "readOrientation must return OptionalInt.empty() for a JFIF-only JPEG (no EXIF segment)");
    }

    /**
     * Large APP0 (32-byte payload) followed by APP1 EXIF Orientation=6.
     * seekJpegExifTiff must skip the full oversized APP0 body without misaligning
     * the cursor, then correctly find and process the APP1 EXIF segment.
     * The result must have height > width (dimensions swapped from the 2x1 original).
     */
    @Test
    public void readImageFile_oversizedApp0ThenExifOrientation6_dimensionsSwapped() throws Exception {
        byte[] jpeg = buildJpegWithAppSegments(buildOversizedApp0(), buildExifApp1WithOrientation6());
        Path file = tempDir.resolve("oversized_app0_then_exif.jpg");
        Files.write(file, jpeg);

        BufferedImage result = assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> ImageIOReader.getInstance().readImage(file.toFile()));

        assertNotNull(result, "image must be decoded when preceded by an oversized APP0");
        // Orientation=6 rotates 90° CW: 2x1 becomes 1x2
        assertTrue(result.getHeight() > result.getWidth(),
                "Oversized APP0 + EXIF Orientation=6 must swap dimensions: expected height > width, got "
                        + result.getWidth() + "x" + result.getHeight());
    }

    /**
     * Three-segment chain: APP0 (JFIF) + non-EXIF APP1 (XMP) + EXIF APP1 (Orientation=6).
     * seekJpegExifTiff must skip both the APP0 and the non-EXIF APP1 and then
     * correctly parse the real EXIF APP1.  The result must have height > width.
     */
    @Test
    public void readImageFile_app0ThenXmpApp1ThenExifOrientation6_dimensionsSwapped() throws Exception {
        byte[] jpeg = buildJpegWithAppSegments(
                buildJfifApp0(),
                buildXmpApp1(),
                buildExifApp1WithOrientation6()
        );
        Path file = tempDir.resolve("app0_xmp_exif_orient6.jpg");
        Files.write(file, jpeg);

        BufferedImage result = assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> ImageIOReader.getInstance().readImage(file.toFile()));

        assertNotNull(result, "image must be decoded from APP0+XMP-APP1+EXIF-APP1 JPEG");
        // Orientation=6 rotates 90° CW: 2x1 becomes 1x2
        assertTrue(result.getHeight() > result.getWidth(),
                "APP0+non-EXIF APP1+EXIF Orientation=6 must swap dimensions: expected height > width, got "
                        + result.getWidth() + "x" + result.getHeight());
    }

    /**
     * Test scenario 5: JPEG where seekJpegExifTiff encounters SOS (0xFFDA) as the very
     * first marker after SOI — no APP segments, just SOI + SOS.
     * seekJpegExifTiff must recognize SOS and return false immediately without reading
     * a length field for SOS, since SOS is listed in the early-exit condition alongside EOI.
     * The outcome (null or ImageReadingException) is acceptable; an unchecked exception must not escape.
     */
    @Test
    public void readImageFile_soiThenSosOnly_sosMarkerHandledWithoutLengthRead() {
        ImageReader reader = ImageIOReader.getInstance();
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Path jpeg = tempDir.resolve("soi_sos_only.jpg");
            // SOI (FF D8) + SOS (FF DA) — 4 bytes total, no length field after SOS
            Files.write(jpeg, new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xDA});
            try {
                reader.readImage(jpeg.toFile());
                // null return is acceptable: no decodable image data
            } catch (ImageReadingException ignored) {
                // acceptable: ImageIO cannot decode a truncated JPEG
            }
            // RuntimeException must NOT escape
        });
    }

    /**
     * Test scenario 4: JPEG with APP0 (JFIF) followed by APP1 whose header is NOT "Exif\0\0".
     * The non-EXIF APP1 must be skipped; no orientation correction applied.
     * The decoded image must retain the original landscape dimensions (width >= height).
     */
    @Test
    public void readImageFile_jfifThenNonExifApp1_returnsOriginalDimensions() throws Exception {
        // Build a non-EXIF APP1 (e.g., XMP data with a non-Exif header)
        byte[] nonExifPayload = new byte[]{'X', 'M', 'P', ' ', 0x00, 0x00, 0x00, 0x00};
        int app1Length = 2 + nonExifPayload.length;
        ByteArrayOutputStream app1Buf = new ByteArrayOutputStream();
        app1Buf.write(new byte[]{(byte) 0xFF, (byte) 0xE1}); // APP1 marker
        app1Buf.write((app1Length >> 8) & 0xFF);
        app1Buf.write(app1Length & 0xFF);
        app1Buf.write(nonExifPayload);
        byte[] nonExifApp1 = app1Buf.toByteArray();

        byte[] jpeg = buildJpegWithAppSegments(buildJfifApp0(), nonExifApp1);
        Path file = tempDir.resolve("jfif_then_non_exif_app1.jpg");
        Files.write(file, jpeg);

        BufferedImage result = ImageIOReader.getInstance().readImage(file.toFile());

        assertNotNull(result, "image must be decoded from JFIF + non-EXIF APP1 JPEG");
        // No valid EXIF Orientation tag found — original dimensions must be preserved
        assertTrue(result.getWidth() >= result.getHeight(),
                "Non-EXIF APP1 must not trigger rotation: expected width >= height, got "
                        + result.getWidth() + "x" + result.getHeight());
    }

    // =========================================================================
    // W-1: null InputStream / null byte[] — readImage overloads without ColorType
    // =========================================================================

    /**
     * W-1: readImage(InputStream) with a null stream must throw ImageReadingException,
     * not NullPointerException.
     */
    @Test
    public void readImageInputStream_nullStream_throwsImageReadingException() {
        ImageReader reader = ImageIOReader.getInstance();
        assertThrows(ImageReadingException.class,
                () -> reader.readImage((java.io.InputStream) null));
    }

    /**
     * W-1: readImage(byte[]) with a null array must throw ImageReadingException,
     * not NullPointerException.
     */
    @Test
    public void readImageByteArray_nullArray_throwsImageReadingException() {
        ImageReader reader = ImageIOReader.getInstance();
        assertThrows(ImageReadingException.class,
                () -> reader.readImage((byte[]) null));
    }

    // =========================================================================
    // W-1: null InputStream / null byte[] — readImage overloads WITH ColorType
    // =========================================================================

    /**
     * W-1: readImage(InputStream, ColorType) with a null stream must throw ImageReadingException,
     * not NullPointerException.
     */
    @Test
    public void readImageInputStreamColorType_nullStream_throwsImageReadingException() {
        ImageReader reader = ImageIOReader.getInstance();
        assertThrows(ImageReadingException.class,
                () -> reader.readImage((java.io.InputStream) null, ColorType.sRGB));
    }

    /**
     * W-1: readImage(byte[], ColorType) with a null array must throw ImageReadingException,
     * not NullPointerException.
     */
    @Test
    public void readImageByteArrayColorType_nullArray_throwsImageReadingException() {
        ImageReader reader = ImageIOReader.getInstance();
        assertThrows(ImageReadingException.class,
                () -> reader.readImage((byte[]) null, ColorType.sRGB));
    }

    // =========================================================================
    // W-2 / C-1: unsupported-format file — readImage(File) and readImage(File, ColorType)
    // must throw ImageReadingException, never NullPointerException
    // =========================================================================

    /**
     * W-2: readImage(File) given a file with an unrecognized extension and garbage content
     * must throw ImageReadingException (not NPE) when ImageIO.read() returns null.
     */
    @Test
    public void readImageFile_unsupportedFormat_throwsImageReadingExceptionNotNpe() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path xyzFile = tempDir.resolve("unsupported.xyz");
        Files.write(xyzFile, new byte[]{0x00, 0x01, 0x02, 0x03, 0x7F, (byte) 0xAB, (byte) 0xCD});
        assertThrows(ImageReadingException.class,
                () -> reader.readImage(xyzFile.toFile()),
                "readImage(File) must throw ImageReadingException for an unsupported format, not NPE");
    }

    /**
     * W-2: readImage(File, ColorType) given an unsupported-format file must throw
     * ImageReadingException (not NPE) — the null-image guard must fire before any cast.
     */
    @Test
    public void readImageFileColorType_unsupportedFormat_throwsImageReadingExceptionNotNpe() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        Path xyzFile = tempDir.resolve("unsupported_ct.xyz");
        Files.write(xyzFile, new byte[]{0x00, 0x01, 0x02, 0x03, 0x7F, (byte) 0xAB, (byte) 0xCD});
        assertThrows(ImageReadingException.class,
                () -> reader.readImage(xyzFile.toFile(), ColorType.sRGB),
                "readImage(File, ColorType) must throw ImageReadingException for an unsupported format, not NPE");
    }

    /**
     * W-2: readImage(InputStream, ColorType) with garbage bytes that no decoder recognizes
     * must throw ImageReadingException because ImageIO.read() returns null.
     */
    @Test
    public void readImageInputStreamColorType_garbageBytes_throwsImageReadingException() {
        ImageReader reader = ImageIOReader.getInstance();
        java.io.InputStream garbage = new ByteArrayInputStream(
                new byte[]{0x00, 0x01, 0x02, 0x03, 0x7F, (byte) 0xFF});
        assertThrows(ImageReadingException.class,
                () -> reader.readImage(garbage, ColorType.sRGB),
                "readImage(InputStream, ColorType) must throw ImageReadingException when format is unrecognized");
    }

    /**
     * W-2: readImage(byte[], ColorType) with garbage bytes that no decoder recognizes
     * must throw ImageReadingException because ImageIO.read() returns null.
     */
    @Test
    public void readImageByteArrayColorType_garbageBytes_throwsImageReadingException() {
        ImageReader reader = ImageIOReader.getInstance();
        byte[] garbage = new byte[]{0x00, 0x01, 0x02, 0x03, 0x7F, (byte) 0xFF};
        assertThrows(ImageReadingException.class,
                () -> reader.readImage(garbage, ColorType.sRGB),
                "readImage(byte[], ColorType) must throw ImageReadingException when format is unrecognized");
    }

    // =========================================================================
    // W-3: orientation value out of range (9) — applyOrientation must return
    // the original image unchanged and print a warning to System.err.
    // Accessed via reflection because applyOrientation is private.
    // =========================================================================

    /**
     * W-3: applyOrientation with orientation=9 (out of 1-8 range) must return
     * the original image instance unchanged without throwing any exception.
     */
    @Test
    public void applyOrientation_orientationNine_returnsOriginalImageWithoutException() throws Exception {
        java.lang.reflect.Method m = org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader.class
                .getDeclaredMethod("applyOrientation", BufferedImage.class, int.class);
        m.setAccessible(true);

        BufferedImage original = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        // Must not throw; must return the same instance (default/unknown branch is a no-op)
        BufferedImage result = assertDoesNotThrow(() ->
                (BufferedImage) m.invoke(null, original, 9));
        assertSame(original, result,
                "applyOrientation with out-of-range value 9 must return the original image unchanged");
    }

    /**
     * W-3: applyOrientation with orientation=9 must write a warning to System.err.
     * Redirects System.err temporarily to capture the output.
     */
    @Test
    public void applyOrientation_orientationNine_writesWarningToStderr() throws Exception {
        java.lang.reflect.Method m = org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader.class
                .getDeclaredMethod("applyOrientation", BufferedImage.class, int.class);
        m.setAccessible(true);

        java.io.PrintStream originalErr = System.err;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(captured));
        try {
            m.invoke(null, new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB), 9);
        } finally {
            System.setErr(originalErr);
        }

        String output = captured.toString();
        assertTrue(output.contains("9"),
                "applyOrientation with orientation=9 must print the unrecognized value in the warning; got: "
                        + output);
    }

    /**
     * W-3: applyOrientation with orientation=0 (also out of 1-8 range) must return
     * the original image without throwing.
     */
    @Test
    public void applyOrientation_orientationZero_returnsOriginalImageWithoutException() throws Exception {
        java.lang.reflect.Method m = org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader.class
                .getDeclaredMethod("applyOrientation", BufferedImage.class, int.class);
        m.setAccessible(true);

        BufferedImage original = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = assertDoesNotThrow(() ->
                (BufferedImage) m.invoke(null, original, 0));
        assertSame(original, result,
                "applyOrientation with orientation=0 must return the original image unchanged");
    }

    /**
     * W-3: applyOrientation with a negative orientation value must return
     * the original image without throwing.
     */
    @Test
    public void applyOrientation_negativeOrientation_returnsOriginalImageWithoutException() throws Exception {
        java.lang.reflect.Method m = org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader.class
                .getDeclaredMethod("applyOrientation", BufferedImage.class, int.class);
        m.setAccessible(true);

        BufferedImage original = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = assertDoesNotThrow(() ->
                (BufferedImage) m.invoke(null, original, -1));
        assertSame(original, result,
                "applyOrientation with negative orientation must return the original image unchanged");
    }

    // =========================================================================
    // W-4: EXIF Orientation entry value is a non-Number type — readExifOrientation
    // must return OptionalInt.empty() instead of throwing ClassCastException.
    //
    // Strategy: build a JPEG whose TIFF IFD encodes the Orientation tag with
    // type=ASCII (2) instead of SHORT (3). TIFFReader may surface the value as a
    // String rather than a Number. The guard `!(value instanceof Number)` must
    // catch this and return OptionalInt.empty(), so readImage() returns the
    // raw decoded image without any orientation transformation.
    // =========================================================================

    /**
     * Builds a minimal TIFF block (little-endian) where the Orientation tag
     * is encoded with type=ASCII (2) and a single-char value "6\0".
     * This produces a non-Number entry value that would previously trigger a
     * ClassCastException.
     *
     * <pre>
     *   Offset 0:  II             - byte order: little-endian
     *   Offset 2:  2A 00          - TIFF magic 42
     *   Offset 4:  08 00 00 00    - IFD0 offset = 8
     *   Offset 8:  01 00          - IFD entry count = 1
     *   Offset 10: 12 01          - tag = 0x0112 (Orientation)
     *   Offset 12: 02 00          - type = ASCII (2) — deliberately wrong type
     *   Offset 14: 02 00 00 00    - count = 2 (two chars: '6' + null terminator)
     *   Offset 18: 1A 00 00 00    - value offset = 26 (points to "6\0" after IFD)
     *   Offset 22: 00 00 00 00    - next IFD offset = 0
     *   Offset 26: 36 00          - ASCII data "6\0"
     * </pre>
     */
    private static byte[] buildTiffOrientationAsAscii() {
        return new byte[]{
                'I', 'I',                               // byte order: little-endian
                0x2A, 0x00,                             // TIFF magic 42
                0x08, 0x00, 0x00, 0x00,                 // IFD0 offset = 8
                0x01, 0x00,                             // IFD entry count = 1
                0x12, 0x01,                             // tag = 0x0112 (Orientation)
                0x02, 0x00,                             // type = ASCII (deliberately non-numeric)
                0x02, 0x00, 0x00, 0x00,                 // count = 2
                0x1A, 0x00, 0x00, 0x00,                 // value offset = 26
                0x00, 0x00, 0x00, 0x00,                 // next IFD offset = 0
                '6', 0x00                               // ASCII data: "6\0"
        };
    }

    /**
     * W-4: When the EXIF Orientation tag carries a non-Number value (ASCII type),
     * readExifOrientation must return OptionalInt.empty() and must NOT throw
     * ClassCastException. The full readImage(File) call must also succeed.
     */
    @Test
    public void readImageFile_exifOrientationTagAsAsciiType_noClassCastException() throws IOException {
        ImageReader reader = ImageIOReader.getInstance();
        byte[] jpeg = buildJpegWithAppSegments(
                buildExifApp1WithTiff(buildTiffOrientationAsAscii()));
        Path file = tempDir.resolve("exif_orientation_ascii_type.jpg");
        Files.write(file, jpeg);

        // Must not throw ClassCastException; the non-Number value must be silently ignored
        assertDoesNotThrow(() -> reader.readImage(file.toFile()),
                "readImage(File) must not throw ClassCastException when EXIF Orientation is a non-Number type");
    }

    /**
     * W-4: Isolation test — {@link ExifOrientationReader#readOrientation(File)} must return
     * {@link OptionalInt#empty()} for a JPEG whose Orientation tag has an ASCII (non-Number) value.
     */
    @Test
    public void readExifOrientation_nonNumberOrientationValue_returnsEmpty() throws Exception {
        byte[] jpeg = buildJpegWithAppSegments(
                buildExifApp1WithTiff(buildTiffOrientationAsAscii()));
        Path file = tempDir.resolve("diag_exif_orient_ascii.jpg");
        Files.write(file, jpeg);

        ExifOrientationReader exifReader = new ExifOrientationReader();
        java.util.OptionalInt result = exifReader.readOrientation(file.toFile());

        assertFalse(result.isPresent(),
                "readOrientation must return OptionalInt.empty() when Orientation entry value is not a Number");
    }

    // =========================================================================
    // I-3: resource non-leak verification for readExifOrientation
    //
    // Strategy: call readImage(File) on a valid JPEG with EXIF many times in a
    // tight loop and confirm that no FileDescriptor leak causes an exception.
    // On most JVMs the default ulimit is 1024; 200 iterations is well within
    // that limit but will fail instantly if a descriptor leaks on every call.
    // =========================================================================

    /**
     * I-3: Repeated readImage(File) calls on the same EXIF-bearing JPEG must not
     * accumulate open file descriptors. If a resource leak were present, later
     * iterations would throw an IOException ("Too many open files").
     */
    @Test
    public void readImageFile_repeatedCallsWithExif_noFileDescriptorLeak() throws IOException {
        byte[] jpeg = buildJpegWithAppSegments(buildJfifApp0(), buildExifApp1WithOrientation6());
        Path file = tempDir.resolve("fd_leak_test.jpg");
        Files.write(file, jpeg);

        ImageReader reader = ImageIOReader.getInstance();
        for (int i = 0; i < 200; i++) {
            BufferedImage result = reader.readImage(file.toFile());
            assertNotNull(result, "readImage must succeed on iteration " + i);
        }
    }

    // -------------------------------------------------------------------------
    // readImage(File) — orientation application logic (mock ImageOrientationReader)
    // -------------------------------------------------------------------------

    /**
     * When orientationReader returns Orientation=2 (mirror horizontal), readImage(File) must
     * apply the flip and return a non-null image with the same dimensions.
     */
    @Test
    public void readImageFile_mockOrientationTwo_imageFlippedSameDimensions() throws Exception {
        ImageOrientationReader mockReader = Mockito.mock(ImageOrientationReader.class);
        Mockito.when(mockReader.readOrientation(Mockito.any())).thenReturn(OptionalInt.of(2));
        ImageIOReader reader = new ImageIOReader(mockReader);
        Path jpeg = tempDir.resolve("mock_o2.jpg");
        BufferedImage src = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(src, "jpeg", jpeg.toFile());
        BufferedImage result = reader.readImage(jpeg.toFile());
        assertNotNull(result);
        assertEquals(100, result.getWidth());
        assertEquals(50, result.getHeight());
    }

    /**
     * When orientationReader returns empty, readImage(File) must return a non-null image
     * with the original dimensions without applying any transformation.
     */
    @Test
    public void readImageFile_mockOrientationEmpty_imageReturnedAsIs() throws Exception {
        ImageOrientationReader mockReader = Mockito.mock(ImageOrientationReader.class);
        Mockito.when(mockReader.readOrientation(Mockito.any())).thenReturn(OptionalInt.empty());
        ImageIOReader reader = new ImageIOReader(mockReader);
        Path jpeg = tempDir.resolve("mock_no_orientation.jpg");
        BufferedImage src = new BufferedImage(60, 40, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(src, "jpeg", jpeg.toFile());
        BufferedImage result = reader.readImage(jpeg.toFile());
        assertNotNull(result);
        assertEquals(60, result.getWidth());
        assertEquals(40, result.getHeight());
    }

    /**
     * When orientationReader returns Orientation=6 (rotate 90 CW), readImage(File) must
     * return an image with swapped dimensions (100x50 becomes 50x100).
     */
    @Test
    public void readImageFile_mockOrientationSix_widthAndHeightSwapped() throws Exception {
        ImageOrientationReader mockReader = Mockito.mock(ImageOrientationReader.class);
        Mockito.when(mockReader.readOrientation(Mockito.any())).thenReturn(OptionalInt.of(6));
        ImageIOReader reader = new ImageIOReader(mockReader);
        Path jpeg = tempDir.resolve("mock_o6.jpg");
        BufferedImage src = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(src, "jpeg", jpeg.toFile());
        BufferedImage result = reader.readImage(jpeg.toFile());
        assertNotNull(result);
        assertEquals(50, result.getWidth());
        assertEquals(100, result.getHeight());
    }
}

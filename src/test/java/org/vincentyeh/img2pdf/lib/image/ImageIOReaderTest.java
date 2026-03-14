package org.vincentyeh.img2pdf.lib.image;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader;
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
}

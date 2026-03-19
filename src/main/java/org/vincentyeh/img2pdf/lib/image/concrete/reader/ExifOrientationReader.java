package org.vincentyeh.img2pdf.lib.image.concrete.reader;

import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFReader;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageOrientationReader;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.OptionalInt;

/**
 * Concrete implementation of {@link ImageOrientationReader} that reads the EXIF
 * Orientation tag from JPEG and TIFF image files using TwelveMonkeys TIFFReader.
 *
 * <p>Supports JPEG (SOI magic 0xFFD8) and TIFF (little-endian 0x4949 / big-endian 0x4D4D)
 * formats via magic byte detection. For JPEG files, scans APP markers to locate the
 * APP1 EXIF segment before delegating to {@link TIFFReader}.</p>
 */
public final class ExifOrientationReader implements ImageOrientationReader {

    /**
     * {@inheritDoc}
     *
     * <p>Opens an {@link ImageInputStream} from the file, detects its format by magic bytes,
     * positions the stream at the embedded TIFF header, and delegates to {@link TIFFReader}
     * to extract the Orientation tag value.</p>
     */
    @Override
    public OptionalInt readOrientation(File file) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            if (iis == null) return OptionalInt.empty();
            if (!positionAtExifTiff(iis)) return OptionalInt.empty();

            // TIFFReader interprets IFD offsets as absolute stream positions via seek().
            // Wrap the remaining stream so TIFF data appears to start at position 0,
            // ensuring seek(ifdOffset) lands at the correct position within the TIFF block.
            final ImageInputStream src = iis;
            try (ImageInputStream tiffStream = new javax.imageio.stream.MemoryCacheImageInputStream(
                    new InputStream() {
                        @Override public int read() throws IOException { return src.read(); }
                        @Override public int read(byte[] b, int off, int len) throws IOException {
                            return src.read(b, off, len);
                        }
                    })) {

                Directory dir = new TIFFReader().read(tiffStream);
                Directory ifd0 = (dir instanceof CompoundDirectory)
                        ? ((CompoundDirectory) dir).getDirectory(0) : dir;

                Entry entry = ifd0.getEntryById(TIFF.TAG_ORIENTATION);
                if (entry == null) return OptionalInt.empty();
                // Guard against null or unexpected type to avoid NPE / ClassCastException
                Object value = entry.getValue();
                if (!(value instanceof Number)) return OptionalInt.empty();
                return OptionalInt.of(((Number) value).intValue());
            }
        }
    }

    /**
     * Positions the stream at the start of the TIFF header containing EXIF data.
     * Detects format by magic bytes: 0xFFD8=JPEG, 0x4949/0x4D4D=TIFF.
     *
     * @param iis the image input stream to inspect; must not be {@code null}
     * @return {@code true} if the stream is successfully positioned at a TIFF header,
     *         {@code false} if the format is not recognized or no EXIF data is found
     * @throws IOException if an I/O error occurs while reading the stream
     */
    private static boolean positionAtExifTiff(ImageInputStream iis) throws IOException {
        long startPos = iis.getStreamPosition();
        int magic = iis.readUnsignedShort();

        if (magic == 0xFFD8) {
            return seekJpegExifTiff(iis);
        } else if (magic == 0x4949 || magic == 0x4D4D) {
            iis.seek(startPos);
            return true;
        }
        return false;
    }

    /**
     * Scans JPEG markers to find the APP1 EXIF segment,
     * positioning the stream at the embedded TIFF header.
     *
     * @param iis the image input stream positioned immediately after the SOI marker; must not be {@code null}
     * @return {@code true} if the stream is positioned at the TIFF header within the APP1 EXIF segment,
     *         {@code false} if the EXIF segment is not found before the end-of-image marker
     * @throws IOException if an I/O error occurs while scanning the JPEG markers
     */
    private static boolean seekJpegExifTiff(ImageInputStream iis) throws IOException {
        try {
            while (true) {
                // Read marker with padding support: JPEG spec allows any number of 0xFF fill bytes
                // before the actual marker byte. Consume fill bytes until a non-0xFF byte is found.
                int b = iis.readUnsignedByte();
                if (b != 0xFF) {
                    // Not at a marker boundary; stream is corrupt or misaligned
                    return false;
                }
                // Skip additional 0xFF padding bytes
                int markerByte;
                do {
                    markerByte = iis.readUnsignedByte();
                } while (markerByte == 0xFF);

                int marker = 0xFF00 | markerByte;

                // Standalone markers (no length field): SOI, EOI, RST0-RST7
                // These markers must be handled before reading the length field
                if (marker == 0xFFD8) {
                    // SOI — skip, continue scanning
                    continue;
                }
                if (marker == 0xFFD9 || marker == 0xFFDA) {
                    // EOI or SOS — EXIF is always in the header before SOS; stop scanning
                    return false;
                }
                if (marker >= 0xFFD0 && marker <= 0xFFD7) {
                    // RST0-RST7 — standalone restart markers, no length field
                    continue;
                }

                int length = iis.readUnsignedShort();

//                APP1:EXIF
                if (marker == 0xFFE1) {
                    // Guard against corrupted APP1 segment: length field must be at least 8
                    // (2 bytes for length itself + 6 bytes for "Exif\0\0" header)
                    // segmentEnd marks the stream position right after this segment's payload.
                    long segmentEnd = iis.getStreamPosition() + (length - 2);
                    if (length < 8) {
                        // Skip the segment body to advance the cursor correctly
                        iis.seek(segmentEnd);
                        continue;
                    }
                    byte[] header = new byte[6];
                    iis.readFully(header);
                    if (header[0] == 'E' && header[1] == 'x' && header[2] == 'i'
                            && header[3] == 'f' && header[4] == 0 && header[5] == 0) {
                        return true;
                    }
                    // Non-EXIF APP1: seek to end of segment (avoids skipBytes partial-skip issue)
                    iis.seek(segmentEnd);

                } else {
                    // APP0 (JFIF) and other unknown markers: skip segment and continue
                    iis.seek(iis.getStreamPosition() + (length - 2));
                }
            }
        } catch (java.io.EOFException e) {
            // JPEG has no EXIF segment; treat as no orientation
            return false;
        }
    }
}

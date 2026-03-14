/**
 * ImageIOReaderOrientationPixelTest.java
 *
 * Pixel-level verification tests for all 8 EXIF Orientation values in {@link ImageIOReader}.
 * Uses a mock {@link ImageOrientationReader} to inject each orientation without requiring
 * real EXIF metadata. A 2x2 PNG with four distinct corner colors is used as the source image
 * to make geometric transformations unambiguous.
 */
package org.vincentyeh.img2pdf.lib.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageOrientationReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pixel-level verification tests for EXIF Orientation correction in {@link ImageIOReader}.
 *
 * <p>Each test injects a mock {@link ImageOrientationReader} that returns a fixed
 * orientation value, then verifies that every pixel of the 2x2 result image
 * is in the position expected after the corresponding geometric transformation.</p>
 *
 * <p>Source image layout (before transformation):
 * <pre>
 *   TL(0,0)=RED    TR(1,0)=GREEN
 *   BL(0,1)=BLUE   BR(1,1)=WHITE
 * </pre>
 * </p>
 */
public class ImageIOReaderOrientationPixelTest {

    @TempDir
    Path tempDir;

    /** Red in RGB (no alpha). */
    private static final int RED   = 0xFF0000;
    /** Green in RGB (no alpha). */
    private static final int GREEN = 0x00FF00;
    /** Blue in RGB (no alpha). */
    private static final int BLUE  = 0x0000FF;
    /** White in RGB (no alpha). */
    private static final int WHITE = 0xFFFFFF;

    /**
     * Extracts only the RGB channels (strips alpha) from a pixel at (x, y).
     *
     * @param img the source image
     * @param x   column index
     * @param y   row index
     * @return 24-bit RGB value
     */
    private static int rgb(BufferedImage img, int x, int y) {
        return img.getRGB(x, y) & 0x00FFFFFF;
    }

    /**
     * Creates a 2x2 PNG file with four distinct corner colors:
     * TL=RED, TR=GREEN, BL=BLUE, BR=WHITE.
     *
     * @return the written PNG {@link File}
     * @throws IOException if writing fails
     */
    private File createSourceImage() throws IOException {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, RED   | 0xFF000000);
        img.setRGB(1, 0, GREEN | 0xFF000000);
        img.setRGB(0, 1, BLUE  | 0xFF000000);
        img.setRGB(1, 1, WHITE);
        File f = tempDir.resolve("source.png").toFile();
        ImageIO.write(img, "png", f);
        return f;
    }

    /**
     * Creates an {@link ImageIOReader} whose orientation mock always returns the given value.
     *
     * @param orientation EXIF orientation value (1-8)
     * @return configured {@link ImageIOReader}
     * @throws IOException if mock setup fails
     */
    private ImageIOReader readerWithOrientation(int orientation) throws IOException {
        ImageOrientationReader mock = Mockito.mock(ImageOrientationReader.class);
        Mockito.when(mock.readOrientation(Mockito.any())).thenReturn(OptionalInt.of(orientation));
        return new ImageIOReader(mock);
    }

    /**
     * Creates an {@link ImageIOReader} whose orientation mock always returns empty (no orientation).
     *
     * @return configured {@link ImageIOReader}
     * @throws IOException if mock setup fails
     */
    private ImageIOReader readerWithNoOrientation() throws IOException {
        ImageOrientationReader mock = Mockito.mock(ImageOrientationReader.class);
        Mockito.when(mock.readOrientation(Mockito.any())).thenReturn(OptionalInt.empty());
        return new ImageIOReader(mock);
    }

    // -------------------------------------------------------------------------
    // Orientation 1: Horizontal (normal) — no transformation
    // Expected:
    //   (0,0)=R  (1,0)=G
    //   (0,1)=B  (1,1)=W
    // -------------------------------------------------------------------------

    /** Orientation 1 must leave all pixels unchanged. */
    @Test
    public void applyOrientation1_noTransform_pixelsUnchanged() throws Exception {
        File src = createSourceImage();
        BufferedImage result = readerWithOrientation(1).readImage(src);

        assertNotNull(result);
        assertEquals(2, result.getWidth());
        assertEquals(2, result.getHeight());
        assertEquals(RED,   rgb(result, 0, 0), "orientation 1: (0,0) should be RED");
        assertEquals(GREEN, rgb(result, 1, 0), "orientation 1: (1,0) should be GREEN");
        assertEquals(BLUE,  rgb(result, 0, 1), "orientation 1: (0,1) should be BLUE");
        assertEquals(WHITE, rgb(result, 1, 1), "orientation 1: (1,1) should be WHITE");
    }

    // -------------------------------------------------------------------------
    // Orientation 2: Mirror horizontal — flip left-right
    // Expected:
    //   (0,0)=G  (1,0)=R
    //   (0,1)=W  (1,1)=B
    // -------------------------------------------------------------------------

    /** Orientation 2 (flip horizontal) must swap left and right columns. */
    @Test
    public void applyOrientation2_flipHorizontal_leftRightSwapped() throws Exception {
        File src = createSourceImage();
        BufferedImage result = readerWithOrientation(2).readImage(src);

        assertNotNull(result);
        assertEquals(2, result.getWidth());
        assertEquals(2, result.getHeight());
        assertEquals(GREEN, rgb(result, 0, 0), "orientation 2: (0,0) should be GREEN (was TR)");
        assertEquals(RED,   rgb(result, 1, 0), "orientation 2: (1,0) should be RED (was TL)");
        assertEquals(WHITE, rgb(result, 0, 1), "orientation 2: (0,1) should be WHITE (was BR)");
        assertEquals(BLUE,  rgb(result, 1, 1), "orientation 2: (1,1) should be BLUE (was BL)");
    }

    // -------------------------------------------------------------------------
    // Orientation 3: Rotate 180°
    // Expected:
    //   (0,0)=W  (1,0)=B
    //   (0,1)=G  (1,1)=R
    // -------------------------------------------------------------------------

    /** Orientation 3 (rotate 180 degrees) must place BR at TL and vice-versa. */
    @Test
    public void applyOrientation3_rotate180_allCornersFlipped() throws Exception {
        File src = createSourceImage();
        BufferedImage result = readerWithOrientation(3).readImage(src);

        assertNotNull(result);
        assertEquals(2, result.getWidth());
        assertEquals(2, result.getHeight());
        assertEquals(WHITE, rgb(result, 0, 0), "orientation 3: (0,0) should be WHITE (was BR)");
        assertEquals(BLUE,  rgb(result, 1, 0), "orientation 3: (1,0) should be BLUE (was BL)");
        assertEquals(GREEN, rgb(result, 0, 1), "orientation 3: (0,1) should be GREEN (was TR)");
        assertEquals(RED,   rgb(result, 1, 1), "orientation 3: (1,1) should be RED (was TL)");
    }

    // -------------------------------------------------------------------------
    // Orientation 4: Mirror vertical — flip top-bottom
    // Expected:
    //   (0,0)=B  (1,0)=W
    //   (0,1)=R  (1,1)=G
    // -------------------------------------------------------------------------

    /** Orientation 4 (flip vertical) must swap top and bottom rows. */
    @Test
    public void applyOrientation4_flipVertical_topBottomSwapped() throws Exception {
        File src = createSourceImage();
        BufferedImage result = readerWithOrientation(4).readImage(src);

        assertNotNull(result);
        assertEquals(2, result.getWidth());
        assertEquals(2, result.getHeight());
        assertEquals(BLUE,  rgb(result, 0, 0), "orientation 4: (0,0) should be BLUE (was BL)");
        assertEquals(WHITE, rgb(result, 1, 0), "orientation 4: (1,0) should be WHITE (was BR)");
        assertEquals(RED,   rgb(result, 0, 1), "orientation 4: (0,1) should be RED (was TL)");
        assertEquals(GREEN, rgb(result, 1, 1), "orientation 4: (1,1) should be GREEN (was TR)");
    }

    // -------------------------------------------------------------------------
    // Orientation 5: Mirror horizontal + Rotate 270° CW
    // Expected:
    //   (0,0)=R  (1,0)=B
    //   (0,1)=G  (1,1)=W
    // -------------------------------------------------------------------------

    /** Orientation 5 (flip H then rotate 270 CW) must produce correct pixel layout. */
    @Test
    public void applyOrientation5_flipHThenRotate270_correctPixels() throws Exception {
        File src = createSourceImage();
        BufferedImage result = readerWithOrientation(5).readImage(src);

        assertNotNull(result);
        assertEquals(2, result.getWidth());
        assertEquals(2, result.getHeight());
        assertEquals(RED,   rgb(result, 0, 0), "orientation 5: (0,0) should be RED");
        assertEquals(BLUE,  rgb(result, 1, 0), "orientation 5: (1,0) should be BLUE");
        assertEquals(GREEN, rgb(result, 0, 1), "orientation 5: (0,1) should be GREEN");
        assertEquals(WHITE, rgb(result, 1, 1), "orientation 5: (1,1) should be WHITE");
    }

    // -------------------------------------------------------------------------
    // Orientation 6: Rotate 90° CW
    // Expected:
    //   (0,0)=B  (1,0)=R
    //   (0,1)=W  (1,1)=G
    // -------------------------------------------------------------------------

    /** Orientation 6 (rotate 90 degrees CW) must place BL at TL and TR at BR. */
    @Test
    public void applyOrientation6_rotate90CW_correctPixels() throws Exception {
        File src = createSourceImage();
        BufferedImage result = readerWithOrientation(6).readImage(src);

        assertNotNull(result);
        assertEquals(2, result.getWidth());
        assertEquals(2, result.getHeight());
        assertEquals(BLUE,  rgb(result, 0, 0), "orientation 6: (0,0) should be BLUE (was BL)");
        assertEquals(RED,   rgb(result, 1, 0), "orientation 6: (1,0) should be RED (was TL)");
        assertEquals(WHITE, rgb(result, 0, 1), "orientation 6: (0,1) should be WHITE (was BR)");
        assertEquals(GREEN, rgb(result, 1, 1), "orientation 6: (1,1) should be GREEN (was TR)");
    }

    // -------------------------------------------------------------------------
    // Orientation 7: Mirror horizontal + Rotate 90° CW
    // Expected:
    //   (0,0)=W  (1,0)=G
    //   (0,1)=B  (1,1)=R
    // -------------------------------------------------------------------------

    /** Orientation 7 (flip H then rotate 90 CW) must produce correct pixel layout. */
    @Test
    public void applyOrientation7_flipHThenRotate90_correctPixels() throws Exception {
        File src = createSourceImage();
        BufferedImage result = readerWithOrientation(7).readImage(src);

        assertNotNull(result);
        assertEquals(2, result.getWidth());
        assertEquals(2, result.getHeight());
        assertEquals(WHITE, rgb(result, 0, 0), "orientation 7: (0,0) should be WHITE");
        assertEquals(GREEN, rgb(result, 1, 0), "orientation 7: (1,0) should be GREEN");
        assertEquals(BLUE,  rgb(result, 0, 1), "orientation 7: (0,1) should be BLUE");
        assertEquals(RED,   rgb(result, 1, 1), "orientation 7: (1,1) should be RED");
    }

    // -------------------------------------------------------------------------
    // Orientation 8: Rotate 270° CW
    // Expected:
    //   (0,0)=G  (1,0)=W
    //   (0,1)=R  (1,1)=B
    // -------------------------------------------------------------------------

    /** Orientation 8 (rotate 270 degrees CW) must place TR at TL and TL at BL. */
    @Test
    public void applyOrientation8_rotate270CW_correctPixels() throws Exception {
        File src = createSourceImage();
        BufferedImage result = readerWithOrientation(8).readImage(src);

        assertNotNull(result);
        assertEquals(2, result.getWidth());
        assertEquals(2, result.getHeight());
        assertEquals(GREEN, rgb(result, 0, 0), "orientation 8: (0,0) should be GREEN (was TR)");
        assertEquals(WHITE, rgb(result, 1, 0), "orientation 8: (1,0) should be WHITE (was BR)");
        assertEquals(RED,   rgb(result, 0, 1), "orientation 8: (0,1) should be RED (was TL)");
        assertEquals(BLUE,  rgb(result, 1, 1), "orientation 8: (1,1) should be BLUE (was BL)");
    }

    // -------------------------------------------------------------------------
    // No orientation — mock returns empty OptionalInt → identity pass-through
    // -------------------------------------------------------------------------

    /** When mock returns no orientation, pixels must be identical to the original image. */
    @Test
    public void applyOrientation_noOrientationFromMock_pixelsUnchanged() throws Exception {
        File src = createSourceImage();
        BufferedImage result = readerWithNoOrientation().readImage(src);

        assertNotNull(result);
        assertEquals(RED,   rgb(result, 0, 0), "no orientation: (0,0) should be RED");
        assertEquals(GREEN, rgb(result, 1, 0), "no orientation: (1,0) should be GREEN");
        assertEquals(BLUE,  rgb(result, 0, 1), "no orientation: (0,1) should be BLUE");
        assertEquals(WHITE, rgb(result, 1, 1), "no orientation: (1,1) should be WHITE");
    }
}

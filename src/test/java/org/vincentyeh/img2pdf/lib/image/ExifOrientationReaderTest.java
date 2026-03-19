package org.vincentyeh.img2pdf.lib.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vincentyeh.img2pdf.lib.image.concrete.reader.ExifOrientationReader;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

public class ExifOrientationReaderTest {

    @TempDir
    Path tempDir;

    private final ExifOrientationReader reader = new ExifOrientationReader();

    private File createLandscapeJpeg(String name) throws IOException {
        BufferedImage img = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 50, 50);
        g.setColor(Color.RED);
        g.fillRect(50, 0, 50, 50);
        g.dispose();
        File f = tempDir.resolve(name).toFile();
        ImageIO.write(img, "jpeg", f);
        return f;
    }

    private static void exiftool(String... args) throws IOException, InterruptedException {
        String[] cmd = new String[args.length + 1];
        cmd[0] = "exiftool";
        System.arraycopy(args, 0, cmd, 1, args.length);
        Process p = Runtime.getRuntime().exec(cmd);
        if (p.waitFor() != 0) {
            throw new IOException("exiftool failed, exit=" + p.exitValue());
        }
    }

    // Scenario 1: no EXIF
    @Test
    void readOrientation_jpegNoExif_returnsEmpty() throws Exception {
        File f = createLandscapeJpeg("no_exif.jpg");
        exiftool("-all=", "-overwrite_original", f.getAbsolutePath());
        OptionalInt result = reader.readOrientation(f);
        assertFalse(result.isPresent(), "no EXIF → should return empty");
    }

    // Scenario 2: pure EXIF (no JFIF), orientation 1~8
    @Test
    void readOrientation_exifOnlyOrientation1_returns1() throws Exception {
        File f = createLandscapeJpeg("exif_only_o1.jpg");
        exiftool("-JFIF:all=", "-overwrite_original", f.getAbsolutePath());
        exiftool("-Orientation=1", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(1, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_exifOnlyOrientation2_returns2() throws Exception {
        File f = createLandscapeJpeg("exif_only_o2.jpg");
        exiftool("-JFIF:all=", "-overwrite_original", f.getAbsolutePath());
        exiftool("-Orientation=2", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(2, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_exifOnlyOrientation3_returns3() throws Exception {
        File f = createLandscapeJpeg("exif_only_o3.jpg");
        exiftool("-JFIF:all=", "-overwrite_original", f.getAbsolutePath());
        exiftool("-Orientation=3", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(3, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_exifOnlyOrientation4_returns4() throws Exception {
        File f = createLandscapeJpeg("exif_only_o4.jpg");
        exiftool("-JFIF:all=", "-overwrite_original", f.getAbsolutePath());
        exiftool("-Orientation=4", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(4, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_exifOnlyOrientation5_returns5() throws Exception {
        File f = createLandscapeJpeg("exif_only_o5.jpg");
        exiftool("-JFIF:all=", "-overwrite_original", f.getAbsolutePath());
        exiftool("-Orientation=5", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(5, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_exifOnlyOrientation6_returns6() throws Exception {
        File f = createLandscapeJpeg("exif_only_o6.jpg");
        exiftool("-JFIF:all=", "-overwrite_original", f.getAbsolutePath());
        exiftool("-Orientation=6", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(6, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_exifOnlyOrientation7_returns7() throws Exception {
        File f = createLandscapeJpeg("exif_only_o7.jpg");
        exiftool("-JFIF:all=", "-overwrite_original", f.getAbsolutePath());
        exiftool("-Orientation=7", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(7, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_exifOnlyOrientation8_returns8() throws Exception {
        File f = createLandscapeJpeg("exif_only_o8.jpg");
        exiftool("-JFIF:all=", "-overwrite_original", f.getAbsolutePath());
        exiftool("-Orientation=8", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(8, reader.readOrientation(f).getAsInt());
    }

    // Scenario 3: JFIF + EXIF, orientation 1~8
    @Test
    void readOrientation_jfifPlusExifOrientation1_returns1() throws Exception {
        File f = createLandscapeJpeg("jfif_exif_o1.jpg");
        exiftool("-Orientation=1", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(1, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_jfifPlusExifOrientation2_returns2() throws Exception {
        File f = createLandscapeJpeg("jfif_exif_o2.jpg");
        exiftool("-Orientation=2", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(2, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_jfifPlusExifOrientation3_returns3() throws Exception {
        File f = createLandscapeJpeg("jfif_exif_o3.jpg");
        exiftool("-Orientation=3", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(3, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_jfifPlusExifOrientation4_returns4() throws Exception {
        File f = createLandscapeJpeg("jfif_exif_o4.jpg");
        exiftool("-Orientation=4", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(4, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_jfifPlusExifOrientation5_returns5() throws Exception {
        File f = createLandscapeJpeg("jfif_exif_o5.jpg");
        exiftool("-Orientation=5", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(5, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_jfifPlusExifOrientation6_returns6() throws Exception {
        File f = createLandscapeJpeg("jfif_exif_o6.jpg");
        exiftool("-Orientation=6", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(6, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_jfifPlusExifOrientation7_returns7() throws Exception {
        File f = createLandscapeJpeg("jfif_exif_o7.jpg");
        exiftool("-Orientation=7", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(7, reader.readOrientation(f).getAsInt());
    }

    @Test
    void readOrientation_jfifPlusExifOrientation8_returns8() throws Exception {
        File f = createLandscapeJpeg("jfif_exif_o8.jpg");
        exiftool("-Orientation=8", "-n", "-overwrite_original", f.getAbsolutePath());
        assertEquals(8, reader.readOrientation(f).getAsInt());
    }
}

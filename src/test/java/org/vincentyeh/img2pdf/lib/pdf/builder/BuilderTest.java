package org.vincentyeh.img2pdf.lib.pdf.builder;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.pdf.concrete.builder.PDFBoxBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.awt.image.BufferedImage;
import java.io.File;

public class BuilderTest {
    @Test
    public void TestNullSetting() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new PDFBoxBuilder(null));
    }

    @Test
    public void NullArgumentTest() {
        PDFBuilder builder = new PDFBoxBuilder(MemoryUsageSetting.setupMainMemoryOnly());
        builder.createDocument();

        Assertions.assertDoesNotThrow(() -> builder.setOwnerPassword(null));
        Assertions.assertDoesNotThrow(() -> builder.setUserPassword(null));

        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.setInfo(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.setPermission(null));

        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.addImage(-1, null, null, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.addImage(0, null, null, null));

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        SizeF size = Mockito.mock(SizeF.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.addImage(0, image, null, null));

        builder.addPage(new SizeF(100, 100));
        Assertions.assertDoesNotThrow(() -> builder.addImage(0, image, new PointF(), size));
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.addImage(0, image, null, size));
    }

    @Test
    public void testInfo() {
        PDFBoxBuilder builder = new PDFBoxBuilder(MemoryUsageSetting.setupMainMemoryOnly());
        builder.createDocument();
        PDDocument document = builder.getDocument();
        PDFDocumentInfo documentInfo = new PDFDocumentInfo();
        documentInfo.Title = "Hello";
        documentInfo.Author = "Hello2";
        documentInfo.Creator = "Hello3";
        documentInfo.Subject = "Hello4";
        documentInfo.Producer = "Hello5";

        builder.setInfo(documentInfo);

        PDDocumentInformation information = document.getDocumentInformation();
        Assertions.assertEquals(documentInfo.Title, information.getTitle());
        Assertions.assertEquals(documentInfo.Author, information.getAuthor());
        Assertions.assertEquals(documentInfo.Creator, information.getCreator());
        Assertions.assertEquals(documentInfo.Subject, information.getSubject());
        Assertions.assertEquals(documentInfo.Producer, information.getProducer());

    }

    @Test
    public void testPermission() {
        PDFBoxBuilder builder = new PDFBoxBuilder(MemoryUsageSetting.setupMainMemoryOnly());
        builder.createDocument();
        Permission permission = new Permission();
        permission.CanAssembleDocument = false;
        permission.CanExtractContent = false;
        permission.CanModify = false;
        permission.CanPrint = false;
        permission.CanModifyAnnotations = false;
        permission.CanFillInForm = false;
        permission.CanPrintDegraded = false;
        permission.CanExtractForAccessibility = false;
        builder.setPermission(permission);

        AccessPermission ap = builder.getPermission();

        Assertions.assertFalse(ap.canAssembleDocument());
        Assertions.assertFalse(ap.canExtractContent());
        Assertions.assertFalse(ap.canModify());
        Assertions.assertFalse(ap.canPrint());
        Assertions.assertFalse(ap.canModifyAnnotations());
        Assertions.assertFalse(ap.canFillInForm());
        Assertions.assertFalse(ap.canPrintDegraded());
        Assertions.assertFalse(ap.canExtractForAccessibility());


    }

    @Test
    public void noPageTest() {
        PDFBoxBuilder builder = new PDFBoxBuilder(MemoryUsageSetting.setupMainMemoryOnly());
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        builder.createDocument();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addImage(1, image, null, new SizeF(100, 100))
        );

    }

    @Test
    public void noDocumentTest() {
        PDFBoxBuilder builder = new PDFBoxBuilder(MemoryUsageSetting.setupMainMemoryOnly());
        Assertions.assertThrows(IllegalStateException.class,
                () -> builder.addPage(new SizeF(100, 100)));

    }

    @Test
    public void noEnoughPage() {
        PDFBoxBuilder builder = new PDFBoxBuilder(MemoryUsageSetting.setupMainMemoryOnly());
        builder.createDocument();
        builder.addPage(new SizeF(100, 100));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> builder.addImage(1,
                        new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB),
                        null,
                        new SizeF(100, 100)));

        builder.addPage(new SizeF(100, 100));
        Assertions.assertDoesNotThrow(
                () -> builder.addImage(1,
                        new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB),
                        new PointF(),
                        new SizeF(100, 100)));


    }

    @Test
    public void saveTest() {
        PDFBoxBuilder builder = new PDFBoxBuilder(MemoryUsageSetting.setupMainMemoryOnly());
        Assertions.assertThrows(IllegalStateException.class, () -> builder.save(new File("AAA")));
        builder.createDocument();
    }

}

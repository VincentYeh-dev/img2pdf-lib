package org.vincentyeh.img2pdf.pdf.concrete.builder;

import com.drew.lang.annotations.NotNull;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.img2pdf.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.pdf.parameter.Permission;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PDFBoxBuilder implements PDFBuilder {

    private final List<PDPage> pages = new LinkedList<>();
    private PDDocument document;
    private AccessPermission permission = new AccessPermission();
    private String ownerPassword;
    private String userPassword;

    private final MemoryUsageSetting memoryUsageSetting;

    public PDFBoxBuilder(MemoryUsageSetting memoryUsageSetting) {
        try {
            this.memoryUsageSetting = Objects.requireNonNull(memoryUsageSetting);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void createDocument() {
        document = new PDDocument(this.memoryUsageSetting);
    }

    @Override
    public void setOwnerPassword(String ownerPassword) {
        this.ownerPassword = ownerPassword;
    }

    @Override
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public void setInfo(PDFDocumentInfo info) {
        if (info == null)
            throw new IllegalArgumentException("info == null");

        PDDocumentInformation information = new PDDocumentInformation();
        information.setTitle(info.Title);
        information.setAuthor(info.Author);
        information.setSubject(info.Subject);
        information.setCreator(info.Creator);
        information.setProducer(info.Producer);
        document.setDocumentInformation(information);
    }

    @Override
    public void setPermission(Permission permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission==null");
        }

        this.permission.setCanAssembleDocument(permission.CanAssembleDocument);
        this.permission.setCanExtractContent(permission.CanExtractContent);
        this.permission.setCanExtractForAccessibility(permission.CanExtractForAccessibility);
        this.permission.setCanFillInForm(permission.CanFillInForm);
        this.permission.setCanModify(permission.CanModify);
        this.permission.setCanModifyAnnotations(permission.CanModifyAnnotations);
        this.permission.setCanPrint(permission.CanPrint);
        this.permission.setCanPrintDegraded(permission.CanPrintDegraded);
    }

    @Override
    public int addPage(SizeF size) {
        if (document == null)
            throw new IllegalStateException("document has not been created");
        if (size == null) throw new IllegalArgumentException("size==null");

        PDPage page = new PDPage();
        page.setMediaBox(new PDRectangle(size.width, size.height));
        pages.add(page);
        return pages.size() - 1;
    }

    @Override
    public void addImage(int index, @NotNull BufferedImage image, @NotNull PointF position, @NotNull SizeF size) throws Exception {
        if (document == null)
            throw new IllegalStateException("document has not been created");

        if (position == null)
            throw new IllegalArgumentException("position==null");

        if (pages.isEmpty())
            throw new IllegalArgumentException("No page has been created");

        if (index >= pages.size())
            throw new IllegalArgumentException(String.format("Illegal index:%d total:%d", index, pages.size()));

        if (image == null) throw new IllegalArgumentException("image==null");


        if (size == null) throw new IllegalArgumentException("size==null");


        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, image);
        PDPageContentStream contentStream = new PDPageContentStream(document, pages.get(index));
        contentStream.drawImage(pdImageXObject, position.x, position.y, size.width, size.height);
        contentStream.close();
    }

    @Override
    public void save(File destination) throws IOException {
        if (document == null)
            throw new IllegalStateException("document has not been created");

        if (ownerPassword != null && userPassword != null)
            document.protect(createProtectionPolicy(ownerPassword, userPassword, permission));
        for (PDPage page : pages)
            document.addPage(page);
        document.save(destination);
    }


    @Override
    public void reset() throws IOException {
        pages.clear();
        if (document != null)
            document.close();
        document = null;
        permission = new AccessPermission();
        ownerPassword = userPassword = null;
    }

    public PDDocument getDocument() {
        return document;
    }

    public List<PDPage> getPages() {
        return pages;
    }

    public AccessPermission getPermission() {
        return permission;
    }

    private static StandardProtectionPolicy createProtectionPolicy(String ownerPassword, String userPassword, AccessPermission permission) {
        // Define the length of the encryption key.
        // Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
        int keyLength = 128;
        StandardProtectionPolicy spp = new StandardProtectionPolicy(ownerPassword, userPassword, permission);
        spp.setEncryptionKeyLength(keyLength);
        return spp;
    }
}

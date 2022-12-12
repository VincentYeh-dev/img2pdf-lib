package org.vincentyeh.img2pdf.lib.pdf.concrete.builder;

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
import org.vincentyeh.img2pdf.lib.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

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

    private final MemoryUsageSetting setting;

    public PDFBoxBuilder(MemoryUsageSetting setting) {
        try{
            this.setting = Objects.requireNonNull(setting);
        }catch (NullPointerException e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void createDocument() {
        document = new PDDocument(this.setting);
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
        PDPage page = new PDPage();
        page.setMediaBox(new PDRectangle(size.width, size.height));
        pages.add(page);
        return pages.size() - 1;
    }

    @Override
    public void addImage(int index, BufferedImage image, PointF position, SizeF size) {
        try {
            PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, image);
            PDPageContentStream contentStream = new PDPageContentStream(document, pages.get(index));
            contentStream.drawImage(pdImageXObject, position.x, position.y, size.width, size.height);
            contentStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(File destination) throws IOException {
        if (ownerPassword != null && userPassword != null)
            document.protect(createProtectionPolicy());
        for (PDPage page : pages)
            document.addPage(page);
        document.save(destination);
        document.close();
    }


    @Override
    public void reset() {
        pages.clear();
        document = null;
        permission = new AccessPermission();
        ownerPassword = userPassword = null;
    }

    private StandardProtectionPolicy createProtectionPolicy() {
        // Define the length of the encryption key.
        // Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
        int keyLength = 128;
        StandardProtectionPolicy spp = new StandardProtectionPolicy(ownerPassword, userPassword, permission);
        spp.setEncryptionKeyLength(keyLength);
        return spp;
    }
}

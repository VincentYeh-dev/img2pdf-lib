package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class PDFBoxDocumentAdaptor implements IDocument {
    private final PDDocument document;
    private final Map<Integer, IPage> pages = new HashMap<>();
    private final DocumentArgument docArgument;


    public PDFBoxDocumentAdaptor(DocumentArgument argument) {
        this(argument, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public PDFBoxDocumentAdaptor(DocumentArgument argument, MemoryUsageSetting memoryUsageSetting) {
        if (argument == null)
            throw new IllegalArgumentException("argument==null");

        if (memoryUsageSetting == null)
            throw new IllegalArgumentException("memoryUsageSetting==null");

        document = new PDDocument(memoryUsageSetting);
        this.docArgument = argument;
    }

    @Override
    public void addPage(IPage page) {
        if (page == null)
            throw new IllegalArgumentException("page==null");

        if (pages.containsKey(page.getPageNumber()))
            throw new IllegalArgumentException("page number " + page.getPageNumber() + " already exists");

        pages.put(page.getPageNumber(), page);

    }

    @Override
    public void saveAndClose(OutputStream outputStream) throws IOException {
        if (document == null)
            throw new IllegalStateException("document has not been created");

        if (docArgument.isEncrypted())
            document.protect(
                    createProtectionPolicy(docArgument.getOwnerPassword(),
                            docArgument.getUserPassword(), convertPermission(docArgument.getPermission())));

        if(docArgument.hasInfo())
            setInfo(docArgument.getInfo());

        for (int i = 1; i <= pages.size(); i++) {
            IPage page = pages.get(i);
            if (page == null)
                throw new IllegalStateException("page with ID " + i + " does not exist");
            document.addPage(((PDFBoxPageAdaptor) page).getInternalPage());
        }
        document.save(outputStream);
        document.close();
    }

    @Override
    public void saveAndClose(File destination) throws IOException {
        if (destination == null)
            throw new IllegalArgumentException("destination==null");
        if (destination.exists() && !destination.canWrite())
            throw new IllegalArgumentException("destination is not writable");

        try (FileOutputStream fos = new FileOutputStream(destination)) {
            saveAndClose(fos);
        }
    }

    private void setInfo(PDFDocumentInfo info) {
        PDDocumentInformation information = new PDDocumentInformation();
        information.setTitle(info.Title);
        information.setAuthor(info.Author);
        information.setSubject(info.Subject);
        information.setCreator(info.Creator);
        information.setProducer(info.Producer);
        document.setDocumentInformation(information);
    }

    @Override
    public int getPageCount() {
        return pages.size();
    }

    private AccessPermission convertPermission(Permission permission) {
        AccessPermission accessPermission = new AccessPermission();
        accessPermission.setCanAssembleDocument(permission.CanAssembleDocument);
        accessPermission.setCanExtractContent(permission.CanExtractContent);
        accessPermission.setCanExtractForAccessibility(permission.CanExtractForAccessibility);
        accessPermission.setCanFillInForm(permission.CanFillInForm);
        accessPermission.setCanModify(permission.CanModify);
        accessPermission.setCanModifyAnnotations(permission.CanModifyAnnotations);
        accessPermission.setCanPrint(permission.CanPrint);
        accessPermission.setCanPrintDegraded(permission.CanPrintDegraded);
        return accessPermission;
    }

    public PDDocument getInternalDocument() {
        return document;
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

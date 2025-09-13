package org.vincentyeh.img2pdf.lib.pdf.concrete.object;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IPage;
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
    private final AccessPermission permission = new AccessPermission();
    private final Map<Integer, IPage> pages = new HashMap<>();
    private final DocumentArgument docArgument;


    public PDFBoxDocumentAdaptor(DocumentArgument argument) {
        this(argument, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public PDFBoxDocumentAdaptor(DocumentArgument argument, MemoryUsageSetting memoryUsageSetting) {
        document = new PDDocument(memoryUsageSetting);
        this.docArgument = argument;
    }

    @Override
    public void addPage(IPage page) {
        if (page == null)
            throw new IllegalArgumentException("page==null");

        if (pages.containsKey(page.getPageNumber()))
            throw new IllegalArgumentException("page with ID " + page.getPageNumber() + " already exists");

        pages.put(page.getPageNumber(), page);

    }

    @Override
    public void save(OutputStream outputStream) throws IOException{
        if (document == null)
            throw new IllegalStateException("document has not been created");
        String ownerPassword = this.docArgument.ownerPassword;
        String userPassword = this.docArgument.userPassword;

        if (ownerPassword != null && userPassword != null)
            document.protect(createProtectionPolicy(ownerPassword, userPassword, permission));

        setInfo();
        setPermission();

        for (int i = 1; i <= pages.size(); i++) {
            IPage page = pages.get(i);
            if (page == null)
                throw new IllegalStateException("page with ID " + i + " does not exist");
            document.addPage(((PDFBoxPageAdaptor) page).getInternalPage());
        }
        document.save(outputStream);
    }

    @Override
    public void save(File destination) throws IOException {
        if (destination == null)
            throw new IllegalArgumentException("destination==null");
        if (destination.exists() && !destination.canWrite())
            throw new IllegalArgumentException("destination is not writable");

        this.save(new FileOutputStream(destination));
    }

    @Override
    public void close() throws IOException {
        if (document != null)
            document.close();
    }

    private void setInfo() {
        PDFDocumentInfo info = docArgument.info;
        if (info == null)
            return;

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

    private void setPermission() {
        Permission permission = this.docArgument.permission;
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

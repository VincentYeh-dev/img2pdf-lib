package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class OpenPDFDocumentAdaptor implements IDocument {

    private final Document document;
    private final DocumentArgument docArgument;
    private final Set<Integer> pageMap = Collections.synchronizedSet(new HashSet<>());
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final PdfCopy copy;

    public OpenPDFDocumentAdaptor(DocumentArgument argument) {
        checkArgument(argument);

        this.docArgument = argument;
        document = new Document();

        copy = new PdfCopy(document, buffer);
        document.open();

        if (argument.hasInfo())
            setPDFDocumentInfo(argument.getInfo(), document);

    }

    private void checkArgument(DocumentArgument argument) {
        if (argument == null) {
            throw new IllegalArgumentException("argument==null");
        }
        if(argument.isEncrypted()){
            if(Objects.requireNonNull(argument.getOwnerPassword()).isEmpty())
                throw new IllegalArgumentException("ownerPassword can not be empty");
            if(Objects.requireNonNull(argument.getUserPassword()).isEmpty())
                throw new IllegalArgumentException("userPassword can not be empty");
        }
    }

    @Override
    public void addPage(IPage page) {
        if (page == null)
            throw new IllegalArgumentException("page==null");

        for (Integer p : pageMap) {
            if (p.equals(page.getPageNumber()))
                throw new IllegalArgumentException("page number " + page.getPageNumber() + " already exists");
        }
        OpenPDFPageAdaptor pageAdaptor = (OpenPDFPageAdaptor) page;
        byte[] rawData = pageAdaptor.getPDFBytesContent();
        pageMap.add(page.getPageNumber());
        try {
            mergePage(rawData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void mergePage(byte[] pdfBytes) throws Exception {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
        copy.addPage(copy.getImportedPage(reader, 1));
        reader.close();
    }

    @Override
    public void saveAndClose(OutputStream outputStream) throws IOException {
        document.close();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(buffer.toByteArray()));

        PdfStamper stamper = new PdfStamper(reader, outputStream);

        if(docArgument.isEncrypted()){
            encryptDocument(stamper, docArgument);
        }
        stamper.close();
        reader.close();
        buffer.close();
    }

    @Override
    public void saveAndClose(File destination) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            saveAndClose(fos);
        }
    }

    @Override
    public int getPageCount() {
        return pageMap.size();
    }

    private void setPDFDocumentInfo(PDFDocumentInfo info, Document document) {
        if (info.Title != null)
            document.addTitle(info.Title);
        if (info.Author != null)
            document.addAuthor(info.Author);
        if (info.Subject != null)
            document.addSubject(info.Subject);
        if (info.Creator != null)
            document.addCreator(info.Creator);
        if (info.Producer != null)
            document.addProducer(info.Producer);
    }

    private void encryptDocument(PdfStamper pdfStamper, DocumentArgument documentArgument) {
        Permission permission = documentArgument.getPermission();

        int permissionFlag = 0;

        permissionFlag |= (permission.CanAssembleDocument) ? PdfWriter.ALLOW_ASSEMBLY : 0;
        permissionFlag |= (permission.CanExtractContent) ? PdfWriter.ALLOW_COPY : 0;
        permissionFlag |= (permission.CanExtractForAccessibility) ? PdfWriter.ALLOW_SCREENREADERS : 0;
        permissionFlag |= (permission.CanFillInForm) ? PdfWriter.ALLOW_FILL_IN : 0;
        permissionFlag |= (permission.CanModify) ? PdfWriter.ALLOW_MODIFY_CONTENTS : 0;
        permissionFlag |= (permission.CanModifyAnnotations) ? PdfWriter.ALLOW_MODIFY_ANNOTATIONS : 0;
        permissionFlag |= (permission.CanPrint) ? PdfWriter.ALLOW_PRINTING : 0;
        permissionFlag |= (permission.CanPrintDegraded) ? PdfWriter.ALLOW_DEGRADED_PRINTING : 0;


        if (documentArgument.isEncrypted()) {
            String userPassword = documentArgument.getUserPassword();
            String ownerPassword = documentArgument.getOwnerPassword();
            if (userPassword.isEmpty() || ownerPassword.isEmpty())
                throw new IllegalArgumentException("Password can not be empty");

            pdfStamper.setEncryption(userPassword.getBytes(), ownerPassword.getBytes(), permissionFlag, PdfWriter.STANDARD_ENCRYPTION_128);
        }
    }

    public Document getInternalDocument() {
        return document;
    }

}

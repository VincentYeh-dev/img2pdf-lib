package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenPDFDocumentAdaptorTest {

    @Test
    public void testConstructorAndGetDocument() {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        Assertions.assertNotNull(adaptor);
    }

    @Test
    public void testConstructorInvalidArgument() {
        DocumentArgument mockArg = mock(DocumentArgument.class);
        when(mockArg.isEncrypted()).thenReturn(true);
        when(mockArg.getOwnerPassword()).thenReturn("");
        when(mockArg.getUserPassword()).thenReturn("");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpenPDFDocumentAdaptor(mockArg));
    }

    @Test
    public void testSaveToOutputStream() throws IOException {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());
        IPage page = createMockPage(1);
        adaptor.addPage(page);
        page.render(adaptor);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);
        byte[] pdfBytes = outputStream.toByteArray();
        Assertions.assertTrue(pdfBytes.length > 0);
    }

    @Test
    public void testSetAndGetDocumentInfo() throws IOException {
        PDFDocumentInfo info = new PDFDocumentInfo();
        info.Title = "TestTitle";
        info.Author = "TestAuthor";
        info.Subject = "TestSubject";
        info.Producer = "TestProducer";
        info.Creator = "TestCreator";

        DocumentArgument arg = new DocumentArgument();
        arg.setInfo(info);

        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(arg);
        IPage page = createMockPage(1);
        adaptor.addPage(page);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);

        PDDocument savedDocument = PDDocument.load(outputStream.toByteArray());

        Assertions.assertEquals(info.Title, savedDocument.getDocumentInformation().getTitle());
        Assertions.assertEquals(info.Author, savedDocument.getDocumentInformation().getAuthor());
        Assertions.assertEquals(info.Subject, savedDocument.getDocumentInformation().getSubject());
        Assertions.assertTrue(savedDocument.getDocumentInformation().getProducer().contains(info.Producer));
        Assertions.assertEquals(info.Creator, savedDocument.getDocumentInformation().getCreator());

    }

    @Test
    public void testPassword() throws IOException {
        String ownerPassword = "owner123";
        String userPassword = "user123";
        DocumentArgument arg = new DocumentArgument();
        arg.setEncryption(ownerPassword, userPassword, new Permission());
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(arg);

        Document testDocument = new Document();
        ByteArrayOutputStream a = new ByteArrayOutputStream();
        PdfWriter.getInstance(testDocument, a);
        testDocument.open();
        testDocument.add(new Paragraph("Test Page"));
        testDocument.close();

        OpenPDFPageAdaptor page = createMockPage(1);

        adaptor.addPage(page);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);

        Assertions.assertDoesNotThrow(() -> PDDocument.load(outputStream.toByteArray(), ownerPassword));
        Assertions.assertDoesNotThrow(() -> PDDocument.load(outputStream.toByteArray(), userPassword));
        Assertions.assertThrows(InvalidPasswordException.class, () ->
                PDDocument.load(outputStream.toByteArray(), "wrongpassword"));
    }

    @Test
    public void testAddPage() {
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(new DocumentArgument());

        Assertions.assertThrows(IllegalArgumentException.class, () -> adaptor.addPage(null));
        Assertions.assertEquals(0, adaptor.getPageCount());

        IPage page1 = createMockPage(1);
        IPage page2 = createMockPage(2);
        IPage page3 = createMockPage(3);
        IPage page4 = createMockPage(3);

        adaptor.addPage(page1);
        Assertions.assertEquals(1, adaptor.getPageCount());
        adaptor.addPage(page2);
        Assertions.assertEquals(2, adaptor.getPageCount());
        adaptor.addPage(page3);
        Assertions.assertEquals(3, adaptor.getPageCount());

        Assertions.assertThrows(IllegalArgumentException.class, () -> adaptor.addPage(page4));
    }

    @Test
    public void testConstructorWithNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpenPDFDocumentAdaptor(null));
    }

    @Test
    public void testSetNullDocumentInfo() {
        DocumentArgument arg = new DocumentArgument();
        Assertions.assertThrows(IllegalArgumentException.class, () -> arg.setInfo(null));
    }

    @Test
    public void testSetEmptyPassword() {
        DocumentArgument arg = mock(DocumentArgument.class);
        when(arg.isEncrypted()).thenReturn(true);
        when(arg.getOwnerPassword()).thenReturn("");
        when(arg.getUserPassword()).thenReturn("");
        when(arg.getPermission()).thenReturn(new Permission());

        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpenPDFDocumentAdaptor(arg));
    }

    @Test
    public void testPermissionSettings() throws IOException {
        Permission permission = new Permission();
        permission.CanPrint = false;
        permission.CanPrintDegraded = false;
        permission.CanModify = true;
        permission.CanExtractContent = false;
        permission.CanFillInForm = true;

        DocumentArgument arg = new DocumentArgument();
        arg.setEncryption("owner", "user", permission);
        OpenPDFDocumentAdaptor adaptor = new OpenPDFDocumentAdaptor(arg);
        IPage page = createMockPage(1);
        adaptor.addPage(page);
        page.render(adaptor);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);

        // 使用擁有者密碼載入
        PDDocument docOwner = PDDocument.load(outputStream.toByteArray(), "owner");
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canModify());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canFillInForm());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canPrint());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canPrintDegraded());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canExtractContent());
        docOwner.close();
        // 使用使用者密碼載入
        PDDocument docUser = PDDocument.load(outputStream.toByteArray(), "user");
        Assertions.assertTrue(docUser.getCurrentAccessPermission().canModify());
        Assertions.assertTrue(docUser.getCurrentAccessPermission().canFillInForm());
        Assertions.assertFalse(docUser.getCurrentAccessPermission().canPrint());
        Assertions.assertFalse(docUser.getCurrentAccessPermission().canPrintDegraded());
        Assertions.assertFalse(docUser.getCurrentAccessPermission().canExtractContent());
        docUser.close();
    }

    private static OpenPDFPageAdaptor createMockPage(int pageNumber) {
        Document testDocument = new Document();
        ByteArrayOutputStream a = new ByteArrayOutputStream();
        PdfWriter.getInstance(testDocument, a);
        testDocument.open();
        testDocument.add(new Paragraph("Test Page " + pageNumber));
        testDocument.close();

        OpenPDFPageAdaptor page = mock(OpenPDFPageAdaptor.class);
        when(page.getPageNumber()).thenReturn(pageNumber);
        when(page.getPDFBytesContent()).thenReturn(a.toByteArray());
        return page;
    }

}

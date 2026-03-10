package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PDFBoxDocumentAdaptorTest {

    @Test
    public void testConstructorAndGetDocument() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        Assertions.assertNotNull(adaptor.getInternalDocument());
    }

    @Test
    public void testSaveAndCloseToOutputStream() throws IOException {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);
        byte[] pdfBytes = outputStream.toByteArray();
        Assertions.assertTrue(pdfBytes.length > 0);
    }

    @Test
    public void testSetAndGetDocumentProperties() throws IOException {
        PDFDocumentInfo info = new PDFDocumentInfo();
        info.Title = "TestTitle";
        info.Author = "TestAuthor";
        info.Subject = "TestSubject";

        DocumentArgument arg = new DocumentArgument();
        arg.setInfo(info);

        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(arg);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);

        PDDocument savedDocument = PDDocument.load(outputStream.toByteArray());

        Assertions.assertEquals("TestTitle", savedDocument.getDocumentInformation().getTitle());
        Assertions.assertEquals("TestAuthor", savedDocument.getDocumentInformation().getAuthor());
        Assertions.assertEquals("TestSubject", savedDocument.getDocumentInformation().getSubject());
    }

    @Test
    public void testAddPageAndPageCount() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        PDDocument doc = adaptor.getInternalDocument();
        int initialPageCount = doc.getNumberOfPages();
        doc.addPage(new PDPage());
        Assertions.assertEquals(initialPageCount + 1, doc.getNumberOfPages());
    }

    @Test
    public void testPassword() throws IOException {
        String ownerPassword = "owner123";
        String userPassword = "user123";
        DocumentArgument arg = new DocumentArgument();
        arg.setEncryption(ownerPassword, userPassword, new Permission());
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(arg);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);

        Assertions.assertDoesNotThrow(() -> PDDocument.load(outputStream.toByteArray(), ownerPassword));
        Assertions.assertDoesNotThrow(() -> PDDocument.load(outputStream.toByteArray(), userPassword));
        Assertions.assertThrows(InvalidPasswordException.class, () ->
                PDDocument.load(outputStream.toByteArray(), "wrongpassword"));
    }

    @Test
    public void testPageCount() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        IPage page1 = mock(IPage.class);
        when(page1.getPageNumber()).thenReturn(1);
        IPage page2 = mock(IPage.class);
        when(page2.getPageNumber()).thenReturn(2);
        IPage page3 = mock(IPage.class);
        when(page3.getPageNumber()).thenReturn(3);

        adaptor.addPage(page1);
        Assertions.assertEquals(1, adaptor.getPageCount());
        adaptor.addPage(page2);
        Assertions.assertEquals(2, adaptor.getPageCount());
        adaptor.addPage(page3);
        Assertions.assertEquals(3, adaptor.getPageCount());
    }

    @Test
    public void testAddDuplicatePageThrows() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        IPage page1 = mock(IPage.class);
        when(page1.getPageNumber()).thenReturn(1);
        adaptor.addPage(page1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> adaptor.addPage(page1));
    }

    @Test
    public void testAddEntryPage() throws IOException {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        adaptor.addPage(new PDFBoxPageAdaptor(1, PageSize.A4.getSizeInPixels()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);

        PDDocument savedDocument = PDDocument.load(outputStream.toByteArray());
        Assertions.assertEquals(1, savedDocument.getNumberOfPages());

    }

    @Test
    public void testAddNullPageThrows() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        Assertions.assertThrows(IllegalArgumentException.class, () -> adaptor.addPage(null));
    }

    @Test
    public void testConstructorWithNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new PDFBoxDocumentAdaptor(null));
    }

    @Test
    public void testGetPageCountWhenNoPages() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        Assertions.assertEquals(0, adaptor.getPageCount());
    }

    @Test
    public void testSetNullDocumentInfo() throws IOException {
        DocumentArgument arg = new DocumentArgument();
        Assertions.assertThrows(IllegalArgumentException.class, () -> arg.setInfo(null));
    }

    @Test
    public void testSetEmptyPassword() throws IOException {
        DocumentArgument arg = mock(DocumentArgument.class);
        when(arg.isEncrypted()).thenReturn(true);
        when(arg.getOwnerPassword()).thenReturn("");
        when(arg.getUserPassword()).thenReturn("");
        when(arg.getPermission()).thenReturn(new Permission());

        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(arg);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Assertions.assertDoesNotThrow(() -> adaptor.saveAndClose(outputStream));
    }

    @Test
    public void testPermissionSettings() throws IOException {
        Permission permission = new Permission();
        permission.CanPrint = false;
        permission.CanModify = true;
        permission.CanExtractContent = false;
        permission.CanFillInForm = true;

        DocumentArgument arg = new DocumentArgument();
        arg.setEncryption("owner", "user", permission);
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(arg);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);

        // 使用擁有者密碼載入
        PDDocument docOwner = PDDocument.load(outputStream.toByteArray(), "owner");
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canModify());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canFillInForm());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canPrint());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canExtractContent());
        docOwner.close();
        // 使用使用者密碼載入
        PDDocument docUser = PDDocument.load(outputStream.toByteArray(), "user");
        Assertions.assertTrue(docUser.getCurrentAccessPermission().canModify());
        Assertions.assertTrue(docUser.getCurrentAccessPermission().canFillInForm());
        Assertions.assertFalse(docUser.getCurrentAccessPermission().canPrint());
        Assertions.assertFalse(docUser.getCurrentAccessPermission().canExtractContent());
        docUser.close();
    }

}

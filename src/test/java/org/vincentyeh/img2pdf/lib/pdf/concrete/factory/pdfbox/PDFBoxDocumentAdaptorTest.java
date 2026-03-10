package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link PDFBoxDocumentAdaptor}, covering construction, page management,
 * document metadata, password encryption, and permission enforcement.
 */
public class PDFBoxDocumentAdaptorTest {

    /** Verifies that construction succeeds and the adaptor is not null. */
    @Test
    public void testConstructorAndGetDocument() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        Assertions.assertNotNull(adaptor);
    }

    /** Verifies that saveAndClose() writes a non-empty byte array to the output stream. */
    @Test
    public void testSaveAndCloseToOutputStream() throws IOException {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);
        byte[] pdfBytes = outputStream.toByteArray();
        Assertions.assertTrue(pdfBytes.length > 0);
    }

    /** Verifies that document metadata set via DocumentArgument is embedded in the saved PDF. */
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

    /** Verifies that adding a PDFBoxPageAdaptor increments getPageCount() by one. */
    @Test
    public void testAddPageAndPageCount() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        Assertions.assertEquals(0, adaptor.getPageCount());
        adaptor.addPage(new PDFBoxPageAdaptor(1, new org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF(100, 100)));
        Assertions.assertEquals(1, adaptor.getPageCount());
    }

    /** Verifies that the encrypted PDF is openable with owner and user passwords but rejects wrong passwords. */
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

    /** Verifies that getPageCount() increments correctly as real pages are added via addPage(). */
    @Test
    public void testPageCount() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        SizeF sz = new SizeF(100, 100);
        adaptor.addPage(new PDFBoxPageAdaptor(1, sz));
        Assertions.assertEquals(1, adaptor.getPageCount());
        adaptor.addPage(new PDFBoxPageAdaptor(2, sz));
        Assertions.assertEquals(2, adaptor.getPageCount());
        adaptor.addPage(new PDFBoxPageAdaptor(3, sz));
        Assertions.assertEquals(3, adaptor.getPageCount());
    }

    /** Verifies that adding two pages with the same page number throws IllegalArgumentException. */
    @Test
    public void testAddDuplicatePageThrows() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        SizeF sz = new SizeF(100, 100);
        PDFBoxPageAdaptor page1 = new PDFBoxPageAdaptor(1, sz);
        adaptor.addPage(page1);
        // 第二個頁號相同的頁面應拋出例外
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> adaptor.addPage(new PDFBoxPageAdaptor(1, sz)));
    }

    /** Verifies that a PDFBoxPageAdaptor added via addPage() appears as exactly one page in the saved PDF. */
    @Test
    public void testAddEntryPage() throws IOException {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        adaptor.addPage(new PDFBoxPageAdaptor(1, PageSize.A4.getSizeInPixels()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.saveAndClose(outputStream);

        PDDocument savedDocument = PDDocument.load(outputStream.toByteArray());
        Assertions.assertEquals(1, savedDocument.getNumberOfPages());

    }

    /** Verifies that addPage(null) throws IllegalArgumentException. */
    @Test
    public void testAddNullPageThrows() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        Assertions.assertThrows(IllegalArgumentException.class, () -> adaptor.addPage(null));
    }

    /** Verifies that constructing with a null DocumentArgument throws IllegalArgumentException. */
    @Test
    public void testConstructorWithNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new PDFBoxDocumentAdaptor(null));
    }

    /** Verifies that getPageCount() returns 0 on a freshly created adaptor with no pages added. */
    @Test
    public void testGetPageCountWhenNoPages() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        Assertions.assertEquals(0, adaptor.getPageCount());
    }

    /** Verifies that setInfo(null) on DocumentArgument throws IllegalArgumentException. */
    @Test
    public void testSetNullDocumentInfo() throws IOException {
        DocumentArgument arg = new DocumentArgument();
        Assertions.assertThrows(IllegalArgumentException.class, () -> arg.setInfo(null));
    }

    /** Verifies that saveAndClose() does not throw when encryption is configured with empty passwords. */
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

    /** Verifies that permission flags are applied correctly for both owner and user access levels. */
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

        // Load with owner password
        PDDocument docOwner = PDDocument.load(outputStream.toByteArray(), "owner");
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canModify());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canFillInForm());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canPrint());
        Assertions.assertTrue(docOwner.getCurrentAccessPermission().canExtractContent());
        docOwner.close();
        // Load with user password
        PDDocument docUser = PDDocument.load(outputStream.toByteArray(), "user");
        Assertions.assertTrue(docUser.getCurrentAccessPermission().canModify());
        Assertions.assertTrue(docUser.getCurrentAccessPermission().canFillInForm());
        Assertions.assertFalse(docUser.getCurrentAccessPermission().canPrint());
        Assertions.assertFalse(docUser.getCurrentAccessPermission().canExtractContent());
        docUser.close();
    }

}

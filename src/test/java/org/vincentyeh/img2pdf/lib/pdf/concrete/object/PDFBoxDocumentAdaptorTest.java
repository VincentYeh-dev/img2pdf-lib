package org.vincentyeh.img2pdf.lib.pdf.concrete.object;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IPage;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDFBoxDocumentAdaptorTest {

    @Test
    public void testConstructorAndGetDocument() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        Assertions.assertNotNull(adaptor.getInternalDocument());
    }

    @Test
    public void testSaveToOutputStream() throws IOException {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.save(outputStream);
        adaptor.close();
        byte[] pdfBytes = outputStream.toByteArray();
        Assertions.assertTrue(pdfBytes.length > 0);
    }

    @Test
    public void testCloseTwice() throws IOException {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        adaptor.close();
        // 再次關閉不應拋出異常
        Assertions.assertDoesNotThrow(adaptor::close);
    }

    @Test
    public void testSaveAfterCloseThrows() throws IOException {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        adaptor.close();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Assertions.assertThrows(IOException.class, () -> adaptor.save(outputStream));
    }

    @Test
    public void testSetAndGetDocumentProperties() throws IOException {
        PDFDocumentInfo info = new PDFDocumentInfo();
        info.Title = "TestTitle";
        info.Author = "TestAuthor";
        info.Subject = "TestSubject";

        DocumentArgument arg = new DocumentArgument(new Permission());
        arg.setInfo(info);

        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(arg);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.save(outputStream);

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
    public void testSaveClosedDocument() throws IOException {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        adaptor.close();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Assertions.assertThrows(IOException.class, () -> adaptor.save(outputStream));
    }

    @Test
    public void testPassword() throws IOException {
        String ownerPassword = "owner123";
        String userPassword = "user123";
        DocumentArgument arg = new DocumentArgument();
        arg.setOwnerPassword(ownerPassword);
        arg.setUserPassword(userPassword);
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(arg);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.save(outputStream);

        Assertions.assertDoesNotThrow(() -> PDDocument.load(outputStream.toByteArray(), ownerPassword));
        Assertions.assertDoesNotThrow(() -> PDDocument.load(outputStream.toByteArray(), userPassword));
        Assertions.assertThrows(InvalidPasswordException.class, () ->
                PDDocument.load(outputStream.toByteArray(), "wrongpassword"));
    }

    @Test
    public void testPageCount() {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        IPage page1 = Mockito.mock(IPage.class);
        Mockito.when(page1.getPageNumber()).thenReturn(1);
        IPage page2 = Mockito.mock(IPage.class);
        Mockito.when(page2.getPageNumber()).thenReturn(2);
        IPage page3 = Mockito.mock(IPage.class);
        Mockito.when(page3.getPageNumber()).thenReturn(3);

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
        IPage page1 = Mockito.mock(IPage.class);
        Mockito.when(page1.getPageNumber()).thenReturn(1);
        adaptor.addPage(page1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> adaptor.addPage(page1));
    }

    @Test
    public void testAddEntryPage() throws IOException {
        PDFBoxDocumentAdaptor adaptor = new PDFBoxDocumentAdaptor(new DocumentArgument());
        adaptor.addPage(new PDFBoxPageAdaptor(adaptor.getInternalDocument(), 1, PageSize.A4.getSizeInPixels()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        adaptor.save(outputStream);

        PDDocument savedDocument = PDDocument.load(outputStream.toByteArray());
        Assertions.assertEquals(1, savedDocument.getNumberOfPages());

    }


}

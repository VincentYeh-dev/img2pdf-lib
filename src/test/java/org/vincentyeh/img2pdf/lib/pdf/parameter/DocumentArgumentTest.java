package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DocumentArgumentTest {

    @Test
    void testDefaultConstructor() {
        DocumentArgument arg = new DocumentArgument();
        assertNotNull(arg.getPermission());
        assertFalse(arg.hasInfo());
        assertFalse(arg.hasOwnerPassword());
        assertFalse(arg.hasUserPassword());
    }

    @Test
    void testConstructorWithPermission() {
        Permission perm = new Permission();
        DocumentArgument arg = new DocumentArgument(perm);
        assertEquals(perm, arg.getPermission());
    }

    @Test
    void testSetAndGetInfo() {
        DocumentArgument arg = new DocumentArgument();
        PDFDocumentInfo info = new PDFDocumentInfo();
        arg.setInfo(info);
        assertTrue(arg.hasInfo());
        assertEquals(info, arg.getInfo());
    }

    @Test
    void testSetInfoNullThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalArgumentException.class, () -> arg.setInfo(null));
    }

    @Test
    void testGetInfoNotSetThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalStateException.class, arg::getInfo);
    }

    @Test
    void testSetAndGetOwnerPassword() {
        DocumentArgument arg = new DocumentArgument();
        arg.setOwnerPassword("owner123");
        assertTrue(arg.hasOwnerPassword());
        assertEquals("owner123", arg.getOwnerPassword());
    }

    @Test
    void testSetOwnerPasswordNullThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(NullPointerException.class, () -> arg.setOwnerPassword(null));
    }

    @Test
    void testSetOwnerPasswordEmptyThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalArgumentException.class, () -> arg.setOwnerPassword(""));
    }

    @Test
    void testGetOwnerPasswordNotSetThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalStateException.class, arg::getOwnerPassword);
    }

    @Test
    void testSetAndGetUserPassword() {
        DocumentArgument arg = new DocumentArgument();
        arg.setUserPassword("user123");
        assertTrue(arg.hasUserPassword());
        assertEquals("user123", arg.getUserPassword());
    }

    @Test
    void testSetUserPasswordNullThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(NullPointerException.class, () -> arg.setUserPassword(null));
    }

    @Test
    void testSetUserPasswordEmptyThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalArgumentException.class, () -> arg.setUserPassword(""));
    }

    @Test
    void testGetUserPasswordNotSetThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalStateException.class, arg::getUserPassword);
    }

    @Test
    void testSetAndGetPermission() {
        DocumentArgument arg = new DocumentArgument();
        Permission perm = new Permission();
        perm.CanPrint = false;
        arg.setPermission(perm);
        assertEquals(perm, arg.getPermission());
    }
}


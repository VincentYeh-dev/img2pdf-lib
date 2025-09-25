package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentArgumentTest {

    @Test
    void testDefaultConstructor() {
        DocumentArgument arg = new DocumentArgument();
        assertFalse(arg.hasInfo());
        assertFalse(arg.isEncrypted());
    }

    @Test
    void testConstructorWithPermission() {
        DocumentArgument arg = new DocumentArgument();
        Permission perm = new Permission();
        arg.setEncryption("owner123", "user123", perm);
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
    void testSetAndGetPassword() {
        DocumentArgument arg = new DocumentArgument();
        String ownerPwd = "owner123";
        String userPwd = "user123";
        arg.setEncryption(ownerPwd, userPwd, new Permission());
        assertTrue(arg.isEncrypted());
        assertEquals(ownerPwd, arg.getOwnerPassword());
        assertEquals(userPwd, arg.getUserPassword());
    }

    @Test
    void testSetEncryptionNullThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(NullPointerException.class, () -> arg.setEncryption(null, null, new Permission()));
    }

    @Test
    void testSetEncryptionEmptyThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalArgumentException.class, () -> arg.setEncryption("", "", new Permission()));
    }

    @Test
    void testGetOwnerPasswordNotSetThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalStateException.class, arg::getOwnerPassword);
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
        arg.setEncryption("123", "456", perm);
        assertEquals(perm, arg.getPermission());
    }
}


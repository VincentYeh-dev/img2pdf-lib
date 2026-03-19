package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link DocumentArgument}, verifying default state, encryption configuration,
 * document info attachment, and guard clauses for invalid inputs.
 */
class DocumentArgumentTest {

    /** Verifies that a default DocumentArgument has no info and no encryption. */
    @Test
    void testDefaultConstructor() {
        DocumentArgument arg = new DocumentArgument();
        assertFalse(arg.hasInfo());
        assertFalse(arg.isEncrypted());
    }

    /** Verifies that setEncryption stores the Permission and makes it retrievable. */
    @Test
    void testConstructorWithPermission() {
        DocumentArgument arg = new DocumentArgument();
        Permission perm = new Permission();
        arg.setEncryption("owner123", "user123", perm);
        assertEquals(perm, arg.getPermission());
    }

    /** Verifies that setInfo stores the info object and hasInfo() returns true afterwards. */
    @Test
    void testSetAndGetInfo() {
        DocumentArgument arg = new DocumentArgument();
        PDFDocumentInfo info = new PDFDocumentInfo();
        arg.setInfo(info);
        assertTrue(arg.hasInfo());
        assertEquals(info, arg.getInfo());
    }

    /** Verifies that setInfo(null) throws IllegalArgumentException. */
    @Test
    void testSetInfoNullThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalArgumentException.class, () -> arg.setInfo(null));
    }

    /** Verifies that getInfo() throws IllegalStateException when no info has been set. */
    @Test
    void testGetInfoNotSetThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalStateException.class, arg::getInfo);
    }

    /** Verifies that setEncryption stores owner and user passwords retrievable via getOwnerPassword/getUserPassword. */
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

    /** Verifies that setEncryption(null, ...) throws NullPointerException. */
    @Test
    void testSetEncryptionNullThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(NullPointerException.class, () -> arg.setEncryption(null, null, new Permission()));
    }

    /** Verifies that setEncryption with empty password strings throws IllegalArgumentException. */
    @Test
    void testSetEncryptionEmptyThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalArgumentException.class, () -> arg.setEncryption("", "", new Permission()));
    }

    /** Verifies that getOwnerPassword() throws IllegalStateException when encryption has not been configured. */
    @Test
    void testGetOwnerPasswordNotSetThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalStateException.class, arg::getOwnerPassword);
    }

    /** Verifies that getUserPassword() throws IllegalStateException when encryption has not been configured. */
    @Test
    void testGetUserPasswordNotSetThrows() {
        DocumentArgument arg = new DocumentArgument();
        assertThrows(IllegalStateException.class, arg::getUserPassword);
    }

    /** Verifies that a custom Permission object is stored and returned correctly by getPermission(). */
    @Test
    void testSetAndGetPermission() {
        DocumentArgument arg = new DocumentArgument();
        Permission perm = new Permission();
        perm.CanPrint = false;
        arg.setEncryption("123", "456", perm);
        assertEquals(perm, arg.getPermission());
    }
}

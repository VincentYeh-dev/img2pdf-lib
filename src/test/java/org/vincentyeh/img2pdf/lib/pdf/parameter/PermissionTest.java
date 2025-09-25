package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void testDefaultValues() {
        Permission perm = new Permission();
        assertTrue(perm.CanAssembleDocument);
        assertTrue(perm.CanExtractContent);
        assertTrue(perm.CanExtractForAccessibility);
        assertTrue(perm.CanFillInForm);
        assertTrue(perm.CanModify);
        assertTrue(perm.CanModifyAnnotations);
        assertTrue(perm.CanPrint);
        assertTrue(perm.CanPrintDegraded);
    }

    @Test
    void testFieldAssignment() {
        Permission perm = new Permission();
        perm.CanAssembleDocument = false;
        perm.CanExtractContent = false;
        perm.CanExtractForAccessibility = false;
        perm.CanFillInForm = false;
        perm.CanModify = false;
        perm.CanModifyAnnotations = false;
        perm.CanPrint = false;
        perm.CanPrintDegraded = false;

        assertFalse(perm.CanAssembleDocument);
        assertFalse(perm.CanExtractContent);
        assertFalse(perm.CanExtractForAccessibility);
        assertFalse(perm.CanFillInForm);
        assertFalse(perm.CanModify);
        assertFalse(perm.CanModifyAnnotations);
        assertFalse(perm.CanPrint);
        assertFalse(perm.CanPrintDegraded);
    }
}


package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PDFDocumentInfoTest {

    @Test
    void testFieldAssignment() {
        PDFDocumentInfo info = new PDFDocumentInfo();
        info.Title = "title";
        info.Author = "author";
        info.Creator = "creator";
        info.Producer = "producer";
        info.Subject = "subject";

        assertEquals("title", info.Title);
        assertEquals("author", info.Author);
        assertEquals("creator", info.Creator);
        assertEquals("producer", info.Producer);
        assertEquals("subject", info.Subject);
    }
}


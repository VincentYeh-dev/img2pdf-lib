package org.vincentyeh.img2pdf.lib.pdf.framework.converter;

import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PdfDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PdfPage;

public interface PDFImpl {

    PdfDocument<?> createEmptyDocument();
    PdfPage<?> createEmptyPage(PdfDocument<?> document);
}

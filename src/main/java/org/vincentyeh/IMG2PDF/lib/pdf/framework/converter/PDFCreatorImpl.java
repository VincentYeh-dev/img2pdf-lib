package org.vincentyeh.IMG2PDF.lib.pdf.framework.converter;

import org.vincentyeh.IMG2PDF.lib.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.objects.PdfPage;

public interface PDFCreatorImpl {

    PdfDocument<?> createEmptyDocument();
    PdfPage<?> createEmptyPage(PdfDocument<?> document);
}

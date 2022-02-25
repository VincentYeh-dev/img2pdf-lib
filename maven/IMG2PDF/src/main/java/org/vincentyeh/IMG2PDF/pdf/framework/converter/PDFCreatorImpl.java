package org.vincentyeh.IMG2PDF.pdf.framework.converter;

import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfPage;

public interface PDFCreatorImpl {

    PdfDocument<?> createEmptyDocument();
    PdfPage<?> createEmptyPage(PdfDocument<?> document);
}

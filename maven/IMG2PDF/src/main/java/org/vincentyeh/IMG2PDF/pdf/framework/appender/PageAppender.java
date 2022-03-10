package org.vincentyeh.IMG2PDF.pdf.framework.appender;

import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfPage;

import java.util.List;
import java.util.concurrent.Callable;

public interface PageAppender {
    void appendToDocument(PdfDocument<?> document, List<Callable<PdfPage<?>>> list) throws Exception;
}

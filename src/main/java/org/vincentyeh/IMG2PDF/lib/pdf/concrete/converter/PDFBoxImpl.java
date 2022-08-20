package org.vincentyeh.img2pdf.lib.pdf.concrete.converter;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PdfDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PdfPage;
import org.vincentyeh.img2pdf.lib.pdf.concrete.objects.PdfBoxDocumentAdaptor;
import org.vincentyeh.img2pdf.lib.pdf.concrete.objects.PdfBoxPageAdaptor;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.PDFImpl;

import java.io.File;

public class PDFBoxImpl implements PDFImpl {

    private final MemoryUsageSetting setting;

    public PDFBoxImpl(File tempFolder, long maxMainMemoryBytes) {
        setting = MemoryUsageSetting.setupMixed(maxMainMemoryBytes).setTempDir(tempFolder);
    }

    public PdfDocument<?> createEmptyDocument() {
        return new PdfBoxDocumentAdaptor(new PDDocument(setting));
    }

    @Override
    public PdfPage<?> createEmptyPage(PdfDocument<?> document) {
        return new PdfBoxPageAdaptor(new PDPage(), (PDDocument) document.get());

    }

}

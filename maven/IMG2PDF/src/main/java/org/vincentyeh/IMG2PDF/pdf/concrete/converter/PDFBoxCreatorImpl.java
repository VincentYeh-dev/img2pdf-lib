package org.vincentyeh.IMG2PDF.pdf.concrete.converter;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.vincentyeh.IMG2PDF.pdf.concrete.objects.PdfBoxDocumentAdaptor;
import org.vincentyeh.IMG2PDF.pdf.concrete.objects.PdfBoxPageAdaptor;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.PDFCreatorImpl;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfPage;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;
import org.vincentyeh.IMG2PDF.util.file.exception.MakeDirectoryException;

import java.io.File;

public class PDFBoxCreatorImpl implements PDFCreatorImpl {

    private final MemoryUsageSetting setting;

    public PDFBoxCreatorImpl(File tempFolder, long maxMainMemoryBytes) throws MakeDirectoryException {
        FileUtils.makeDirectories(tempFolder);
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

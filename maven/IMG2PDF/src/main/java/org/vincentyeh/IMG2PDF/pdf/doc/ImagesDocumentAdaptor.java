package org.vincentyeh.IMG2PDF.pdf.doc;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.vincentyeh.IMG2PDF.util.FileChecker;

import java.io.IOException;

public class ImagesDocumentAdaptor {

    private final DocumentArgument argument;
    private final PDDocument document;
    public ImagesDocumentAdaptor(DocumentArgument argument,MemoryUsageSetting memoryUsageSetting) throws IOException {
        this.argument=argument;
        document=new PDDocument(memoryUsageSetting);
        document.protect(argument.getSpp());
    }

    public void save() throws IOException {
        document.save(argument.getDestination());
    }

    public void addPage(PDPage imgPage) {
        document.addPage(imgPage);
    }

    public PDDocument getDocument() {
        return document;
    }

    /**
     * Close PDF Document.
     */
    public void closeDocument() {
        try {
            if (document != null)
                document.close();
        } catch (IOException ignore) {
            ignore.printStackTrace();
        }
    }
}

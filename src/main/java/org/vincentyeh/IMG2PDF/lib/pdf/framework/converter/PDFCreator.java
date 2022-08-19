package org.vincentyeh.img2pdf.lib.pdf.framework.converter;

import org.vincentyeh.img2pdf.lib.pdf.framework.appender.PageAppender;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.exception.PDFConversionException;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.exception.SaveException;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PdfDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PdfPage;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class PDFCreator {

    private final DocumentArgument documentArgument;

    public interface CreationListener {
        void initializing();

        void onConversionComplete();

        void onSaved(File destination);

        void onFinally();
    }

    protected final PDFCreatorImpl pdfCreatorImpl;
    private final boolean overwrite;
    protected CreationListener listener;
    private final PageAppender appender;

    public PDFCreator(DocumentArgument documentArgument, PDFCreatorImpl pdfCreatorImpl, PageAppender pageAppender, boolean overwrite) {
        if (documentArgument == null)
            throw new IllegalArgumentException("documentArgument is null");
        if (pdfCreatorImpl == null)
            throw new IllegalArgumentException("pdfCreatorImpl is null");

        this.documentArgument = documentArgument;
        this.pdfCreatorImpl = pdfCreatorImpl;
        this.appender = pageAppender;
        this.overwrite = overwrite;
    }

    protected abstract List<Callable<PdfPage<?>>> getPageCallables(PdfDocument<?> document);

    protected final PdfDocument<?> generateDocument() throws IOException {
        var document = pdfCreatorImpl.createEmptyDocument();
        document.setOwnerPassword(this.documentArgument.ownerPassword());
        document.setUserPassword(this.documentArgument.userPassword());
        document.setPermission(this.documentArgument.permission());
        document.encrypt();
        document.setInfo(this.documentArgument.info());
        return document;
    }

    public final File start(File destination) throws PDFConversionException {
        try (PdfDocument<?> document = generateDocument()) {
            checkOverwrite(destination);
            if (appender != null)
                appender.append(document, getPageCallables(document));

            try {
                destination.getParentFile().mkdirs();
                document.save(destination);
                if (listener != null)
                    listener.onSaved(destination);
            } catch (IOException e) {
                throw new SaveException(e, destination);
            }

            if (listener != null)
                listener.onConversionComplete();

            if (listener != null)
                listener.onFinally();

            return destination;
        } catch (Exception e) {
            throw new PDFConversionException(e);
        }
    }

    private void checkOverwrite(File file) throws SaveException {
        if (!overwrite && file.exists()) {
            throw new SaveException(new IOException("Overwrite deny"), file);
        }
    }

    public final void setCreationListener(CreationListener listener) {
        this.listener = listener;
    }

}

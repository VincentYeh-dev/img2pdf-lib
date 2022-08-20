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

    public interface Listener {
        void initializing(long procedure_id);

        void onSaved(long procedure_id, File destination);

        void onConversionComplete(long procedure_id);

        void onAppend(long procedure_id, int index);
    }

    protected final PDFImpl pdfImpl;
    private final boolean overwrite;
    private final PageAppender appender;

    public PDFCreator(DocumentArgument documentArgument, PDFImpl pdfImpl, PageAppender pageAppender) {
        this(documentArgument, pdfImpl,pageAppender,false);
    }

    public PDFCreator(DocumentArgument documentArgument, PDFImpl pdfImpl, PageAppender pageAppender, boolean overwrite) {
        if (documentArgument == null)
            throw new IllegalArgumentException("documentArgument is null");
        if (pdfImpl == null)
            throw new IllegalArgumentException("pdfCreatorImpl is null");

        this.documentArgument = documentArgument;
        this.pdfImpl = pdfImpl;
        this.appender = pageAppender;
        this.overwrite = overwrite;
    }

    protected abstract List<Callable<PdfPage<?>>> getPageCallables(PdfDocument<?> document);

    protected final PdfDocument<?> generateDocument() throws IOException {
        var document = pdfImpl.createEmptyDocument();
        document.setOwnerPassword(this.documentArgument.ownerPassword());
        document.setUserPassword(this.documentArgument.userPassword());
        document.setPermission(this.documentArgument.permission());
        document.encrypt();
        document.setInfo(this.documentArgument.info());
        return document;
    }

    public final File start(File destination) throws PDFConversionException {
        return start(destination, null);
    }
    public final File start(File destination, Listener listener) throws PDFConversionException {
        return start(-1, destination, listener);
    }

    public final File start(long procedure_id, File destination, Listener listener) throws PDFConversionException {

        if (listener != null) {
            listener.initializing(procedure_id);
            appender.setPageAppendListener((page_index) -> listener.onAppend(procedure_id, page_index));
        }

        try (PdfDocument<?> document = generateDocument()) {
            checkOverwrite(destination);
            if (appender != null)
                appender.append(document, getPageCallables(document));

            try {
                destination.getParentFile().mkdirs();
                document.save(destination);
                if (listener != null)
                    listener.onSaved(procedure_id, destination);
            } catch (IOException e) {
                throw new SaveException(e, destination);
            }

            if (listener != null)
                listener.onConversionComplete(procedure_id);

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

}

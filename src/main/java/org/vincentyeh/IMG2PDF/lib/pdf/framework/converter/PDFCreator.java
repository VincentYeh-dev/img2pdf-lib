package org.vincentyeh.IMG2PDF.lib.pdf.framework.converter;

import org.vincentyeh.IMG2PDF.lib.pdf.framework.appender.PageAppender;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.converter.exception.PDFConversionException;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.converter.exception.SaveException;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.lib.pdf.framework.objects.PdfPage;
import org.vincentyeh.IMG2PDF.lib.pdf.parameter.DocumentArgument;

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

    protected final PDFCreatorImpl impl;
    private final boolean overwrite;
    protected CreationListener listener;
    private final PageAppender appender;

    public PDFCreator(DocumentArgument documentArgument, PDFCreatorImpl impl, PageAppender pageAppender, boolean overwrite) {
        this.documentArgument = documentArgument;
        if (impl == null)
            throw new IllegalArgumentException("impl is null");
        this.impl = impl;
        this.overwrite = overwrite;
        appender = pageAppender;
    }

    protected abstract List<Callable<PdfPage<?>>> getPageCallables(PdfDocument<?> document);

    protected final PdfDocument<?> generateDocument() throws IOException {
        PdfDocument<?> document = impl.createEmptyDocument();
        document.setOwnerPassword(this.documentArgument.getOwnerPassword());
        document.setUserPassword(this.documentArgument.getUserPassword());
        document.setPermission(this.documentArgument.getPermission());
        document.encrypt();
        document.setInfo(this.documentArgument.getInformation());
        return document;
    }

    public final File start(File destination) throws PDFConversionException {

        PdfDocument<?> document = null;
        try {
            checkOverwrite(destination);
            document = generateDocument();

            if(appender!=null)
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

            return destination;
        } catch (Exception e) {
            throw new PDFConversionException(e);
        } finally {
            try {
                if (document != null)
                    document.close();
            } catch (IOException ignored) {

            }
            if (listener != null)
                listener.onFinally();
        }

    }

    private void checkOverwrite(File file) throws SaveException {
        if (!overwrite&&file.exists()) {
            throw new SaveException(new IOException("Overwrite deny"),file);
        }
    }

    public final void setCreationListener(CreationListener listener) {
        this.listener = listener;
    }

}

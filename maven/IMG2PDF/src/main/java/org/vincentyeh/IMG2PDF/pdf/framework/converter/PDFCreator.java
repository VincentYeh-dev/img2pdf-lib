package org.vincentyeh.IMG2PDF.pdf.framework.converter;

import org.vincentyeh.IMG2PDF.pdf.framework.appender.PageAppender;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.exception.PDFConversionException;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.exception.SaveException;
import org.vincentyeh.IMG2PDF.pdf.framework.listener.PDFCreationListener;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfPage;
import org.vincentyeh.IMG2PDF.task.framework.Task;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;
import org.vincentyeh.IMG2PDF.util.file.exception.OverwriteException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class PDFCreator<L extends PDFCreationListener> {
    protected final PDFCreatorImpl impl;
    private final boolean overwrite;
    protected L listener;
    private final PageAppender appenderImpl;

    public PDFCreator(PDFCreatorImpl impl, PageAppender pageAppender, boolean overwrite) {
        this.impl = impl;
        this.overwrite = overwrite;
        appenderImpl = pageAppender;
    }

    protected abstract List<Callable<PdfPage<?>>> getPageCallables(PdfDocument<?> document, Task task);

    protected final PdfDocument<?> generateDocument(Task task) throws IOException {
        PdfDocument<?> document = impl.createEmptyDocument();
        document.setOwnerPassword(task.getDocumentArgument().getOwnerPassword());
        document.setUserPassword(task.getDocumentArgument().getUserPassword());
        document.setPermission(task.getDocumentArgument().getPermission());
        document.encrypt();
        document.setInfo(task.getDocumentArgument().getInformation());
        return document;
    }

    public final File start(Task task) throws PDFConversionException {
        if (task == null)
            throw new IllegalArgumentException("task is null.");

        if (listener != null)
            listener.initializing(task);

        try {
            checkOverwrite(task.getPdfDestination());
            PdfDocument<?> document = generateDocument(task);
            appenderImpl.appendToDocument(document, getPageCallables(document, task));
            try {
                document.save(task.getPdfDestination());
            } catch (IOException e) {
                throw new SaveException(e, task.getPdfDestination());
            }
            document.close();

            if (listener != null)
                listener.onConversionComplete();

            return task.getPdfDestination();
        } catch (Exception e) {
            throw new PDFConversionException(task, e);
        } finally {
            if (listener != null)
                listener.onFinally();
        }

    }

    private void checkOverwrite(File file) throws SaveException {
        if (!overwrite) {
            try {
                FileUtils.checkOverwrite(file, "PDF overwrite deny,File is already exists:" + file.getAbsoluteFile());
            } catch (OverwriteException e) {
                throw new SaveException(e, file);
            }
        }
    }

    public void setListener(L listener) {
        this.listener = listener;
    }

}

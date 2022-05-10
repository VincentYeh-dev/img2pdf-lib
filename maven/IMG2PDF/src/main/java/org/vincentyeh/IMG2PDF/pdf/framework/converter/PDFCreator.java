package org.vincentyeh.IMG2PDF.pdf.framework.converter;

import org.vincentyeh.IMG2PDF.pdf.framework.appender.PageAppender;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.exception.PDFConversionException;
import org.vincentyeh.IMG2PDF.pdf.framework.converter.exception.SaveException;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfDocument;
import org.vincentyeh.IMG2PDF.pdf.framework.objects.PdfPage;
import org.vincentyeh.IMG2PDF.task.framework.Task;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;
import org.vincentyeh.IMG2PDF.util.file.exception.OverwriteException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class PDFCreator {

    public interface CreationListener {
        void initializing(Task task);

        void onConversionComplete();

        void onSaved(File destination);

        void onFinally();
    }

    protected final PDFCreatorImpl impl;
    private final boolean overwrite;
    protected CreationListener listener;
    private final PageAppender appender;

    public PDFCreator(PDFCreatorImpl impl, PageAppender pageAppender, boolean overwrite) {
        if (impl == null)
            throw new IllegalArgumentException("impl is null");
        this.impl = impl;
        this.overwrite = overwrite;
        appender = pageAppender;
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
        PdfDocument<?> document = null;
        try {
            checkOverwrite(task.getPdfDestination());
            document = generateDocument(task);

            if(appender!=null)
                appender.append(document, getPageCallables(document, task));

            try {
                document.save(task.getPdfDestination());
                if (listener != null)
                    listener.onSaved(task.getPdfDestination());
            } catch (IOException e) {
                throw new SaveException(e, task.getPdfDestination());
            }

            if (listener != null)
                listener.onConversionComplete();

            return task.getPdfDestination();
        } catch (Exception e) {
            throw new PDFConversionException(task, e);
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
        if (!overwrite) {
            try {
                FileUtils.checkOverwrite(file, "PDF overwrite deny,File is already exists:" + file.getAbsoluteFile());
            } catch (OverwriteException e) {
                throw new SaveException(e, file);
            }
        }
    }

    public final void setCreationListener(CreationListener listener) {
        this.listener = listener;
    }

}

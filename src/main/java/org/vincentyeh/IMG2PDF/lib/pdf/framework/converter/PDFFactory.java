package org.vincentyeh.img2pdf.lib.pdf.framework.converter;

import org.vincentyeh.img2pdf.lib.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.exception.PDFConversionException;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.exception.SaveException;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;

import java.io.File;
import java.io.IOException;

public abstract class PDFFactory {

    private final DocumentArgument documentArgument;
    private final PDFBuilder builder;

    public interface Listener {
        void initializing(long procedure_id);

        void onSaved(long procedure_id, File destination);

        void onConversionComplete(long procedure_id);

        void onAppend(long procedure_id, int index);
    }

    private final boolean overwrite;

    protected abstract void AddPages(long procedure_id, PDFBuilder builder, Listener listener);

    public PDFFactory(DocumentArgument documentArgument, PDFBuilder builder) {
        this(documentArgument, builder, false);
    }

    public PDFFactory(DocumentArgument documentArgument, PDFBuilder builder, boolean overwrite) {
        this.builder = builder;
        if (documentArgument == null)
            throw new IllegalArgumentException("documentArgument is null");

        this.documentArgument = documentArgument;
        this.overwrite = overwrite;
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
        }

        builder.createDocument();
        if (listener != null)
            listener.onSaved(procedure_id, destination);
        builder.setOwnerPassword(this.documentArgument.ownerPassword());
        builder.setUserPassword(this.documentArgument.userPassword());
        builder.setPermission(this.documentArgument.permission());
        if(documentArgument.info()!=null)
            builder.setInfo(this.documentArgument.info());
        try {
            AddPages(procedure_id,builder, listener);
            if (listener != null)
                listener.onSaved(procedure_id, destination);
            builder.save(destination);
            if (listener != null)
                listener.onConversionComplete(procedure_id);
            builder.reset();
            return destination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void checkOverwrite(File file) throws SaveException {
        if (!overwrite && file.exists()) {
            throw new SaveException(new IOException("Overwrite deny"), file);
        }
    }

}

package org.vincentyeh.img2pdf.lib.pdf.framework.converter;

import org.vincentyeh.img2pdf.lib.pdf.concrete.calculation.strategy.StandardImagePageCalculationStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.exception.PDFConversionException;
import org.vincentyeh.img2pdf.lib.pdf.framework.converter.exception.SaveException;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePDFFactory {

    private final DocumentArgument documentArgument;
    private final PDFBuilder builder;

    private final ImageReadImpl imageReadImpl;
    private final StandardImagePageCalculationStrategy strategy;
    private final PageArgument pageArgument;
    private File[] imageFiles;


    public interface Listener {
        void initializing(long procedure_id);

        void onSaved(long procedure_id, File destination);

        void onConversionComplete(long procedure_id);

        void onAppend(long procedure_id, int index);
    }

    private final boolean overwrite;


    public ImagePDFFactory(PageArgument pageArgument, DocumentArgument documentArgument, ImageReadImpl imageReadImpl, PDFBuilder builder) {
        this(pageArgument, documentArgument, builder, imageReadImpl, false);
    }


    public ImagePDFFactory(PageArgument pageArgument, DocumentArgument documentArgument, PDFBuilder builder, ImageReadImpl imageReadImpl,
                           boolean overwrite) {
        this.builder = builder;
        if (documentArgument == null)
            throw new IllegalArgumentException("documentArgument is null");

        this.documentArgument = documentArgument;
        this.overwrite = overwrite;
        this.pageArgument = pageArgument;
        this.imageReadImpl = imageReadImpl;
        this.strategy = new StandardImagePageCalculationStrategy();
    }

    public final File start(File[] imageFiles, File destination, Listener listener) throws PDFConversionException {
        return start(-1,imageFiles,destination,listener);
    }

    public final File start(long procedure_id,File[] imageFiles, File destination, Listener listener) throws PDFConversionException {
        if (listener != null) {
            listener.initializing(procedure_id);
        }

        builder.createDocument();
        if (listener != null)
            listener.onSaved(procedure_id, destination);
        builder.setOwnerPassword(this.documentArgument.ownerPassword());
        builder.setUserPassword(this.documentArgument.userPassword());
        builder.setPermission(this.documentArgument.permission());
        if (documentArgument.info() != null)
            builder.setInfo(this.documentArgument.info());
        try {
            AddPages(procedure_id,imageFiles, listener);
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

    protected void AddPages(long procedure_id, File[] imageFiles, Listener listener) {
        int i = 0;
        if (imageFiles != null)
            for (File file : imageFiles) {
                try {
                    BufferedImage bufferedImage = imageReadImpl != null ? imageReadImpl.readImage(file) : ImageIO.read(file);
                    strategy.execute(this.pageArgument, new SizeF(bufferedImage.getWidth(), bufferedImage.getHeight()));
                    var index = builder.addPage(strategy.getPageSize());
                    builder.addImage(index, bufferedImage, strategy.getImagePosition(), strategy.getImageSize());
                    if (listener != null)
                        listener.onAppend(procedure_id, i);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                i++;
            }
    }

}

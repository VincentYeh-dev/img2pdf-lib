package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePageStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ImagePDFFactory {

    private final DocumentArgument documentArgument;
    private final PDFBuilder builder;

    private final ImageReader imageReader;
    private final ImagePageStrategy strategy;
    private final PageArgument pageArgument;


    public interface Listener {
        void initializing(long procedure_id);

        void onSaved(long procedure_id, File destination);

        void onConversionComplete(long procedure_id);

        void onAppend(long procedure_id, int index,int total);
    }

    private final boolean overwrite;


    public ImagePDFFactory(PDFBuilder builder, ImagePageStrategy strategy) {
        this(null, null, builder, null, strategy, false);
    }

    public ImagePDFFactory(PageArgument pageArgument, DocumentArgument documentArgument, PDFBuilder builder,
                           ImagePageStrategy strategy) {
        this(pageArgument, documentArgument, builder, null, strategy, false);
    }

    public ImagePDFFactory(PageArgument pageArgument, DocumentArgument documentArgument, PDFBuilder builder,
                           ImageReader imageReader, ImagePageStrategy strategy) {
        this(pageArgument, documentArgument, builder, imageReader, strategy, false);
    }

    public ImagePDFFactory(@Nullable PageArgument pageArgument,
                           @Nullable DocumentArgument documentArgument,
                           @NotNull PDFBuilder builder,
                           @Nullable ImageReader imageReader,
                           @NotNull ImagePageStrategy strategy,
                           boolean overwrite) {
        try{
            this.builder =Objects.requireNonNull(builder,"builder==null");
            this.pageArgument = Objects.requireNonNullElseGet(pageArgument, PageArgument::new);
            this.documentArgument = Objects.requireNonNullElseGet(documentArgument, DocumentArgument::new);
            this.imageReader = Objects.requireNonNullElseGet(imageReader, ImageReader::getDefault);
            this.strategy = Objects.requireNonNull(strategy,"strategy==null");
            this.overwrite = overwrite;
        }catch (NullPointerException e){
            throw new IllegalArgumentException(e);
        }
    }

    public final File start(File[] imageFiles, File destination, Listener listener) throws PDFFactoryException {
        return start(-1, imageFiles, destination, listener);
    }

    public final File start(long procedure_id, File[] imageFiles, File destination, Listener listener) throws PDFFactoryException {
        try {
            if (listener != null) {
                listener.initializing(procedure_id);
            }
            checkOverwrite(destination);
            builder.createDocument();

            if (listener != null)
                listener.onSaved(procedure_id, destination);

            builder.setOwnerPassword(this.documentArgument.ownerPassword());
            builder.setUserPassword(this.documentArgument.userPassword());
            builder.setPermission(this.documentArgument.permission());
            if (documentArgument.info() != null)
                builder.setInfo(this.documentArgument.info());
            AddPages(procedure_id, imageFiles, listener);
            if (listener != null)
                listener.onSaved(procedure_id, destination);
            builder.save(destination);
            if (listener != null)
                listener.onConversionComplete(procedure_id);
            builder.reset();
            return destination;
        } catch (Exception e) {
            throw new PDFFactoryException(e);
        }
    }


    private void checkOverwrite(File file) throws IOException {
        if (!overwrite && file.exists()) {
            throw new IOException("Overwrite deny");
        }
    }


    protected void AddPages(long procedure_id, File[] imageFiles, Listener listener) {
        int i = 0;
        if (imageFiles != null)
            for (File file : imageFiles) {
                try {
                    BufferedImage bufferedImage = imageReader != null ? imageReader.read(file) : ImageIO.read(file);
                    strategy.execute(this.pageArgument, new SizeF(bufferedImage.getWidth(), bufferedImage.getHeight()));
                    var index = builder.addPage(strategy.getPageSize());
                    builder.addImage(index, bufferedImage, strategy.getImagePosition(), strategy.getImageSize());
                    if (listener != null)
                        listener.onAppend(procedure_id, i,imageFiles.length);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                i++;
            }
    }

}

package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import org.vincentyeh.img2pdf.lib.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.FactoryImpl;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageFactoryListener;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePageStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class DefaultImagePDFFactory implements ImagePDFFactory {

    private final DocumentArgument documentArgument;
    private final PageArgument pageArgument;

    private final boolean overwrite;

    private final FactoryImpl impl;

    private final PDFBuilder builder;
    private final ImagePageStrategy strategy;


    public DefaultImagePDFFactory(@Nullable PageArgument pageArgument,
                                  @Nullable DocumentArgument documentArgument,
                                  @NotNull FactoryImpl impl,
                                  @NotNull PDFBuilder builder,
                                  boolean overwrite) {
        this(pageArgument, documentArgument, impl, builder, new DefaultImagePageStrategy(), overwrite);
    }

    public DefaultImagePDFFactory(@Nullable PageArgument pageArgument,
                                  @Nullable DocumentArgument documentArgument,
                                  @NotNull FactoryImpl impl,
                                  @NotNull PDFBuilder builder,
                                  @NotNull ImagePageStrategy strategy,
                                  boolean overwrite) {

        try {

            this.impl = Objects.requireNonNull(impl, "impl==null");
            this.builder = Objects.requireNonNull(builder, "builder==null");
            this.strategy = Objects.requireNonNull(strategy, "strategy==null");
            this.overwrite = overwrite;
            if (pageArgument == null) {
                this.pageArgument = new PageArgument();
            } else {
                this.pageArgument = pageArgument;
            }
            if (documentArgument == null) {
                this.documentArgument = new DocumentArgument();
            } else {
                this.documentArgument = documentArgument;
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public final File start(int procedure_id, File[] imageFiles, File destination, ImageFactoryListener listener) throws PDFFactoryException {
        try {
            if (!overwrite && destination.exists()) {
                throw new IOException("Overwrite deny");
            }
            if (listener != null) {
                listener.initializing(procedure_id);
            }

            builder.createDocument();
            builder.setOwnerPassword(this.documentArgument.ownerPassword);
            builder.setUserPassword(this.documentArgument.userPassword);
            builder.setPermission(this.documentArgument.permission);

            if (documentArgument.info != null)
                builder.setInfo(this.documentArgument.info);

            if (imageFiles != null)
                for (int i = 0; i < imageFiles.length; i++) {
                    BufferedImage bufferedImage = impl.readImage(imageFiles[i]);
                    strategy.execute(this.pageArgument, new SizeF(bufferedImage.getWidth(), bufferedImage.getHeight()));
                    int index = builder.addPage(strategy.getPageSize());
                    builder.addImage(index, bufferedImage, strategy.getImagePosition(), strategy.getImageSize());
                    if (listener != null)
                        listener.onAppend(procedure_id, imageFiles[i], i, imageFiles.length);
                }

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

    @Override
    public File start(int procedure_id, File[] imageFiles, File destination) throws PDFFactoryException {
        return start(procedure_id, imageFiles, destination, null);
    }


    @Override
    public File start(int procedure_id, File directory, FileFilter filter,
                      Comparator<File> fileSorter, File destination, ImageFactoryListener listener) throws PDFFactoryException {

        File[] files = directory.listFiles(filter);
        try {
            if (files == null) {
                throw new RuntimeException("abstract pathname does not denote a directory");
            }
            if (files.length == 0) {
                throw new RuntimeException("No image files is found");
            }
            if (fileSorter != null)
                Arrays.sort(files, fileSorter);
        } catch (Exception e) {
            throw new PDFFactoryException(e);
        }

        return start(procedure_id, files, destination, listener);
    }

    @Override
    public File start(int procedure_id, File directory, FileFilter filter,
                      Comparator<File> fileSorter, File destination) throws PDFFactoryException {
        return start(procedure_id, directory, filter, fileSorter, destination, null);
    }

}

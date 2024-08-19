package org.vincentyeh.img2pdf.pdf.concrete.factory;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import org.vincentyeh.img2pdf.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.pdf.framework.factory.ImageReadImpl;
import org.vincentyeh.img2pdf.pdf.framework.factory.ImagePDFFactoryListener;
import org.vincentyeh.img2pdf.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.pdf.parameter.PageArgument;

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

    private final boolean allowOverwriteFile;

    private final ImageReadImpl imageReadImpl;

    private final PDFBuilder pdfBuilder;
    private final ImageScalingStrategy imageScalingStrategy;


    public DefaultImagePDFFactory(@Nullable PageArgument pageArgument,
                                  @Nullable DocumentArgument documentArgument,
                                  @NotNull ImageReadImpl imageReadImpl,
                                  @NotNull PDFBuilder pdfBuilder,
                                  boolean allowOverwriteFile) {
        this(pageArgument, documentArgument, imageReadImpl, pdfBuilder, new DefaultImageScalingStrategy(), allowOverwriteFile);
    }

    public DefaultImagePDFFactory(@Nullable PageArgument pageArgument,
                                  @Nullable DocumentArgument documentArgument,
                                  @NotNull ImageReadImpl imageReadImpl,
                                  @NotNull PDFBuilder pdfBuilder,
                                  @NotNull ImageScalingStrategy imageScalingStrategy,
                                  boolean allowOverwriteFile) {

        try {

            this.imageReadImpl = Objects.requireNonNull(imageReadImpl, "impl==null");
            this.pdfBuilder = Objects.requireNonNull(pdfBuilder, "builder==null");
            this.imageScalingStrategy = Objects.requireNonNull(imageScalingStrategy, "strategy==null");
            this.allowOverwriteFile = allowOverwriteFile;
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

    public final File start(int procedure_id, File[] imageFiles, File destination, ImagePDFFactoryListener listener) throws PDFFactoryException {
        try {
            if (!allowOverwriteFile && destination.exists()) {
                throw new IOException("Overwrite deny");
            }

            pdfBuilder.createDocument();
            pdfBuilder.setOwnerPassword(this.documentArgument.ownerPassword);
            pdfBuilder.setUserPassword(this.documentArgument.userPassword);
            pdfBuilder.setPermission(this.documentArgument.permission);

            if (documentArgument.info != null)
                pdfBuilder.setInfo(this.documentArgument.info);

            if (imageFiles != null) {
                if (listener != null) {
                    listener.initializing(procedure_id, imageFiles.length);
                }

                for (int i = 0; i < imageFiles.length; i++) {
                    BufferedImage bufferedImage = imageReadImpl.readImage(imageFiles[i]);
                    imageScalingStrategy.execute(this.pageArgument, new SizeF(bufferedImage.getWidth(), bufferedImage.getHeight()));
                    pdfBuilder.addImage(pdfBuilder.addPage(imageScalingStrategy.getPageSize()),
                            bufferedImage, imageScalingStrategy.getImagePosition(), imageScalingStrategy.getImageSize());

                    if (listener != null)
                        listener.onAppend(procedure_id, imageFiles[i], i, imageFiles.length);
                }
            }

            if (listener != null)
                listener.onSaved(procedure_id, destination);
            pdfBuilder.save(destination);
            if (listener != null)
                listener.onConversionComplete(procedure_id);
            return destination;
        } catch (Exception e) {
            throw new PDFFactoryException(e);
        } finally {
            try {
                pdfBuilder.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public File start(int procedure_id, File[] imageFiles, File destination) throws PDFFactoryException {
        return start(procedure_id, imageFiles, destination, null);
    }


    @Override
    public File start(int procedure_id, File directory, FileFilter filter,
                      Comparator<File> fileSorter, File destination, ImagePDFFactoryListener listener) throws PDFFactoryException {

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

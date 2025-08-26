package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import org.vincentyeh.img2pdf.lib.pdf.concrete.object.PDFBoxDocumentAdaptor;
import org.vincentyeh.img2pdf.lib.pdf.concrete.object.PDFBoxPageAdaptor;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactoryListener;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageReadImpl;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.ImageScalingResult;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DefaultImagePDFFactory implements ImagePDFFactory {

    private final DocumentArgument documentArgument;
    private final PageArgument pageArgument;

    private final boolean allowOverwriteFile;

    private final ImageReadImpl imageReadImpl;

    private final ImageScalingStrategy imageScalingStrategy;

    private final ExecutorService executorService;


    public DefaultImagePDFFactory(@Nullable PageArgument pageArgument,
                                  @Nullable DocumentArgument documentArgument,
                                  @NotNull ImageReadImpl imageReadImpl,
                                  boolean allowOverwriteFile) {
        this(pageArgument, documentArgument, imageReadImpl, new DefaultImageScalingStrategy(), allowOverwriteFile);
    }

    public DefaultImagePDFFactory(@Nullable PageArgument pageArgument,
                                  @Nullable DocumentArgument documentArgument,
                                  @NotNull ImageReadImpl imageReadImpl,
                                  @NotNull ImageScalingStrategy imageScalingStrategy,
                                  boolean allowOverwriteFile) {

        try {

            this.imageReadImpl = Objects.requireNonNull(imageReadImpl, "impl==null");
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
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public final File start(int procedure_id, File[] imageFiles, File destination, ImagePDFFactoryListener listener) throws PDFFactoryException {
        try {
            if (!allowOverwriteFile && destination.exists()) {
                throw new IOException("Overwrite deny");
            }

            IDocument pdfDocument = new PDFBoxDocumentAdaptor(this.documentArgument);

            if (imageFiles != null) {
                if (listener != null) {
                    listener.initializing(procedure_id, imageFiles.length);
                }
                List<IPage> pages = new java.util.LinkedList<>();
                List<Callable<Void>> tasks = new java.util.ArrayList<>();
                for (int i = 0; i < imageFiles.length; i++) {
                    final int final_i = i;
                    Callable<Void> task = new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            BufferedImage bufferedImage = imageReadImpl.readImage(imageFiles[final_i]);
                            ImageScalingResult result = imageScalingStrategy.execute(pageArgument,
                                    new SizeF(bufferedImage.getWidth(), bufferedImage.getHeight()));
                            PDFBoxPageAdaptor page = new PDFBoxPageAdaptor(final_i + 1, result.getPageSize());

                            page.drawImage(bufferedImage, result.getImagePosition(), result.getImageSize());
                            page.render(pdfDocument);
                            pages.add(page);
                            if (listener != null)
                                listener.onAppend(procedure_id, imageFiles[final_i], page.getPageNumber(), imageFiles.length);
                            return null;
                        }
                    };
                    tasks.add(task);
                }
                List<Future<Void>> futures = executorService.invokeAll(tasks);

                for (IPage page : pages) {
                    pdfDocument.addPage(page);
                }
            }

            if (listener != null)
                listener.onSaved(procedure_id, destination);
            pdfDocument.save(destination);
            if (listener != null)
                listener.onConversionComplete(procedure_id);
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

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

}

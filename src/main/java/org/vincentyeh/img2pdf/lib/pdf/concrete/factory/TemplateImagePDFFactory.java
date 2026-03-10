package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import com.drew.lang.annotations.NotNull;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.*;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.exception.PDFFactoryException;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TemplateImagePDFFactory implements ImagePDFFactory {
    private final ImageScalingStrategy imageScalingStrategy;
    private final ExecutorService executorService;
    private final ImageReader imageReader;

    protected abstract IDocument createDocument(DocumentArgument argument);

    protected abstract IPage createPage(int pageNumber, SizeF pageSize);

    protected abstract boolean parallelProcessingSupported();

    public TemplateImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy, @NotNull ImageReader imageReader, int nThreads) {
        this.imageReader = imageReader;
        if (nThreads < 1)
            throw new IllegalArgumentException("nThreads can not be less than 1");

        try {
            this.imageScalingStrategy = Objects.requireNonNull(imageScalingStrategy, "strategy==null");
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        }

        if (!parallelProcessingSupported() && nThreads != 1)
            throw new IllegalArgumentException("This PDF factory does not support parallel processing.");

        executorService = Executors.newFixedThreadPool(nThreads);
    }

    public final IDocument start(File[] imageFiles, ColorType colorType,
                                 DocumentArgument documentArgument,
                                 PageArgument pageArgument,
                                 ImagePDFFactoryListener listener) throws PDFFactoryException {
        try {
            Objects.requireNonNull(imageFiles, "imageFiles==null");
            Objects.requireNonNull(documentArgument, "documentArgument==null");
            Objects.requireNonNull(pageArgument, "pageArgument==null");

            if (imageFiles.length == 0)
                throw new PDFFactoryException(new IllegalArgumentException("imageFiles.length==0"));


            IDocument pdfDocument = createDocument(documentArgument);
            try {
                if (listener != null) {
                    listener.initializing(imageFiles.length);
                }
                List<Callable<IPage>> tasks = new java.util.ArrayList<>();

                AtomicInteger completedCount = new AtomicInteger(0);

                for (int i = 0; i < imageFiles.length; i++) {
                    final int final_i = i;
                    checkFileState(imageFiles[final_i]);

                    Callable<IPage> task = () -> {
                        BufferedImage bufferedImage = imageReader.readImage(imageFiles[final_i], colorType);
                        ImageScalingResult result = imageScalingStrategy.execute(pageArgument,
                                new SizeF(bufferedImage.getWidth(), bufferedImage.getHeight()));

                        IPage page = createPage(final_i + 1, result.getPageSize());
                        page.drawImage(bufferedImage, result.getImagePosition(), result.getImageSize());
                        page.render(pdfDocument);
                        int done = completedCount.incrementAndGet();
                        if (listener != null)
                            listener.onAppend(imageFiles[final_i], done, imageFiles.length);
                        return page;
                    };
                    tasks.add(task);
                }
                List<Future<IPage>> futures = executorService.invokeAll(tasks);

                for (Future<IPage> future : futures) {
                    try {
                        IPage page = future.get();
                        pdfDocument.addPage(page);
                    } catch (Exception e) {
                        throw new PDFFactoryException(e);
                    }
                }

                if (listener != null)
                    listener.onConversionComplete();
                return pdfDocument;
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            throw new PDFFactoryException(e);
        }
    }

    private void checkFileState(File imageFile) throws IOException {
        if (imageFile == null)
            throw new NullPointerException("imageFile==null");
        if (!imageFile.exists())
            throw new IOException("imageFile does not exist");
        if (!imageFile.isFile())
            throw new IOException("imageFile is not file");
        if (!imageFile.canRead())
            throw new IOException("imageFile can not be read");

    }

    @Override
    public IDocument start(File[] imageFiles, ColorType colorType,
                           DocumentArgument documentArgument,
                           PageArgument pageArgument) throws PDFFactoryException {
        return start(imageFiles, colorType, documentArgument, pageArgument, null);
    }


    @Override
    public void shutdown() {
        executorService.shutdown();
    }

}

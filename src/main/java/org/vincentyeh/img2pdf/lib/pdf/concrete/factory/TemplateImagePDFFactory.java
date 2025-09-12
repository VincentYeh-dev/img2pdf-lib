package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import com.drew.lang.annotations.NotNull;
import org.vincentyeh.img2pdf.lib.image.ColorType;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactoryListener;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TemplateImagePDFFactory implements ImagePDFFactory {

    private final DocumentArgument documentArgument;
    private final PageArgument pageArgument;

    private final ImageScalingStrategy imageScalingStrategy;

    private final ExecutorService executorService;

    protected abstract IDocument createDocument(DocumentArgument argument);

    protected abstract IPage createPage(IDocument pdfDocument, int pageNumber, SizeF pageSize);

    protected abstract BufferedImage readImage(File imageFile, ColorType colorType);

    public TemplateImagePDFFactory(@NotNull PageArgument pageArgument,
                                   @NotNull DocumentArgument documentArgument,
                                   @NotNull ImageScalingStrategy imageScalingStrategy) {

        try {
            this.imageScalingStrategy = Objects.requireNonNull(imageScalingStrategy, "strategy==null");
            this.pageArgument = Objects.requireNonNull(pageArgument, "pageArgument==null");
            this.documentArgument = Objects.requireNonNull(documentArgument, "documentArgument==null");
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public final IDocument start(File[] imageFiles, ColorType colorType, ImagePDFFactoryListener listener) throws PDFFactoryException {
        try {

            IDocument pdfDocument = createDocument(this.documentArgument);

            if (imageFiles != null) {
                if (listener != null) {
                    listener.initializing(imageFiles.length);
                }
                List<IPage> pages = Collections.synchronizedList(new LinkedList<>());
                List<Callable<Void>> tasks = new java.util.ArrayList<>();

                AtomicInteger completedCount = new AtomicInteger(0);

                for (int i = 0; i < imageFiles.length; i++) {
                    final int final_i = i;
                    Callable<Void> task = () -> {
                        BufferedImage bufferedImage = readImage(imageFiles[final_i], colorType);
                        ImageScalingResult result = imageScalingStrategy.execute(pageArgument,
                                new SizeF(bufferedImage.getWidth(), bufferedImage.getHeight()));

                        IPage page = createPage(pdfDocument, final_i + 1, result.getPageSize());
                        page.drawImage(bufferedImage, result.getImagePosition(), result.getImageSize());
                        page.render();
                        pages.add(page);
                        int done = completedCount.incrementAndGet();
                        if (listener != null)
                            listener.onAppend(imageFiles[final_i], done, imageFiles.length);
                        return null;
                    };
                    tasks.add(task);
                }
                List<Future<Void>> futures = executorService.invokeAll(tasks);

                for (IPage page : pages) {
                    pdfDocument.addPage(page);
                }
            }

            if (listener != null)
                listener.onConversionComplete();
            return pdfDocument;
        } catch (Exception e) {
            throw new PDFFactoryException(e);
        }
    }

    @Override
    public IDocument start(File[] imageFiles, ColorType colorType) throws PDFFactoryException {
        return start(imageFiles, colorType, null);
    }


    @Override
    public void shutdown() {
        executorService.shutdown();
    }

}

package org.vincentyeh.img2pdf.lib.pdf.concrete.factory;

import org.vincentyeh.img2pdf.lib.annotation.NotNull;
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

/**
 * Abstract base class for {@link ImagePDFFactory} implementations that follows the
 * <em>Template Method</em> design pattern to coordinate the image-to-PDF conversion pipeline.
 *
 * <p>This class handles the common orchestration logic — validating inputs, reading images,
 * invoking the {@link ImageScalingStrategy}, and dispatching page-rendering tasks to a fixed
 * thread pool — while delegating backend-specific operations to concrete subclasses through
 * three abstract hook methods:</p>
 * <ul>
 *   <li>{@link #createDocument(DocumentArgument)} — instantiate the backend PDF document.</li>
 *   <li>{@link #createPage(int, SizeF)} — instantiate a backend-specific page object.</li>
 *   <li>{@link #parallelProcessingSupported()} — declare whether the backend is thread-safe.</li>
 * </ul>
 *
 * <p>Page rendering tasks are submitted concurrently to a {@link java.util.concurrent.ExecutorService}
 * backed by a fixed-size thread pool. When rendering is complete, pages are added to the
 * document in their original order (not completion order), ensuring correct page sequence.</p>
 *
 * <p>Callers <strong>must</strong> invoke {@link #shutdown()} after all conversions are
 * finished to release the thread pool and allow the JVM to exit cleanly.</p>
 *
 * @see OpenPDFImagePDFFactory
 */
public abstract class TemplateImagePDFFactory implements ImagePDFFactory {
    private final ImageScalingStrategy imageScalingStrategy;
    private final ExecutorService executorService;
    private final ImageReader imageReader;

    /**
     * Creates the backend-specific PDF document configured by the given argument.
     *
     * @param argument document-level settings such as metadata and encryption; never {@code null}
     * @return a new, empty {@link IDocument} ready to receive pages
     */
    protected abstract IDocument createDocument(DocumentArgument argument);

    /**
     * Creates a backend-specific page object with the given page number and dimensions.
     *
     * @param pageNumber 1-based page index within the final document
     * @param pageSize   the page dimensions in PDF user-space units (points); never {@code null}
     * @return a new {@link IPage} instance ready to receive an image
     */
    protected abstract IPage createPage(int pageNumber, SizeF pageSize);

    /**
     * Declares whether the PDF backend supports concurrent page rendering.
     *
     * <p>If {@code false} is returned, the constructor rejects any {@code nThreads} value
     * other than {@code 1}.</p>
     *
     * @return {@code true} if the backend is thread-safe and can render pages in parallel
     */
    protected abstract boolean parallelProcessingSupported();

    /**
     * Constructs a new factory with the given scaling strategy, image reader, and thread count.
     *
     * @param imageScalingStrategy the strategy used to compute page size and image placement;
     *                             must not be {@code null}
     * @param imageReader          the reader used to decode image files into
     *                             {@link java.awt.image.BufferedImage}; must not be {@code null}
     * @param nThreads             the number of worker threads for parallel page rendering;
     *                             must be at least {@code 1}; must be {@code 1} when
     *                             {@link #parallelProcessingSupported()} returns {@code false}
     * @throws IllegalArgumentException if {@code nThreads < 1}, if {@code imageScalingStrategy}
     *                                  is {@code null}, or if {@code nThreads > 1} and the
     *                                  backend does not support parallel processing
     */
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

    /**
     * Converts an ordered array of image files into a single PDF document, reporting
     * progress through the supplied listener.
     *
     * <p>The conversion pipeline for each image file is:</p>
     * <ol>
     *   <li>Validate the file (existence, readability).</li>
     *   <li>Read the image via the {@link ImageReader}.</li>
     *   <li>Compute page layout via the {@link ImageScalingStrategy}.</li>
     *   <li>Create and render the page via {@link #createPage(int, SizeF)}.</li>
     *   <li>Add the rendered page to the document in page-number order.</li>
     * </ol>
     *
     * <p>Steps 2–4 are executed in parallel across the thread pool. Pages are added to the
     * document sequentially in their original order after all tasks complete.</p>
     *
     * @param imageFiles       ordered array of image files to convert; must not be
     *                         {@code null} or empty, and each element must be a readable file
     * @param colorType        target color space applied during image reading;
     *                         {@code null} means no color-space conversion
     * @param documentArgument document-level settings (metadata, encryption); must not be {@code null}
     * @param pageArgument     page-level settings (size, alignment, direction, auto-rotation);
     *                         must not be {@code null}
     * @param listener         optional progress callback; may be {@code null}
     * @return an {@link IDocument} containing one page per input image, ready to be saved
     * @throws PDFFactoryException if any argument is invalid, if an image file cannot be read,
     *                             or if a page-rendering task fails
     */
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

    /**
     * Validates that the given file exists, is a regular file, and can be read.
     *
     * @param imageFile the file to validate; must not be {@code null}
     * @throws NullPointerException if {@code imageFile} is {@code null}
     * @throws IOException          if the file does not exist, is not a regular file,
     *                              or cannot be read
     */
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

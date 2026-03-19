package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.vincentyeh.img2pdf.lib.annotation.NotNull;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.TemplateImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;

/**
 * Apache PDFBox backend implementation of the image-to-PDF factory.
 *
 * <p>This class extends {@link TemplateImagePDFFactory} and wires it to the PDFBox
 * library by supplying concrete implementations of {@link IDocument} and {@link IPage}
 * via {@link PDFBoxDocumentAdaptor} and {@link PDFBoxPageAdaptor} respectively.</p>
 *
 * <p><strong>Memory management</strong> — PDFBox requires scratch space when
 * assembling large documents. This factory exposes three construction strategies:</p>
 * <ul>
 *   <li><em>Main-memory only</em> (default) — fastest; may exhaust heap for very large
 *       image sets.</li>
 *   <li><em>Temp-file only</em> — lower heap pressure; slower due to disk I/O.</li>
 *   <li><em>Mixed</em> — configurable split between heap and disk; suited for
 *       production workloads with predictable memory budgets.</li>
 * </ul>
 *
 * <p><strong>Parallel processing</strong> — page rendering is fully parallel.
 * {@link #parallelProcessingSupported()} returns {@code true}, so
 * {@link TemplateImagePDFFactory} will dispatch page tasks to a fixed thread pool
 * whose size is controlled by the {@code nThreads} constructor argument.</p>
 *
 * <p>After use, call {@link #shutdown()} to release the executor service's threads.</p>
 *
 * <p><strong>Typical usage:</strong></p>
 * <pre>{@code
 * PDFBoxImagePDFFactory factory = new PDFBoxImagePDFFactory(scalingStrategy, imageReader);
 * try {
 *     IDocument doc = factory.start(imageFiles, ColorType.sRGB, docArg, pageArg);
 *     doc.saveAndClose(outputFile);
 * } finally {
 *     factory.shutdown();
 * }
 * }</pre>
 */
public class PDFBoxImagePDFFactory extends TemplateImagePDFFactory {

    private final MemoryUsageSetting memoryUsageSetting;

    /**
     * Creates a factory using main-memory-only scratch storage and a thread pool sized
     * to the number of available processors.
     *
     * @param imageScalingStrategy the strategy used to compute page and image dimensions;
     *                             must not be {@code null}
     * @param imageReader          the reader used to decode source image files;
     *                             must not be {@code null}
     * @throws IllegalArgumentException if either argument is {@code null}
     */
    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy,
                                 @NotNull ImageReader imageReader) {
        this(imageScalingStrategy, imageReader, Runtime.getRuntime().availableProcessors(), true);
    }

    /**
     * Creates a factory using main-memory-only scratch storage and a fixed-size thread pool.
     *
     * @param imageScalingStrategy the strategy used to compute page and image dimensions;
     *                             must not be {@code null}
     * @param imageReader          the reader used to decode source image files;
     *                             must not be {@code null}
     * @param nThreads             the number of worker threads for parallel page rendering;
     *                             must be at least 1
     * @throws IllegalArgumentException if any argument is invalid or {@code null}
     */
    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy, @NotNull ImageReader imageReader, int nThreads) {
        this(imageScalingStrategy, imageReader, nThreads, true);
    }

    /**
     * Creates a factory with explicit control over scratch storage and thread count.
     *
     * @param imageScalingStrategy the strategy used to compute page and image dimensions;
     *                             must not be {@code null}
     * @param imageReader          the reader used to decode source image files;
     *                             must not be {@code null}
     * @param nThreads             the number of worker threads for parallel page rendering;
     *                             must be at least 1
     * @param useMemoryOnly        {@code true} to use main-memory-only scratch storage
     *                             ({@link MemoryUsageSetting#setupMainMemoryOnly()}),
     *                             {@code false} to use temp-file-only storage
     *                             ({@link MemoryUsageSetting#setupTempFileOnly()})
     * @throws IllegalArgumentException if any argument is invalid or {@code null}
     */
    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy, @NotNull ImageReader imageReader, int nThreads, boolean useMemoryOnly) {
        super(imageScalingStrategy, imageReader, nThreads);
        if (useMemoryOnly)
            this.memoryUsageSetting = MemoryUsageSetting.setupMainMemoryOnly();
        else
            this.memoryUsageSetting = MemoryUsageSetting.setupTempFileOnly();
    }

    /**
     * Creates a factory with mixed (heap + disk) scratch storage and a fixed-size thread pool.
     *
     * <p>PDFBox will use up to {@code maxMainMemoryBytes} of heap and spill the remainder
     * to a temporary file, up to {@code maxStorageBytes} total.</p>
     *
     * @param imageScalingStrategy the strategy used to compute page and image dimensions;
     *                             must not be {@code null}
     * @param imageReader          the reader used to decode source image files;
     *                             must not be {@code null}
     * @param nThreads             the number of worker threads for parallel page rendering;
     *                             must be at least 1
     * @param maxMainMemoryBytes   maximum heap bytes PDFBox may use for scratch storage;
     *                             must be a positive value
     * @param maxStorageBytes      maximum total bytes (heap + temp file) PDFBox may use;
     *                             must be greater than or equal to {@code maxMainMemoryBytes}
     * @throws IllegalArgumentException if any argument is invalid or {@code null}
     */
    public PDFBoxImagePDFFactory(@NotNull ImageScalingStrategy imageScalingStrategy,
                                 @NotNull ImageReader imageReader,
                                 int nThreads,
                                 long maxMainMemoryBytes,
                                 long maxStorageBytes) {
        super(imageScalingStrategy, imageReader, nThreads);
        this.memoryUsageSetting = MemoryUsageSetting.setupMixed(maxMainMemoryBytes, maxStorageBytes);
    }

    /**
     * Creates a {@link PDFBoxDocumentAdaptor} configured with the factory's
     * {@link MemoryUsageSetting}.
     *
     * @param argument the document configuration; must not be {@code null}
     * @return a new {@link PDFBoxDocumentAdaptor}; never {@code null}
     */
    @Override
    protected IDocument createDocument(DocumentArgument argument) {
        return new PDFBoxDocumentAdaptor(argument, memoryUsageSetting);
    }

    /**
     * Creates a {@link PDFBoxPageAdaptor} with the given page number and media-box size.
     *
     * @param pageNumber the one-based page index within the document
     * @param pageSize   the width and height of the page in PDF user-space units (points)
     * @return a new {@link PDFBoxPageAdaptor}; never {@code null}
     */
    @Override
    protected IPage createPage(int pageNumber, SizeF pageSize) {
        return new PDFBoxPageAdaptor(pageNumber, pageSize);
    }

    /**
     * Indicates that this backend supports parallel page rendering.
     *
     * @return {@code true} always; PDFBox allows concurrent content stream creation
     *         when each page uses its own {@link PDFBoxPageAdaptor} instance
     */
    @Override
    protected boolean parallelProcessingSupported() {
        return true;
    }

}

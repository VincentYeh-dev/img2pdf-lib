package org.vincentyeh.img2pdf.lib;

import org.vincentyeh.img2pdf.lib.image.concrete.reader.ImageIOReader;
import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.DefaultImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf.OpenPDFImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox.PDFBoxImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImagePDFFactory;

/**
 * Public entry point for creating {@link ImagePDFFactory} instances.
 *
 * <p>This utility class provides static factory methods that wire together the standard
 * implementations of {@link ImageReader}, {@link ImageScalingStrategy}, and the chosen PDF
 * backend. Callers should select the factory variant that best fits their memory and
 * concurrency requirements:</p>
 *
 * <ul>
 *   <li>{@link #createPDFBoxMaxPerformanceFactory()} — PDFBox backend with maximum RAM usage
 *       and an unbounded thread pool.</li>
 *   <li>{@link #createPDFBoxFactory(int, long, long)} — PDFBox backend with explicit thread
 *       count and memory limits.</li>
 *   <li>{@link #createOpenPDFFactory(int)} — OpenPDF backend with a configurable thread
 *       count.</li>
 * </ul>
 *
 * <p>After use, always call {@link ImagePDFFactory#shutdown()} on the returned factory to
 * release the underlying thread pool.</p>
 *
 * <p>This class is not instantiable.</p>
 */
public class Img2Pdf {

    private Img2Pdf() {

    }

    /**
     * Creates a PDFBox-backed factory configured for maximum performance.
     *
     * <p>Memory usage is unbounded (main memory and temporary storage are both set to their
     * maximum values) and the thread pool size matches the number of available processors.
     * Use this variant when memory consumption is not a concern and throughput is the
     * primary goal.</p>
     *
     * @return a fully configured {@link ImagePDFFactory} using the PDFBox backend
     */
    public static ImagePDFFactory createPDFBoxMaxPerformanceFactory() {
        ImageReader reader = ImageIOReader.getInstance();
        return new PDFBoxImagePDFFactory(new DefaultImageScalingStrategy(), reader);
    }

    /**
     * Creates a PDFBox-backed factory with explicit thread count and memory limits.
     *
     * <p>The {@code maxMainMemoryBytes} and {@code maxStorageBytes} parameters map directly
     * to PDFBox's {@code MemoryUsageSetting}, allowing callers to cap RAM and temporary-file
     * usage when converting large images.</p>
     *
     * @param nThreads           number of threads in the page-rendering thread pool; must be
     *                           greater than zero
     * @param maxMainMemoryBytes maximum bytes kept in main memory per document; use
     *                           {@code Long.MAX_VALUE} for unlimited
     * @param maxStorageBytes    maximum bytes written to temporary storage per document; use
     *                           {@code Long.MAX_VALUE} for unlimited
     * @return a fully configured {@link ImagePDFFactory} using the PDFBox backend
     */
    public static ImagePDFFactory createPDFBoxFactory(int nThreads, long maxMainMemoryBytes, long maxStorageBytes) {
        ImageReader reader = ImageIOReader.getInstance();
        return new PDFBoxImagePDFFactory(new DefaultImageScalingStrategy(), reader,
                nThreads, maxMainMemoryBytes, maxStorageBytes);
    }

    /**
     * Creates an OpenPDF-backed factory with the specified thread count.
     *
     * <p>OpenPDF pages are built in parallel using a fixed-size thread pool of
     * {@code nThreads} threads. This backend tends to have a lower memory footprint than
     * PDFBox for typical image-to-PDF workloads.</p>
     *
     * @param nThreads number of threads in the page-rendering thread pool; must be greater
     *                 than zero
     * @return a fully configured {@link ImagePDFFactory} using the OpenPDF backend
     */
    public static ImagePDFFactory createOpenPDFFactory(int nThreads) {
        ImageReader reader = ImageIOReader.getInstance();
        return new OpenPDFImagePDFFactory(new DefaultImageScalingStrategy(), reader, nThreads);
    }


}

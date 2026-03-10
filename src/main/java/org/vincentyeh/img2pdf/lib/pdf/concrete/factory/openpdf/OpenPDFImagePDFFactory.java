package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.openpdf;

import org.vincentyeh.img2pdf.lib.image.framework.reader.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.TemplateImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.ImageScalingStrategy;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IDocument;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;

/**
 * {@link TemplateImagePDFFactory} implementation that uses the <em>OpenPDF</em> (librepdf)
 * library as the PDF rendering backend.
 *
 * <p>This factory creates {@link OpenPDFDocumentAdaptor} and {@link OpenPDFPageAdaptor}
 * instances. OpenPDF renders each page into an independent in-memory PDF byte stream, so
 * concurrent rendering is safe — {@link #parallelProcessingSupported()} returns {@code true}
 * and the inherited thread pool may be sized to any value {@code >= 1}.</p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * ImagePDFFactory factory = new OpenPDFImagePDFFactory(
 *         new DefaultImageScalingStrategy(),
 *         ImageIOReader.getInstance(),
 *         4);
 * IDocument doc = factory.start(imageFiles, ColorType.sRGB, docArg, pageArg);
 * doc.saveAndClose(outputFile);
 * factory.shutdown();
 * }</pre>
 */
public class OpenPDFImagePDFFactory extends TemplateImagePDFFactory {

    /**
     * Constructs a new OpenPDF-backed factory.
     *
     * @param imageScalingStrategy the strategy used to compute page layout; must not be {@code null}
     * @param imageReader          the reader used to decode image files; must not be {@code null}
     * @param nThreads             number of worker threads for parallel page rendering;
     *                             must be {@code >= 1}
     * @throws IllegalArgumentException if {@code nThreads < 1} or any required argument is
     *                                  {@code null}
     */
    public OpenPDFImagePDFFactory(ImageScalingStrategy imageScalingStrategy, ImageReader imageReader, int nThreads) {
        super(imageScalingStrategy, imageReader, nThreads);
    }

    /**
     * {@inheritDoc}
     *
     * @return a new {@link OpenPDFDocumentAdaptor} configured with the given argument
     */
    @Override
    protected IDocument createDocument(DocumentArgument argument) {
        return new OpenPDFDocumentAdaptor(argument);
    }

    /**
     * {@inheritDoc}
     *
     * @return a new {@link OpenPDFPageAdaptor} for the given page number and dimensions
     */
    @Override
    protected IPage createPage(int pageNumber, SizeF pageSize) {
        return new OpenPDFPageAdaptor(pageNumber, pageSize);
    }

    /**
     * Returns {@code true} because OpenPDF renders each page into an independent
     * in-memory buffer, making concurrent page creation thread-safe.
     *
     * @return {@code true} always
     */
    @Override
    protected boolean parallelProcessingSupported() {
        return true;
    }
}

package org.vincentyeh.img2pdf.lib.pdf.concrete.factory.pdfbox;

import org.vincentyeh.img2pdf.lib.annotation.NotNull;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.IPage;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.factory.SizeF;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * PDFBox-backed implementation of {@link IPage}.
 *
 * <p>Each instance owns a private {@link PDDocument} containing a single {@link PDPage}.
 * {@link #drawImage} writes image content immediately into that document via a
 * {@link PDPageContentStream}. When the page is ready, {@link PDFBoxDocumentAdaptor#addPage}
 * calls {@link #getOwnDocument()} and merges the single-page document into the parent
 * document using {@link org.apache.pdfbox.multipdf.PDFMergerUtility}.</p>
 *
 * <p>This class is package-private and is only instantiated by
 * {@link PDFBoxImagePDFFactory}.</p>
 */
public class PDFBoxPageAdaptor implements IPage {
    private final PDPage page;
    private final int pageNumber;
    private final PDDocument ownDoc;

    /**
     * Creates a new page adaptor with the given page number and media-box size.
     *
     * @param pageNumber the one-based page number within the document; must be
     *                   greater than zero
     * @param pageSize   the width and height of the page in PDF user-space units
     *                   (points); must not be {@code null}
     * @throws IllegalArgumentException if {@code pageSize} is {@code null}
     */
    public PDFBoxPageAdaptor(int pageNumber, SizeF pageSize) {
        this.pageNumber = pageNumber;
        if (pageSize == null)
            throw new IllegalArgumentException("size==null");
        page = new PDPage();
        page.setMediaBox(new PDRectangle(pageSize.width, pageSize.height));
        ownDoc = new PDDocument();
        ownDoc.addPage(page);
    }


    /**
     * Draws an image onto this page immediately by writing to the page's content stream
     * within the private {@link PDDocument}.
     *
     * @param image         the source image to embed; must not be {@code null}
     * @param imagePosition the bottom-left position of the image in PDF user-space
     *                      units (points); must not be {@code null}
     * @param imageSize     the target width and height of the rendered image in PDF
     *                      user-space units (points); must not be {@code null}
     * @throws IllegalArgumentException if any argument is {@code null}
     * @throws RuntimeException         if a backend-level error occurs during image encoding
     *                                  or stream writing
     */
    @Override
    public void drawImage(@NotNull BufferedImage image, @NotNull PointF imagePosition, @NotNull SizeF imageSize) throws RuntimeException {
        if (imagePosition == null)
            throw new IllegalArgumentException("position==null");

        if (image == null) throw new IllegalArgumentException("image==null");

        if (imageSize == null) throw new IllegalArgumentException("size==null");

        try {
            PDImageXObject xobj = LosslessFactory.createFromImage(ownDoc, image);
            try (PDPageContentStream cs = new PDPageContentStream(ownDoc, page)) {
                cs.drawImage(xobj, imagePosition.x, imagePosition.y, imageSize.width, imageSize.height);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Returns the one-based page number assigned to this page.
     *
     * @return the page number; always greater than zero
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Returns the private {@link PDDocument} that contains this single page.
     *
     * <p>This method is intended for use by {@link PDFBoxDocumentAdaptor#addPage} when
     * merging this page into the parent document. The caller is responsible for closing
     * the returned document after the merge.</p>
     *
     * @return the internal single-page {@link PDDocument}; never {@code null}
     */
    PDDocument getOwnDocument() {
        return ownDoc;
    }

}

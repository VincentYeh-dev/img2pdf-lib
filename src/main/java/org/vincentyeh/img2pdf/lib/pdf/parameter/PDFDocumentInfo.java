package org.vincentyeh.img2pdf.lib.pdf.parameter;

/**
 * Carries standard PDF document metadata that is embedded in the PDF information dictionary.
 *
 * <p>All fields are optional; a {@code null} value means the corresponding metadata entry
 * will not be written to the output PDF.  This class is a plain data holder — set any
 * combination of fields before passing the instance to
 * {@link DocumentArgument#setInfo(PDFDocumentInfo)}.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * PDFDocumentInfo info = new PDFDocumentInfo();
 * info.Title  = "My Document";
 * info.Author = "John Doe";
 * documentArgument.setInfo(info);
 * }</pre>
 */
public final class PDFDocumentInfo {

    /**
     * The title of the document, written to the {@code /Title} entry of the PDF
     * information dictionary.
     */
    public String Title;

    /**
     * The name of the person who created the document, written to the {@code /Author} entry.
     */
    public String Author;

    /**
     * The name of the application that originally created the source content, written to
     * the {@code /Creator} entry.
     */
    public String Creator;

    /**
     * The name of the application that converted or generated the PDF, written to
     * the {@code /Producer} entry.
     */
    public String Producer;

    /**
     * The subject of the document, written to the {@code /Subject} entry.
     */
    public String Subject;

}

package org.vincentyeh.img2pdf.lib.pdf.parameter;

/**
 * Defines the access permission flags applied to an AES-encrypted PDF document.
 *
 * <p>Each boolean field corresponds to a standard PDF permission bit as specified in the
 * PDF specification (ISO 32000).  All flags default to {@code true} (fully permissive),
 * so only the flags that should be <em>restricted</em> need to be set to {@code false}.</p>
 *
 * <p>A {@code Permission} instance is supplied together with owner and user passwords via
 * {@link DocumentArgument#setEncryption(String, String, Permission)}.  The concrete PDF
 * backend (openpdf or PDFBox) translates these flags into the appropriate low-level
 * permission bits when writing the encrypted document.</p>
 *
 * <p>Example — allow printing only:</p>
 * <pre>{@code
 * Permission p = new Permission();
 * p.CanAssembleDocument    = false;
 * p.CanExtractContent      = false;
 * p.CanFillInForm          = false;
 * p.CanModify              = false;
 * p.CanModifyAnnotations   = false;
 * }</pre>
 */
public final class Permission {

    /**
     * Whether the user is allowed to assemble the document (insert, rotate, or delete pages
     * and create bookmarks or thumbnail images).
     */
    public boolean CanAssembleDocument = true;

    /**
     * Whether the user is allowed to copy or otherwise extract text and graphics from the document.
     */
    public boolean CanExtractContent = true;

    /**
     * Whether the user is allowed to extract text and graphics in support of accessibility
     * features for disabled users.
     */
    public boolean CanExtractForAccessibility = true;

    /**
     * Whether the user is allowed to fill in existing interactive form fields (including
     * signature fields).
     */
    public boolean CanFillInForm = true;

    /**
     * Whether the user is allowed to modify the document (other than operations covered by
     * {@link #CanModifyAnnotations}, {@link #CanFillInForm}, and {@link #CanAssembleDocument}).
     */
    public boolean CanModify = true;

    /**
     * Whether the user is allowed to add or modify text annotations and fill in interactive
     * form fields.  If {@link #CanModify} is also {@code true}, the user may also create
     * or modify interactive form fields.
     */
    public boolean CanModifyAnnotations = true;

    /**
     * Whether the user is allowed to print the document at full resolution.
     */
    public boolean CanPrint = true;

    /**
     * Whether the user is allowed to print the document at a degraded (lower) quality level,
     * typically used when high-quality printing is not permitted.
     */
    public boolean CanPrintDegraded = true;
}

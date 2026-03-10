package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.vincentyeh.img2pdf.lib.annotation.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Aggregates document-level parameters for a generated PDF, including optional metadata
 * and optional AES encryption settings.
 *
 * <p>By default, a newly constructed {@code DocumentArgument} has no metadata and no
 * encryption.  Metadata can be attached via {@link #setInfo(PDFDocumentInfo)}, and
 * AES encryption (owner password, user password, and permission flags) can be configured
 * via {@link #setEncryption(String, String, Permission)}.  Encryption can be removed at
 * any time by calling {@link #resetEncryption()}.</p>
 *
 * <p>This class is consumed by {@code ImagePDFFactory.start()} implementations, which
 * apply the settings to the underlying PDF backend (openpdf or PDFBox).</p>
 */
public class DocumentArgument {

    /** Whether AES encryption is currently active for this document. */
    boolean encrypted = false;

    /** The owner password used for AES encryption; {@code null} when not encrypted. */
    private String ownerPassword = null;

    /** The user password used for AES encryption; {@code null} when not encrypted. */
    private String userPassword = null;

    /** The permission flags applied when encryption is active; {@code null} when not encrypted. */
    private Permission permission = null;

    /** Optional document metadata (title, author, etc.). */
    private Optional<PDFDocumentInfo> info = Optional.empty();

    /**
     * Returns whether document metadata has been set.
     *
     * @return {@code true} if {@link #setInfo(PDFDocumentInfo)} has been called with a valid value
     */
    public boolean hasInfo() {
        return info.isPresent();
    }

    /**
     * Sets the document metadata.
     *
     * @param info the {@link PDFDocumentInfo} to attach; must not be {@code null}
     * @throws IllegalArgumentException if {@code info} is {@code null}
     */
    public void setInfo(@NotNull PDFDocumentInfo info) {
        if (info == null)
            throw new IllegalArgumentException("info==null");
        this.info = Optional.of(info);
    }

    /**
     * Returns the document metadata.
     *
     * @return the {@link PDFDocumentInfo} previously set via {@link #setInfo(PDFDocumentInfo)}
     * @throws IllegalStateException if no metadata has been set
     */
    public PDFDocumentInfo getInfo() {
        if (!info.isPresent())
            throw new IllegalStateException("info is not set");
        return info.get();
    }

    /**
     * Configures AES encryption for the document.
     *
     * <p>Both passwords must be non-null and non-empty.  After this call,
     * {@link #isEncrypted()} returns {@code true} and the password / permission
     * getters become available.</p>
     *
     * @param ownerPassword the owner (administrator) password; must not be {@code null} or empty
     * @param userPassword  the user (reader) password; must not be {@code null} or empty
     * @param permission    the access permissions granted to the user; must not be {@code null}
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if either password is empty
     */
    public void setEncryption(@NotNull String ownerPassword, @NotNull String userPassword, @NotNull Permission permission) {
        Objects.requireNonNull(ownerPassword, "ownerPassword==null");
        Objects.requireNonNull(userPassword, "userPassword==null");
        Objects.requireNonNull(permission, "permission==null");

        if (ownerPassword.isEmpty())
            throw new IllegalArgumentException("ownerPassword can not be empty");

        if (userPassword.isEmpty())
            throw new IllegalArgumentException("userPassword can not be empty");

        this.ownerPassword = ownerPassword;
        this.userPassword = userPassword;
        this.permission = permission;

        encrypted = true;
    }

    /**
     * Returns whether AES encryption is currently configured for this document.
     *
     * @return {@code true} if {@link #setEncryption(String, String, Permission)} has been called
     *         and {@link #resetEncryption()} has not been called afterwards
     */
    public boolean isEncrypted() {
        return encrypted;
    }

    /**
     * Clears all encryption settings, reverting the document to an unencrypted state.
     * After this call, {@link #isEncrypted()} returns {@code false}.
     */
    public void resetEncryption() {
        encrypted = false;
        ownerPassword = null;
        userPassword = null;
        permission = null;
    }

    /**
     * Returns the permission flags associated with the current encryption configuration.
     *
     * @return the {@link Permission} object
     * @throws IllegalStateException if the document is not encrypted
     */
    public Permission getPermission() {
        if (!encrypted)
            throw new IllegalStateException("Document is not encrypted, permission is not set");
        return permission;
    }

    /**
     * Returns the owner password of the current encryption configuration.
     *
     * @return the owner password string
     * @throws IllegalStateException if the document is not encrypted
     */
    public String getOwnerPassword() {
        if (!encrypted)
            throw new IllegalStateException("Document is not encrypted, ownerPassword is not set");
        return ownerPassword;
    }

    /**
     * Returns the user password of the current encryption configuration.
     *
     * @return the user password string
     * @throws IllegalStateException if the document is not encrypted
     */
    public String getUserPassword() {
        if (!encrypted)
            throw new IllegalStateException("Document is not encrypted, userPassword is not set");
        return userPassword;
    }
}

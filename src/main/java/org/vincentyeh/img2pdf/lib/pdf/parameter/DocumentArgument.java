package org.vincentyeh.img2pdf.lib.pdf.parameter;

import org.vincentyeh.img2pdf.lib.annotation.NotNull;

import java.util.Objects;
import java.util.Optional;

public class DocumentArgument {
    boolean encrypted = false;
    private String ownerPassword = null;
    private String userPassword = null;
    private Permission permission = null;
    private Optional<PDFDocumentInfo> info = Optional.empty();


    public boolean hasInfo() {
        return info.isPresent();
    }

    public void setInfo(@NotNull PDFDocumentInfo info) {
        if (info == null)
            throw new IllegalArgumentException("info==null");
        this.info = Optional.of(info);
    }


    public PDFDocumentInfo getInfo() {
        if (!info.isPresent())
            throw new IllegalStateException("info is not set");
        return info.get();
    }

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

    public boolean isEncrypted() {
        return encrypted;
    }
    public void resetEncryption() {
        encrypted = false;
        ownerPassword = null;
        userPassword = null;
        permission = null;
    }

    public Permission getPermission() {
        if (!encrypted)
            throw new IllegalStateException("Document is not encrypted, permission is not set");
        return permission;
    }

    public String getOwnerPassword() {
        if (!encrypted)
            throw new IllegalStateException("Document is not encrypted, ownerPassword is not set");
        return ownerPassword;
    }

    public String getUserPassword() {
        if (!encrypted)
            throw new IllegalStateException("Document is not encrypted, userPassword is not set");
        return userPassword;
    }
}

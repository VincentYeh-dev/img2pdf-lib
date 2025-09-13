package org.vincentyeh.img2pdf.lib.pdf.parameter;

import com.drew.lang.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class DocumentArgument {
    private Permission permission;
    private Optional<String> ownerPassword = Optional.empty();
    private Optional<String> userPassword = Optional.empty();
    private Optional<PDFDocumentInfo> info = Optional.empty();

    public DocumentArgument(Permission permission) {
        this.permission = permission;
    }

    public DocumentArgument() {
        this(new Permission());
    }

    public void setInfo(@NotNull PDFDocumentInfo info) {
        if(info==null)
            throw new IllegalArgumentException("info==null");
        this.info = Optional.of(info);
    }

    public boolean hasInfo() {
        return info.isPresent();
    }

    public PDFDocumentInfo getInfo() {
        if (!info.isPresent())
            throw new IllegalStateException("info is not set");
        return info.get();
    }

    public void setOwnerPassword(@NotNull String ownerPassword) {
        Objects.requireNonNull(ownerPassword, "ownerPassword==null");
        if(ownerPassword.isEmpty())
            throw new IllegalArgumentException("ownerPassword can not be empty");
        this.ownerPassword = Optional.of(ownerPassword);
    }

    public boolean hasOwnerPassword() {
        return ownerPassword.isPresent();
    }

    public void setUserPassword(@NotNull String userPassword) {
        Objects.requireNonNull(userPassword, "userPassword==null");
        if(userPassword.isEmpty())
            throw new IllegalArgumentException("userPassword can not be empty");
        this.userPassword = Optional.of(userPassword);
    }

    public boolean hasUserPassword() {
        return userPassword.isPresent();
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }

    public String getOwnerPassword() {
        if (!ownerPassword.isPresent())
            throw new IllegalStateException("ownerPassword is not set");
        return ownerPassword.get();
    }

    public String getUserPassword() {
        if (!userPassword.isPresent())
            throw new IllegalStateException("userPassword is not set");
        return userPassword.get();
    }
}

package org.vincentyeh.img2pdf.pdf.parameter;

public class DocumentArgument {
    public final String ownerPassword;
    public final String userPassword;
    public final Permission permission;
    public final PDFDocumentInfo info;

    public DocumentArgument(String ownerPassword, String userPassword, Permission permission, PDFDocumentInfo info) {

        this.ownerPassword = ownerPassword;
        this.userPassword = userPassword;
        this.permission = permission;
        this.info = info;
    }

    public DocumentArgument(String ownerPassword, String userPassword, Permission permission) {
        this(ownerPassword, userPassword, permission, null);
    }

    public DocumentArgument(Permission permission) {
        this(null, null, permission, null);
    }

    public DocumentArgument(String ownerPassword, String userPassword) {
        this(ownerPassword, userPassword, new Permission());
    }

    public DocumentArgument() {
        this(new Permission());
    }

}

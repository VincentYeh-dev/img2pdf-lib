package org.vincentyeh.IMG2PDF.lib.pdf.parameter;

public final class DocumentArgument {

    private final String ownerPassword;
    private final String userPassword;
    private final Permission permission;
    private final PDFDocumentInfo info;

    public DocumentArgument(Permission permission,String ownerPassword, String userPassword, PDFDocumentInfo info){
        this.ownerPassword = ownerPassword;
        this.userPassword = userPassword;
        this.permission = permission;
        this.info=info;
    }

    public DocumentArgument(Permission permission,String ownerPassword, String userPassword){
        this(permission,ownerPassword,userPassword,null);
    }

    public DocumentArgument(Permission permission){
        this(permission,null,null,null);
    }
    public DocumentArgument(String ownerPassword, String userPassword){
        this(new Permission(),ownerPassword,userPassword,null);
    }
    public DocumentArgument(){
        this(new Permission());
    }

    public String getOwnerPassword() {
        return ownerPassword;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public Permission getPermission() {
        return permission;
    }

    public PDFDocumentInfo getInformation() {
        return info;
    }

}

package org.vincentyeh.img2pdf.lib.pdf.parameter;

public record DocumentArgument(
        String ownerPassword,
        String userPassword,
        Permission permission,
        PDFDocumentInfo info) {

    public DocumentArgument(String ownerPassword, String userPassword,Permission permission){
        this(ownerPassword,userPassword,permission,null);
    }

    public DocumentArgument(Permission permission){
        this(null,null,permission,null);
    }
    public DocumentArgument(String ownerPassword, String userPassword){
        this(ownerPassword,userPassword,new Permission());
    }
    public DocumentArgument(){
        this(new Permission());
    }

}

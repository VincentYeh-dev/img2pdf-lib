package org.vincentyeh.IMG2PDF.pdf.doc;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.jdom2.Element;

import java.io.File;

public class DocumentArgument {
    private final String pdf_owner_password;
    private final String pdf_user_password;
    private final AccessPermission ap;
    private final File pdf_destination;

    public DocumentArgument(Element element) {

        Element permission = element.getChild("permission");
        Element owner = permission.getChild("OWNER-PASSWORD");
        Element user = permission.getChild("USER-PASSWORD");
        this.ap = new AccessPermission();
        this.ap.setCanPrint(Boolean.parseBoolean(permission.getAttributeValue("canPrint")));
        this.ap.setCanModify(Boolean.parseBoolean(permission.getAttributeValue("canModify")));

        this.pdf_user_password = user.getValue();
        this.pdf_owner_password =  owner.getValue();
        this.pdf_destination=new File(element.getChild("destination").getValue());

    }

    public DocumentArgument(String pdf_owner_password, String pdf_user_password, DocumentAccessPermission ap,File pdf_destination) {
        this.pdf_owner_password = pdf_owner_password;
        this.pdf_user_password = pdf_user_password;
        this.ap = ap;
        this.pdf_destination=pdf_destination;
    }

    public Element toElement(){
        Element element = new Element("DocumentArgument");

        element.addContent(new Element("destination").addContent(pdf_destination.getAbsolutePath()));
        Element permission = new Element("permission");
        permission.setAttribute("canPrint", String.valueOf(ap.canPrint()));
        permission.setAttribute("canModify", String.valueOf(ap.canModify()));
        permission.addContent(new Element("OWNER-PASSWORD").addContent(pdf_owner_password));
        permission.addContent(new Element("USER-PASSWORD").addContent(pdf_user_password));
        element.addContent(permission);
        return element;

    }

    public AccessPermission getPermission() {
        return ap;
    }

    public StandardProtectionPolicy getSpp() {
        return createProtectionPolicy(pdf_owner_password,pdf_user_password,ap);
    }

    public String getPdf_owner_password() {
        return pdf_owner_password;
    }

    public String getPdf_user_password() {
        return pdf_user_password;
    }

    public File getPdf_destination() {
        return pdf_destination;
    }

    private StandardProtectionPolicy createProtectionPolicy(String owner_pwd, String user_pwd, AccessPermission ap) {

        // Define the length of the encryption key.
        // Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
        int keyLength = 128;
        StandardProtectionPolicy spp = new StandardProtectionPolicy(owner_pwd, user_pwd, ap);
        spp.setEncryptionKeyLength(keyLength);
        return spp;
    }

}

package org.vincentyeh.IMG2PDF.pdf.doc;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.jdom2.Element;

public class DocumentArgument {
    private final String owner_password;
    private final String user_password;
    private final AccessPermission ap;

    public DocumentArgument(Element element) {

        Element permission = element.getChild("permission");
        Element owner = permission.getChild("OWNER-PASSWORD");
        Element user = permission.getChild("USER-PASSWORD");
        this.ap = new AccessPermission();
        this.ap.setCanPrint(Boolean.parseBoolean(permission.getAttributeValue("canPrint")));
        this.ap.setCanModify(Boolean.parseBoolean(permission.getAttributeValue("canModify")));

        this.user_password = user.getValue();
        this.owner_password =  owner.getValue();

    }

    public DocumentArgument(String owner_password, String user_password, AccessPermission ap) {
        this.owner_password = owner_password;
        this.user_password = user_password;
        this.ap = ap;
    }

    public Element toElement(){
        Element element = new Element("DocumentArgument");

        Element permission = new Element("permission");
        permission.setAttribute("canPrint", String.valueOf(ap.canPrint()));
        permission.setAttribute("canModify", String.valueOf(ap.canModify()));
        permission.addContent(new Element("OWNER-PASSWORD").addContent(owner_password));
        permission.addContent(new Element("USER-PASSWORD").addContent(user_password));
        element.addContent(permission);
        return element;

    }

    public StandardProtectionPolicy getSpp() {
        return createProtectionPolicy(owner_password,user_password,ap);
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

package org.vincentyeh.img2pdf.lib.pdf.framework.builder;

import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.lib.pdf.parameter.Permission;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface PDFBuilder {

    void createDocument();

    void setOwnerPassword(String ownerPassword);

    void setUserPassword(String userPassword);

    void setInfo(PDFDocumentInfo info);

    void setPermission(Permission permission);
    int addPage(SizeF size);

    void addImage(int index, BufferedImage image, PointF position, SizeF size);

    void save(File destination) throws IOException;


    void reset();

}

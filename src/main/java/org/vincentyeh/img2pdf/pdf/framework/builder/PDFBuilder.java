package org.vincentyeh.img2pdf.pdf.framework.builder;

import com.drew.lang.annotations.NotNull;
import org.vincentyeh.img2pdf.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.pdf.parameter.Permission;

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

    void addImage(int index, @NotNull BufferedImage image, @NotNull PointF position, @NotNull SizeF size) throws Exception;

    void save(File destination) throws IOException;


    void reset() throws IOException;

}

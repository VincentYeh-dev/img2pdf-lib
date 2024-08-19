package org.vincentyeh.img2pdf.pdf.concrete.builder;

import org.vincentyeh.img2pdf.pdf.framework.builder.PDFBuilder;
import org.vincentyeh.img2pdf.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.pdf.parameter.PDFDocumentInfo;
import org.vincentyeh.img2pdf.pdf.parameter.Permission;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class TextPDFBuilder implements PDFBuilder {

    private StringBuffer buffer;
    int page_count;

    public TextPDFBuilder(){
    }

    @Override
    public void createDocument() {
        reset();
    }

    @Override
    public void setOwnerPassword(String ownerPassword) {
        buffer.append("setOwnerPassword ").append(ownerPassword);
        buffer.append("\n");
    }

    @Override
    public void setUserPassword(String userPassword) {
        buffer.append("setUserPassword ").append(userPassword);
        buffer.append("\n");
    }

    @Override
    public void setInfo(PDFDocumentInfo info) {
        buffer.append("Title:").append(info.Title);
        buffer.append("\n");
        buffer.append("Author:").append(info.Author);
        buffer.append("\n");
        buffer.append("Creator:").append(info.Creator);
        buffer.append("\n");
        buffer.append("Producer:").append(info.Producer);
        buffer.append("\n");
        buffer.append("Subject:").append(info.Subject);
        buffer.append("\n");
    }

    @Override
    public void setPermission(Permission permission) {

    }

    @Override
    public int addPage(SizeF size) {
        buffer.append("addPage Size:").append(size.toString());
        buffer.append("\n");
        return page_count++;
    }

    @Override
    public void addImage(int index, BufferedImage image, PointF position, SizeF size) {
        buffer.append("addImage ").append("Index:").append(index).append("\tSize:").append(size).append("\tPosition:").append(position);
        buffer.append("\n");
    }

    @Override
    public void save(File destination) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destination), StandardCharsets.UTF_8));
        writer.write(buffer.toString());
        writer.close();
    }

    @Override
    public void reset() {
        this.buffer = new StringBuffer();
        page_count = 0;
    }


}

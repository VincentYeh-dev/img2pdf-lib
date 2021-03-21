package org.vincentyeh.IMG2PDF.task;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Test;
import org.vincentyeh.IMG2PDF.file.ImgFile;
import org.vincentyeh.IMG2PDF.pdf.document.DocumentAccessPermission;
import org.vincentyeh.IMG2PDF.pdf.page.PageAlign;
import org.vincentyeh.IMG2PDF.pdf.page.PageDirection;
import org.vincentyeh.IMG2PDF.pdf.page.PageSize;
import org.vincentyeh.IMG2PDF.util.NameFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class TaskTester {
    @Test(expected = IllegalArgumentException.class)
    public void initialize_by_null_args() throws FileNotFoundException {
        HashMap<String,Object> conf=null;
        Task task=new Task(conf,null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void initialize_by_empty_map() throws FileNotFoundException {
        HashMap<String,Object> conf=new HashMap<>();
        Task task=new Task(conf,null);
    }

    @Test
    public void initialize_by_empty_imgs() throws IOException {
        HashMap<String, Object> configuration = new HashMap<>();

        File file = new File("src\\test\\resources\\").getAbsoluteFile();
        NameFormatter nf = new NameFormatter(file);

        configuration.put("pdf_permission", new DocumentAccessPermission("11"));
        configuration.put("pdf_destination", nf.format("$NAME"));
        configuration.put("pdf_align", new PageAlign("CENTER-CENTER"));
        configuration.put("pdf_size", PageSize.A4);
//        configuration.put("pdf_direction", PageDirection.Vertical);
        configuration.put("pdf_direction", PageDirection.Vertical);
        configuration.put("pdf_auto_rotate", true);

        Task task=new Task(configuration,new ImgFile[0]);
        Document doc = new Document();
        Element root = task.toElement();

        doc.setRootElement(root);
        XMLOutputter outter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        outter.setFormat(format);
        System.out.println(outter.outputString(doc));


        Task task2=new Task(root);
        Assert.assertEquals(new DocumentAccessPermission("11").toString(),task2.getDap().toString());
        Assert.assertEquals(nf.format("$NAME"),task2.getPDFDestination());
        Assert.assertEquals(new PageAlign("CENTER-CENTER").toString(),task2.getPDFAlign().toString());
        Assert.assertEquals(PageSize.A4,task2.getPDFSize());


    }

}

package org.vincentyeh.IMG2PDF.lib;

import org.vincentyeh.IMG2PDF.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.IMG2PDF.lib.pdf.parameter.PageArgument;
import org.vincentyeh.IMG2PDF.lib.pdf.parameter.PageSize;

import java.awt.color.ColorSpace;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestProgram {

    public static void main(String[] args) throws IOException {
        var tempFolder= Files.createTempDirectory("org.vincentyeh.img2pdf-lib.tmp.").toFile();
        tempFolder.deleteOnExit();
        var creator =
                PDFacade.createImagePDFConverter(
                        new PageArgument(PageSize.A4),
                        new DocumentArgument("1234","5678")
                        ,3*1024*1024,tempFolder, true,
                        ColorSpace.getInstance(ColorSpace.CS_sRGB) ,10);

        creator.setImages(new File("test").listFiles());
        creator.start(new File("ssss\\output.pdf"));
    }

}
